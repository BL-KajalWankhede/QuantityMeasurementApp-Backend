import { API_BASE_URL as baseUrl } from '../env'

type RequestOptions = RequestInit & {
  auth?: boolean
  _isRetry?: boolean
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { auth = true, headers, ...rest } = options
  const requestHeaders = new Headers(headers)
  if (rest.body !== undefined && !requestHeaders.has('Content-Type')) {
    requestHeaders.set('Content-Type', 'application/json')
  }
  if (auth) {
    const token = localStorage.getItem('qma_token')
    if (token) requestHeaders.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${baseUrl}${path}`, {
    ...rest,
    credentials: auth ? 'include' : 'same-origin',
    headers: requestHeaders,
  })

  // If token is expired, try to refresh and retry request
  if (response.status === 401 && auth && !options._isRetry && !path.includes('/refresh')) {
    const refreshToken = localStorage.getItem('qma_refresh_token')
    console.log("DEBUG: Refresh token in localStorage is:", refreshToken ? "PRESENT" : "MISSING (NULL)")
    if (refreshToken) {
      // get new access token
      const res = await fetch(`${baseUrl}/api/v1/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken })
      }).catch(() => null)

      if (res?.ok) {
        const data = await res.json()

        // Save the new tokens
        localStorage.setItem('qma_token', data.accessToken)
        if (data.refreshToken) localStorage.setItem('qma_refresh_token', data.refreshToken)

        // Retry the original request
        return request<T>(path, { ...options, _isRetry: true })
      }
    }
    
    // If no refresh token or refresh fails, clear tokens and force login
    localStorage.removeItem('qma_token')
    localStorage.removeItem('qma_refresh_token')
    window.location.href = '/login'
  }

  if (!response.ok) {
    const raw = await response.text().catch(() => '')
    const maybeJson = raw
      ? (() => {
        try {
          return JSON.parse(raw)
        } catch {
          return { message: raw }
        }
      })()
      : { message: response.statusText }
    const message = maybeJson?.message ?? 'Request failed'
    throw new Error(message)
  }

  if (response.status === 204 || response.status === 205) {
    return undefined as T
  }

  const contentType = response.headers.get('content-type') ?? ''
  if (!contentType.includes('application/json')) {
    return undefined as T
  }

  const raw = await response.text()
  if (!raw) {
    return undefined as T
  }

  return JSON.parse(raw) as T
}

export const apiClient = {
  get: <T>(path: string, options?: RequestOptions) =>
    request<T>(path, { ...options, method: 'GET' }),
  post: <T>(path: string, body?: unknown, options?: RequestOptions) =>
    request<T>(path, { ...options, method: 'POST', body: body === undefined ? undefined : JSON.stringify(body) }),
  patch: <T>(path: string, body: unknown, options?: RequestOptions) =>
    request<T>(path, { ...options, method: 'PATCH', body: JSON.stringify(body) }),
}

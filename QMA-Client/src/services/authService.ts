import { API_BASE_URL as baseUrl } from '../env'
import { apiClient } from './apiClient'
import type { AuthResponse, UserProfile } from '../types'

type LoginInput = { email: string; password: string }
type SignupInput = { name: string; email: string; password: string; picture?: string | null }

export const authService = {
  login: async (payload: LoginInput) => {
    const res = await apiClient.post<AuthResponse>('/api/v1/auth/login', payload)
    if (res && (res as any).accessToken) {
      localStorage.setItem('qma_token', (res as any).accessToken)
      if ((res as any).refreshToken) {
        localStorage.setItem('qma_refresh_token', (res as any).refreshToken)
      }
    }
    return res
  },
  signup: async (payload: SignupInput) => {
    const res = await apiClient.post<AuthResponse>('/api/v1/auth/signup', payload)
    if (res && (res as any).accessToken) {
      localStorage.setItem('qma_token', (res as any).accessToken)
      if ((res as any).refreshToken) {
        localStorage.setItem('qma_refresh_token', (res as any).refreshToken)
      }
    }
    return res
  },
  getSession: () => apiClient.get<UserProfile | undefined>('/api/v1/users/me'),
  logout: async () => {
    await apiClient.post<void>('/api/v1/auth/logout').catch(() => { })
    localStorage.removeItem('qma_token')
    localStorage.removeItem('qma_refresh_token')
  },
  startGoogleLogin: () => {
    localStorage.removeItem('qma_token') // Clear stale tokens
    localStorage.removeItem('qma_refresh_token')
    sessionStorage.setItem('qma_oauth_in_progress', '1')
    // Go via API Gateway so the cookie is set for the gateway origin
    window.location.assign(`${baseUrl}/oauth2/authorization/google`)
  },
}

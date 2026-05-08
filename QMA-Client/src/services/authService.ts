import { apiClient } from './apiClient'
import type { AuthResponse, UserProfile } from '../types'

type LoginInput = { email: string; password: string }
type SignupInput = { name: string; email: string; password: string; picture?: string | null }

export const authService = {
  login: async (payload: LoginInput) => {
    const res = await apiClient.post<AuthResponse>('/api/v1/auth/login', payload)
    if (res && (res as any).accessToken) localStorage.setItem('qma_token', (res as any).accessToken)
    return res
  },
  signup: async (payload: SignupInput) => {
    return apiClient.post<AuthResponse>('/api/v1/auth/signup', payload)
  },
  getSession: () => apiClient.get<UserProfile | undefined>('/api/v1/users/me'),
  logout: async () => {
    localStorage.removeItem('qma_token')
    await apiClient.post<void>('/api/v1/auth/logout').catch(() => { })
  },
  startGoogleLogin: () => {
    localStorage.removeItem('qma_token') // Clear stale tokens
    sessionStorage.setItem('qma_oauth_in_progress', '1')
    // Go via API Gateway so the cookie is set for the gateway origin
    window.location.assign('https://quantitymeasurementapp-api-5be3.onrender.com/oauth2/authorization/google')
  },
}

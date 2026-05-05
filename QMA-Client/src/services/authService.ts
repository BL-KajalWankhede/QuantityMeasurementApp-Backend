import { API_BASE_URL, apiClient } from './apiClient'
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
    const res = await apiClient.post<AuthResponse>('/api/v1/auth/signup', payload)
    if (res && (res as any).accessToken) localStorage.setItem('qma_token', (res as any).accessToken)
    return res
  },
  getSession: () => apiClient.get<UserProfile | undefined>('/api/v1/auth/me'),
  logout: async () => {
    localStorage.removeItem('qma_token')
    await apiClient.post<void>('/api/v1/auth/logout').catch(() => {})
  },
  startGoogleLogin: () => {
    sessionStorage.setItem('qma_oauth_in_progress', '1')
    const oauthUrl = `${API_BASE_URL}/oauth2/authorization/google`
    window.location.assign(oauthUrl)
  },
}

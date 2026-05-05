import { apiClient } from './apiClient'
import type { QuantityMeasurementHistory, UserProfile } from '../types'

type UpdateProfileInput = {
  name: string
  picture?: string | null
}

export const userService = {
  getMyProfile: () => apiClient.get<UserProfile>('/api/v1/users/me'),
  updateMyProfile: (payload: UpdateProfileInput) =>
    apiClient.patch<UserProfile>('/api/v1/users/me', payload),
  getMyHistory: async () => {
    const res = await apiClient.get<{ history: QuantityMeasurementHistory[] }>('/api/v1/users/me')
    return res?.history || []
  },
}

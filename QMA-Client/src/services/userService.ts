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
    const res = await apiClient.get<QuantityMeasurementHistory[] | { history: QuantityMeasurementHistory[] }>('/api/v1/users/me/history')
    // Handle both formats: direct array (C: drive) or object with history prop (D: drive)
    if (Array.isArray(res)) return res
    return res?.history || []
  },
}

import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  try {
    const token = useAuthStore.getState().auth.accessToken
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
  } catch {}
  return config
})

export interface AppUserDTO {
  id: number
  firstName?: string
  lastName?: string
  email: string
  phone?: string
  role: string
  cin: string
  address?: string
  birthDate?: string
  municipalityId?: number
}

export async function fetchAppUsers(): Promise<AppUserDTO[]> {
  const res = await api.get<AppUserDTO[]>('/api/app-users')
  return res.data
}

export type RequestStatus = 'PENDING' | 'IN_PROGRESS' | 'RESOLVED' | 'REJECTED'
export interface RequestDTO {
  id?: number
  type: string
  description: string
  status: RequestStatus
  createdDate?: string
  resolvedDate?: string
  citizenId?: number
  municipalityId?: number
}

export async function fetchRequests(): Promise<RequestDTO[]> {
  const res = await api.get<RequestDTO[]>('/api/requests')
  return res.data
}

export async function fetchMyRequests(): Promise<RequestDTO[]> {
  const res = await api.get<RequestDTO[]>('/api/requests/mine')
  return res.data
}

export async function fetchRequestById(id: number): Promise<RequestDTO> {
  const res = await api.get<RequestDTO>(`/api/requests/${id}`)
  return res.data
}

export async function createRequest(payload: RequestDTO): Promise<RequestDTO> {
  const res = await api.post<RequestDTO>('/api/requests', payload)
  return res.data
}

export async function updateRequest(id: number, payload: RequestDTO): Promise<RequestDTO> {
  const res = await api.put<RequestDTO>(`/api/requests/${id}`, { ...payload, id })
  return res.data
}

export async function deleteRequest(id: number): Promise<void> {
  await api.delete(`/api/requests/${id}`)
}

// --- Optional extensions (backend endpoints may be added later) ---
export interface RequestMessageDTO {
  id?: number
  requestId: number
  author?: string
  content: string
  createdDate?: string
}

export async function listRequestMessages(requestId: number): Promise<RequestMessageDTO[]> {
  const res = await api.get<RequestMessageDTO[]>(`/api/requests/${requestId}/messages`)
  return res.data
}

export async function createRequestMessage(requestId: number, content: string): Promise<RequestMessageDTO> {
  const res = await api.post<RequestMessageDTO>(`/api/requests/${requestId}/messages`, { content })
  return res.data
}

export interface RequestAttachmentDTO {
  id?: number
  requestId: number
  filename: string
  url?: string
  uploadedDate?: string
}

export async function listRequestAttachments(requestId: number): Promise<RequestAttachmentDTO[]> {
  const res = await api.get<RequestAttachmentDTO[]>(`/api/requests/${requestId}/attachments`)
  return res.data
}

export async function uploadRequestAttachment(requestId: number, file: File): Promise<RequestAttachmentDTO> {
  const form = new FormData()
  form.append('file', file)
  const res = await api.post<RequestAttachmentDTO>(`/api/requests/${requestId}/attachments`, form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return res.data
}

export interface AppointmentDTO {
  id?: number
  requestId: number
  dateTime: string
  location?: string
  notes?: string
  status?: 'PENDING' | 'CONFIRMED' | 'CANCELLED'
}

export async function listAppointments(requestId: number): Promise<AppointmentDTO[]> {
  const res = await api.get<AppointmentDTO[]>(`/api/requests/${requestId}/appointments`)
  return res.data
}

export async function createAppointment(requestId: number, payload: Omit<AppointmentDTO, 'id' | 'requestId'>): Promise<AppointmentDTO> {
  const res = await api.post<AppointmentDTO>(`/api/requests/${requestId}/appointments`, payload)
  return res.data
}



import { api } from "./client";
import type {
  ComplaintRequest,
  ComplaintResponse,
  ComplaintUpdateRequest,
  NoticeRequest,
  NoticeResponse,
  EventRequest,
  EventResponse,
  NotificationResponse,
} from "./types";
 
export const complaintApi = {
  raise: (userId: number, data: ComplaintRequest) =>
    api
      .post<ComplaintResponse>("/complaints", data, { params: { userId } })
      .then((r) => r.data),
 
  getByUser: (userId: number) =>
    api.get<ComplaintResponse[]>(`/complaints/user/${userId}`).then((r) => r.data),
 
  getByApartment: (apartmentId: number) =>
    api
      .get<ComplaintResponse[]>(`/complaints/apartment/${apartmentId}`)
      .then((r) => r.data),
 
  update: (id: number, data: ComplaintUpdateRequest) =>
    api.put<ComplaintResponse>(`/complaints/${id}`, data).then((r) => r.data),
};
 
export const noticeApi = {
  publish: (data: NoticeRequest) =>
    api.post<NoticeResponse>("/notices", data).then((r) => r.data),
 
  getActive: (apartmentId: number) =>
    api.get<NoticeResponse[]>(`/notices/apartment/${apartmentId}`).then((r) => r.data),
 
  remove: (id: number) => api.delete(`/notices/${id}`),
};
 
export const eventApi = {
  create: (data: EventRequest) =>
    api.post<EventResponse>("/events", data).then((r) => r.data),
 
  getUpcoming: (apartmentId: number) =>
    api.get<EventResponse[]>(`/events/apartment/${apartmentId}`).then((r) => r.data),
 
  getMyRsvps: (residentId: number) =>
    api.get<number[]>(`/events/rsvps/${residentId}`).then((r) => r.data),
 
  rsvp: (eventId: number, residentId: number) =>
    api
      .post<EventResponse>(`/events/${eventId}/rsvp`, null, { params: { residentId } })
      .then((r) => r.data),
 
  withdrawRsvp: (eventId: number, residentId: number) =>
    api
      .delete<EventResponse>(`/events/${eventId}/rsvp`, { params: { residentId } })
      .then((r) => r.data),
};
 
export const notificationApi = {
  getByUser: (userId: number) =>
    api
      .get<NotificationResponse[]>(`/notifications/user/${userId}`)
      .then((r) => r.data),
 
  markRead: (id: number) =>
    api.put<NotificationResponse>(`/notifications/${id}/read`).then((r) => r.data),
};
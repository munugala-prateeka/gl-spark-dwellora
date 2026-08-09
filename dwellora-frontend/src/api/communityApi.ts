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
  raise: (data: ComplaintRequest) =>
    api
      .post<ComplaintResponse>("/complaints", data)
      .then((r) => r.data),
 
  getByUser: () =>
    api.get<ComplaintResponse[]>(`/complaints/my`).then((r) => r.data),
 
  getByApartment: () =>
    api
      .get<ComplaintResponse[]>(`/complaints`)
      .then((r) => r.data),
 
  update: (id: number, data: ComplaintUpdateRequest) =>
    api.put<ComplaintResponse>(`/complaints/${id}`, data).then((r) => r.data),
};
 
export const noticeApi = {
  publish: (data: NoticeRequest) =>
    api.post<NoticeResponse>("/notices", data).then((r) => r.data),
 
  getActive: () =>
    api.get<NoticeResponse[]>(`/notices`).then((r) => r.data),
 
  remove: (id: number) => api.delete(`/notices/${id}`),
};
 
export const eventApi = {
  create: (data: EventRequest) =>
    api.post<EventResponse>("/events", data).then((r) => r.data),
 
  getUpcoming: () =>
    api.get<EventResponse[]>(`/events`).then((r) => r.data),
 
  getMyRsvps: () =>
    api.get<number[]>(`/events/my-rsvps`).then((r) => r.data),
 
  rsvp: (eventId: number) =>
    api
      .post<EventResponse>(`/events/${eventId}/rsvp`, null)
      .then((r) => r.data),
 
  withdrawRsvp: (eventId: number) =>
    api
      .delete<EventResponse>(`/events/${eventId}/rsvp`)
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
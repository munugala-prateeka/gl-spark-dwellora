import { api } from "./client";
import type {
  AmenityRequest,
  AmenityResponse,
  AmenityType,
  BookingRequest,
  BookingResponse,
  AdminBookingResponse,
  AvailabilitySlot,
} from "./types";

export const amenityApi = {
  getAll: () => api.get<AmenityResponse[]>("/amenities").then((r) => r.data),

  getById: (id: number) => api.get<AmenityResponse>(`/amenities/${id}`).then((r) => r.data),

  getByApartment: (apartmentId: number) =>
    api.get<AmenityResponse[]>(`/amenities/apartment/${apartmentId}`).then((r) => r.data),

  getTypes: () => api.get<AmenityType[]>("/amenities/types").then((r) => r.data),

  add: (data: AmenityRequest) =>
    api.post<AmenityResponse>("/amenities", data).then((r) => r.data),

  update: (id: number, data: AmenityRequest) =>
    api.put<AmenityResponse>(`/amenities/${id}`, data).then((r) => r.data),

  remove: (id: number) => api.delete(`/amenities/${id}`),
};

export const bookingApi = {
  getAll: () => api.get<BookingResponse[]>("/bookings").then((r) => r.data),

  getById: (id: number) => api.get<BookingResponse>(`/bookings/${id}`).then((r) => r.data),

  getByUser: (userId: number) =>
    api.get<BookingResponse[]>(`/bookings/user/${userId}`).then((r) => r.data),

  add: (data: BookingRequest) =>
    api.post<BookingResponse>("/bookings", data).then((r) => r.data),

  cancel: (bookingId: number) =>
    api.put<BookingResponse>(`/bookings/cancel/${bookingId}`).then((r) => r.data),

  getAvailability: (amenityId: number, bookingDate: string) =>
    api
      .get<AvailabilitySlot[]>(`/bookings/availability/${amenityId}`, {
        params: { bookingDate },
      })
      .then((r) => r.data),

  getByApartment: (apartmentId: number) =>
    api
      .get<AdminBookingResponse[]>(`/bookings/apartment/${apartmentId}`)
      .then((r) => r.data),

  getTodayCount: (apartmentId: number) =>
    api
      .get<number>(`/bookings/apartment/${apartmentId}/today/count`)
      .then((r) => r.data),
};

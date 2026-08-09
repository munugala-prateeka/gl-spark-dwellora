import { api } from "./client";

import type {
  BookingRequest,
  BookingResponse,
  AdminBookingResponse,
  AvailabilitySlot,
} from "./types";

export const bookingApi = {

  // ---------- MANAGER ----------
  // GET /bookings
  getAll: () =>
    api
      .get<BookingResponse[]>("/bookings")
      .then((r) => r.data),


  // ---------- MANAGER ----------
  // GET /bookings/{id}
  getById: (id: number) =>
    api
      .get<BookingResponse>(`/bookings/${id}`)
      .then((r) => r.data),


  // ---------- MANAGER ----------
  // GET /bookings/date?bookingDate=2026-08-10
  getByDate: (bookingDate: string) =>
    api
      .get<BookingResponse[]>("/bookings/date", {
        params: { bookingDate },
      })
      .then((r) => r.data),


  // ---------- RESIDENT ----------
  // GET /bookings/my
  //
  // X-User-Id is taken from the logged-in user
  // by the API interceptor.
  getMyBookings: () =>
    api
      .get<BookingResponse[]>("/bookings/my")
      .then((r) => r.data),


  // ---------- RESIDENT ----------
  // POST /bookings
  //
  // userId is NOT sent in the body.
  // Backend gets it from X-User-Id.
  add: (data: BookingRequest) =>
    api
      .post<BookingResponse>("/bookings", data)
      .then((r) => r.data),


  // ---------- RESIDENT ----------
  // PUT /bookings/cancel/{bookingId}
  //
  // X-User-Id is taken from the logged-in user
  // by the API interceptor.
  cancel: (bookingId: number) =>
    api
      .put<BookingResponse>(`/bookings/cancel/${bookingId}`)
      .then((r) => r.data),


  // ---------- RESIDENT ----------
  // GET /bookings/availability/{amenityId}?bookingDate=...
  getAvailability: (
    amenityId: number,
    bookingDate: string
  ) =>
    api
      .get<AvailabilitySlot[]>(
        `/bookings/availability/${amenityId}`,
        {
          params: { bookingDate },
        }
      )
      .then((r) => r.data),


  // ---------- MANAGER ----------
  // GET /bookings/apartment/{apartmentId}
  getByApartment: (apartmentId: number) =>
    api
      .get<AdminBookingResponse[]>(
        `/bookings/apartment/${apartmentId}`
      )
      .then((r) => r.data),


  // ---------- MANAGER ----------
  // GET /bookings/apartment/{apartmentId}/today/count
  getTodayCount: (apartmentId: number) =>
    api
      .get<number>(
        `/bookings/apartment/${apartmentId}/today/count`
      )
      .then((r) => r.data),
};


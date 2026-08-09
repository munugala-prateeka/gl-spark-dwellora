import { api } from "./client";

import type {
  LoginRequest,
  LoginResponse,
  ActivateAccountRequest,
  UserResponse,
  ResidentRequest,
  UserUpdateRequest,
} from "./types";

export const userApi = {

  // Public
  login: (data: LoginRequest) =>
    api
      .post<LoginResponse>("/users/login", data)
      .then((r) => r.data),

  // Public
  activate: (data: ActivateAccountRequest) =>
    api
      .post<LoginResponse>("/users/activate", data)
      .then((r) => r.data),

  // Public according to your current backend SecurityConfig
  getById: (id: number) =>
    api
      .get<UserResponse>(`/users/${id}`)
      .then((r) => r.data),

  // PLATFORM_ADMIN only
  getAll: () =>
    api
      .get<UserResponse[]>("/users")
      .then((r) => r.data),

  // MANAGER only
  // Apartment ID comes from JWT -> Gateway -> X-Apartment-Id
  createResident: (data: ResidentRequest) =>
    api
      .post<UserResponse>("/users/residents", data)
      .then((r) => r.data),

  // MANAGER only
  // No apartmentId in URL.
  // Gateway supplies X-Apartment-Id.
  getResidentsByApartment: () =>
    api
      .get<UserResponse[]>("/users/residents")
      .then((r) => r.data),

  // MANAGER only
  // Apartment ID is supplied by Gateway.
  updateResident: (
    id: number,
    data: UserUpdateRequest
  ) =>
    api
      .put<UserResponse>(`/users/${id}`, data)
      .then((r) => r.data),

  // MANAGER only
  deleteResident: (id: number) =>
    api.delete(`/users/${id}`),
};
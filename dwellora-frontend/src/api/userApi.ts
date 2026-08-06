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
  login: (data: LoginRequest) =>
    api.post<LoginResponse>("/users/login", data).then((r) => r.data),

  activate: (data: ActivateAccountRequest) =>
    api.post<LoginResponse>("/users/activate", data).then((r) => r.data),

  getById: (id: number) => api.get<UserResponse>(`/users/${id}`).then((r) => r.data),

  getAll: () => api.get<UserResponse[]>("/users").then((r) => r.data),

  createResident: (data: ResidentRequest) =>
    api.post<UserResponse>("/users/residents", data).then((r) => r.data),

  getResidentsByApartment: (apartmentId: number) =>
    api
      .get<UserResponse[]>(`/users/apartment/${apartmentId}/residents`)
      .then((r) => r.data),

  updateResident: (id: number, data: UserUpdateRequest) =>
    api.put<UserResponse>(`/users/${id}`, data).then((r) => r.data),

  deleteResident: (id: number) => api.delete(`/users/${id}`),
};

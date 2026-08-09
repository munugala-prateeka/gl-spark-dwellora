import { api } from "./client";
import type {
  AmenityRequest,
  AmenityResponse,
  AmenityType,
} from "./types";


export const amenityApi = {
  // MANAGER
  add: (data: AmenityRequest) =>
    api
      .post<AmenityResponse>("/amenities", data)
      .then((r) => r.data),

  // ALL AUTHENTICATED USERS
  getAll: () =>
    api
      .get<AmenityResponse[]>("/amenities")
      .then((r) => r.data),

  // ALL AUTHENTICATED USERS
  getById: (id: number) =>
    api
      .get<AmenityResponse>(`/amenities/${id}`)
      .then((r) => r.data),

  // MANAGER
  update: (id: number, data: AmenityRequest) =>
    api
      .put<AmenityResponse>(`/amenities/${id}`, data)
      .then((r) => r.data),

  // MANAGER
  remove: (id: number) =>
    api.delete(`/amenities/${id}`),
};



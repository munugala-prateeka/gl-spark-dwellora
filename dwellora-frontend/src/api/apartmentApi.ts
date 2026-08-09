import { api } from "./client";

import type { ApartmentResponse } from "./types";

export const apartmentApi = {

  // Protected by backend security
  getAll: () =>
    api
      .get<ApartmentResponse[]>("/apartments")
      .then((r) => r.data),

  getById: (id: number) =>
    api
      .get<ApartmentResponse>(`/apartments/${id}`)
      .then((r) => r.data),
};
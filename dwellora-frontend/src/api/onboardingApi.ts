import { api } from "./client";
import type { OnboardingRequest, OnboardingResponse, ApartmentResponse } from "./types";

export const onboardingApi = {
  submitRequest: (data: OnboardingRequest) =>
    api.post<OnboardingResponse>("/onboarding/request", data).then((r) => r.data),

  getPending: () =>
    api.get<OnboardingResponse[]>("/onboarding/pending").then((r) => r.data),

  getAll: () =>
    api.get<OnboardingResponse[]>("/onboarding").then((r) => r.data),

  approve: (id: number) =>
    api.put<OnboardingResponse>(`/onboarding/${id}/approve`).then((r) => r.data),

  reject: (id: number) =>
    api.put<OnboardingResponse>(`/onboarding/${id}/reject`).then((r) => r.data),
};

export const apartmentApi = {
  getAll: () => api.get<ApartmentResponse[]>("/apartments").then((r) => r.data),

  getById: (id: number) => api.get<ApartmentResponse>(`/apartments/${id}`).then((r) => r.data),
};
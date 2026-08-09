import { api } from "./client";

import type {
  OnboardingRequest,
  OnboardingResponse,
} from "./types";

export const onboardingApi = {

  // Public
  submitRequest: (data: OnboardingRequest) =>
    api
      .post<OnboardingResponse>(
        "/onboarding/request",
        data
      )
      .then((r) => r.data),

  // PLATFORM_ADMIN only
  getPending: () =>
    api
      .get<OnboardingResponse[]>("/onboarding/pending")
      .then((r) => r.data),

  // PLATFORM_ADMIN only
  getAll: () =>
    api
      .get<OnboardingResponse[]>("/onboarding")
      .then((r) => r.data),

  // PLATFORM_ADMIN only
  approve: (id: number) =>
    api
      .put<OnboardingResponse>(
        `/onboarding/${id}/approve`
      )
      .then((r) => r.data),

  // PLATFORM_ADMIN only
  reject: (id: number) =>
    api
      .put<OnboardingResponse>(
        `/onboarding/${id}/reject`
      )
      .then((r) => r.data),
};
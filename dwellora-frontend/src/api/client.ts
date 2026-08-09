import axios from "axios";

export const GATEWAY_URL = "http://localhost:8769";

export const api = axios.create({
  baseURL: GATEWAY_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Attach authentication information to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("dwellora_token");
  const rawUser = localStorage.getItem("dwellora_user");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  if (rawUser) {
    try {
      const user = JSON.parse(rawUser);

      if (user.userId != null) {
        config.headers["X-User-Id"] = String(user.userId);
      }

      if (user.role) {
        config.headers["X-User-Role"] = user.role;
      }

      if (user.apartmentId != null) {
        config.headers["X-Apartment-Id"] = String(user.apartmentId);
      }
    } catch (error) {
      console.error("Could not parse stored user:", error);
    }
  }

  return config;
});

// Handle authentication failures
api.interceptors.response.use(
  (response) => response,

  (error) => {
    const status = error?.response?.status;

    if (status === 401) {
      localStorage.removeItem("dwellora_token");
      localStorage.removeItem("dwellora_user");

      if (!window.location.pathname.startsWith("/login")) {
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);


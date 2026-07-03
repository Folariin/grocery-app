import axios from "axios";

const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL;

if (import.meta.env.PROD && !configuredBaseUrl) {
  throw new Error("VITE_API_BASE_URL must be set for production builds.");
}

const api = axios.create({
  baseURL: configuredBaseUrl || "http://localhost:8080",
});

// Attach token automatically if present
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;

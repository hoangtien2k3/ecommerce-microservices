import axios, { AxiosError, InternalAxiosRequestConfig } from "axios";

const API_BASE_URL = "";

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
  timeout: 10000,
});

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  if (typeof window !== "undefined") {
    const token = localStorage.getItem("access_token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const refreshToken = localStorage.getItem("refresh_token");
        if (refreshToken) {
          const { data } = await axios.post(`${API_BASE_URL}/api/v1/auth/refresh`, {
            refreshToken,
          });
          const newToken = data.data?.access_token;
          if (newToken) {
            localStorage.setItem("access_token", newToken);
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            return apiClient(originalRequest);
          }
        }
      } catch {
        localStorage.removeItem("access_token");
        localStorage.removeItem("refresh_token");
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authApi = {
  login: (username: string, password: string) =>
    apiClient.post("/api/v1/auth/signin", { username, password }),
  register: (data: {
    fullName: string;
    username: string;
    email: string;
    password: string;
    gender: string;
    phone?: string;
  }) => apiClient.post("/api/v1/auth/signup", data),
  logout: (refreshToken: string) =>
    apiClient.post("/api/v1/auth/logout", { refreshToken }),
  refreshToken: (refreshToken: string) =>
    apiClient.post("/api/v1/auth/refresh", { refreshToken }),
  getProfile: () => apiClient.get("/api/v1/users/me"),
};

// Product API
export const productApi = {
  getAll: (params?: { page?: number; size?: number; sort?: string; categoryId?: number }) =>
    apiClient.get("/api/products", { params }),
  getById: (id: number) => apiClient.get(`/api/products/${id}`),
  create: (data: unknown) => apiClient.post("/api/products", data),
  update: (id: number, data: unknown) => apiClient.put(`/api/products/${id}`, data),
  delete: (id: number) => apiClient.delete(`/api/products/${id}`),
};

// Category API
export const categoryApi = {
  getAll: (params?: { page?: number; size?: number }) =>
    apiClient.get("/api/categories", { params }),
  getById: (id: number) => apiClient.get(`/api/categories/${id}`),
  create: (data: unknown) => apiClient.post("/api/categories", data),
  update: (id: number, data: unknown) => apiClient.put(`/api/categories/${id}`, data),
  delete: (id: number) => apiClient.delete(`/api/categories/${id}`),
};

// Order API
export const orderApi = {
  getAll: (params?: { page?: number; size?: number }) =>
    apiClient.get("/api/orders", { params }),
  getById: (id: number) => apiClient.get(`/api/orders/${id}`),
  create: (data: unknown) => apiClient.post("/api/orders", data),
  update: (id: number, data: unknown) => apiClient.put(`/api/orders/${id}`, data),
  delete: (id: number) => apiClient.delete(`/api/orders/${id}`),
};

// Cart API
export const cartApi = {
  getAll: (params?: { page?: number; size?: number }) =>
    apiClient.get("/api/carts", { params }),
  getById: (id: number) => apiClient.get(`/api/carts/${id}`),
  create: (data: unknown) => apiClient.post("/api/carts", data),
  update: (id: number, data: unknown) => apiClient.put(`/api/carts/${id}`, data),
  delete: (id: number) => apiClient.delete(`/api/carts/${id}`),
};

// Payment API
export const paymentApi = {
  getAll: (params?: { page?: number; size?: number }) =>
    apiClient.get("/api/payments", { params }),
  getById: (id: number) => apiClient.get(`/api/payments/${id}`),
  create: (data: unknown) => apiClient.post("/api/payments", data),
};

// Favourite API
export const favouriteApi = {
  getAll: () => apiClient.get("/api/favourites"),
  add: (userId: number, productId: number) =>
    apiClient.post("/api/favourites", { userId, productId }),
  remove: (userId: number, productId: number) =>
    apiClient.delete(`/api/favourites/${userId}/${productId}`),
};

// Rating API
export const ratingApi = {
  getByProduct: (productId: number) =>
    apiClient.get(`/storefront/ratings`, { params: { productId } }),
  create: (data: unknown) => apiClient.post("/storefront/ratings", data),
};

// Inventory API
export const inventoryApi = {
  checkStock: (skuCode: string) =>
    apiClient.get(`/api/inventory`, { params: { skuCode } }),
};

// Search API
export const searchApi = {
  search: (keyword: string, params?: { page?: number; size?: number; sort?: string }) =>
    apiClient.get("/storefront/catalog-search", { params: { keyword, ...params } }),
  suggest: (keyword: string) =>
    apiClient.get("/storefront/search_suggest", { params: { keyword } }),
};

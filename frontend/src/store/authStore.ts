import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { AuthState, UserResponse } from "@/types";

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      refreshToken: null,
      isAuthenticated: false,

      setAuth: (user: UserResponse, token: string, refreshToken: string) => {
        try {
          if (typeof window !== "undefined" && typeof localStorage !== "undefined") {
            localStorage.setItem("access_token", token);
            localStorage.setItem("refresh_token", refreshToken);
          }
        } catch {}
        set({ user, token, refreshToken, isAuthenticated: true });
      },

      logout: () => {
        try {
          if (typeof window !== "undefined" && typeof localStorage !== "undefined") {
            localStorage.removeItem("access_token");
            localStorage.removeItem("refresh_token");
          }
        } catch {}
        set({ user: null, token: null, refreshToken: null, isAuthenticated: false });
      },
    }),
    { name: "ecommerce-auth" }
  )
);

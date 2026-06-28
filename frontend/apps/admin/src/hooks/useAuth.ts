import { useCallback, useRef, useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { authApi, buildSsoLoginUrl } from "@ecommerce/lib/api";
import { useAuthStore } from "@ecommerce/lib/store";
import { useRouter } from "@/i18n/navigation";

const SSO_NEXT_KEY = "sso_next";

/** Kicks off Keycloak SSO for the admin app. */
export function useLogin() {
  const login = useCallback((next?: string) => {
    if (typeof window === "undefined") return;
    if (next) sessionStorage.setItem(SSO_NEXT_KEY, next);
    window.location.href = buildSsoLoginUrl();
  }, []);

  return { login, isPending: false };
}

/** Swaps the callback ticket for tokens, loads the profile, persists the session. */
export function useSsoCallback() {
  const { setAuth } = useAuthStore();
  const router = useRouter();
  const [status, setStatus] = useState<"loading" | "error">("loading");
  const ran = useRef(false);

  const run = useCallback(
    async (ticket: string) => {
      if (ran.current) return;
      ran.current = true;

      try {
        const res = await authApi.getSession(ticket);
        const tokens = res.data?.data;
        if (!tokens?.access_token) throw new Error("missing access token");

        localStorage.setItem("access_token", tokens.access_token);

        let user = { id: 0, fullName: "", username: "", email: "", gender: "", roles: [] };
        try {
          const profileRes = await authApi.getProfile();
          if (profileRes.data?.data) user = profileRes.data.data;
        } catch {
          /* best-effort profile */
        }

        setAuth(user, tokens.access_token, tokens.refresh_token);

        const next = sessionStorage.getItem(SSO_NEXT_KEY) || "/admin/dashboard";
        sessionStorage.removeItem(SSO_NEXT_KEY);
        router.replace(next);
        router.refresh();
      } catch {
        setStatus("error");
      }
    },
    [setAuth, router]
  );

  return { run, status };
}

export function useLogout() {
  const { logout } = useAuthStore();
  const router = useRouter();

  return useMutation({
    mutationFn: async () => {
      const refreshToken = localStorage.getItem("refresh_token");
      if (refreshToken) {
        await authApi.logout(refreshToken);
      }
    },
    onSuccess: () => {
      logout();
      router.push("/");
    },
    onError: () => {
      logout();
      router.push("/");
    },
  });
}

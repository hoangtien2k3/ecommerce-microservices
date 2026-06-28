"use client";

import { useEffect, useSyncExternalStore } from "react";
import { useTranslations } from "next-intl";
import { useAuthStore } from "@ecommerce/lib/store";
import { useLogin } from "@/hooks";
import { adminAuthStyles as s } from "./adminAuth.styles";

// Client-only read of the persisted access token (SSR snapshot is always null),
// kept in sync across tabs via the `storage` event.
function subscribeToken(onChange: () => void) {
  window.addEventListener("storage", onChange);
  return () => window.removeEventListener("storage", onChange);
}
function readToken(): string | null {
  return typeof window !== "undefined" ? localStorage.getItem("access_token") : null;
}

/**
 * Gates the whole admin app behind a Keycloak SSO session. With no token it
 * bounces the browser straight to SSO login (there's no public admin login page).
 */
export default function AdminAuthGuard({ children }: { children: React.ReactNode }) {
  const t = useTranslations("Auth");
  const { isAuthenticated } = useAuthStore();
  const { login } = useLogin();
  const token = useSyncExternalStore(subscribeToken, readToken, () => null);

  const authed = isAuthenticated || !!token;

  useEffect(() => {
    if (!authed) login(window.location.pathname);
  }, [authed, login]);

  if (!authed) {
    return (
      <div className={s.screen}>
        <div className={s.spinner} />
        <p className={s.text}>{t("redirecting")}</p>
      </div>
    );
  }

  return <>{children}</>;
}

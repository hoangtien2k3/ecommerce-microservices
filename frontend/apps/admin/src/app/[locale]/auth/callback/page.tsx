"use client";

import { Suspense, useEffect } from "react";
import { useSearchParams } from "next/navigation";
import { useTranslations } from "next-intl";
import { AlertCircle } from "lucide-react";
import { useSsoCallback } from "@/hooks";
import { useRouter } from "@/i18n/navigation";
import { adminAuthStyles as s } from "@/components/adminAuth.styles";

function CallbackInner() {
  const t = useTranslations("Auth");
  const params = useSearchParams();
  const router = useRouter();
  const { run, status } = useSsoCallback();

  const ticket = params.get("ticket");
  const oauthError = params.get("error");

  useEffect(() => {
    if (ticket) run(ticket);
  }, [ticket, run]);

  const failed = status === "error" || !!oauthError || !ticket;

  if (failed) {
    return (
      <div className={s.screen}>
        <div className={s.errorIcon}>
          <AlertCircle className="h-7 w-7 text-red-500" />
        </div>
        <h1 className={s.errorTitle}>{t("callbackErrorTitle")}</h1>
        <p className={s.errorText}>{t("callbackErrorText")}</p>
        <button onClick={() => router.replace("/admin/dashboard")} className={s.retryBtn}>
          {t("callbackRetry")}
        </button>
      </div>
    );
  }

  return (
    <div className={s.screen}>
      <div className={s.spinner} />
      <p className={s.text}>{t("callbackLoading")}</p>
    </div>
  );
}

export default function AdminSsoCallbackPage() {
  return (
    <Suspense fallback={null}>
      <CallbackInner />
    </Suspense>
  );
}

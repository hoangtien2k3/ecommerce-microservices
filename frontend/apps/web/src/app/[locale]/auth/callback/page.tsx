"use client";

import { Suspense, useEffect } from "react";
import { useSearchParams } from "next/navigation";
import { useTranslations } from "next-intl";
import { AlertCircle } from "lucide-react";
import { useSsoCallback } from "@/hooks";
import { Link } from "@/i18n/navigation";
import { authStyles as s } from "@/components/auth/auth.styles";

function CallbackInner() {
  const t = useTranslations("Auth");
  const params = useSearchParams();
  const { run, status } = useSsoCallback();

  const ticket = params.get("ticket");
  const oauthError = params.get("error");

  useEffect(() => {
    if (ticket) run(ticket);
  }, [ticket, run]);

  const failed = status === "error" || !!oauthError || !ticket;

  if (failed) {
    return (
      <div className={s.callbackWrap}>
        <div className={s.callbackErrorIcon}>
          <AlertCircle className="h-7 w-7 text-red-500" />
        </div>
        <h1 className={s.callbackErrorTitle}>{t("callbackErrorTitle")}</h1>
        <p className={s.callbackErrorText}>{t("callbackErrorText")}</p>
        <Link href="/login" className={s.switchLink}>
          {t("callbackRetry")}
        </Link>
      </div>
    );
  }

  return (
    <div className={s.callbackWrap}>
      <div className={s.callbackSpinner} />
      <p className={s.callbackText}>{t("callbackLoading")}</p>
    </div>
  );
}

export default function SsoCallbackPage() {
  return (
    <Suspense fallback={null}>
      <CallbackInner />
    </Suspense>
  );
}

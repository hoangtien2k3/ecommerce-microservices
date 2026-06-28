"use client";

import { ShieldCheck, KeyRound, Users } from "lucide-react";
import { useTranslations } from "next-intl";
import { Button } from "@ecommerce/ui";
import { useLogin } from "@/hooks";
import { Link } from "@/i18n/navigation";
import { authStyles as s } from "./auth.styles";

export default function LoginForm() {
  const t = useTranslations("Auth");
  const { login } = useLogin();

  const handleLogin = () => {
    // Preserve an optional post-login destination (e.g. ?redirect=/checkout)
    const next = new URLSearchParams(window.location.search).get("redirect") ?? undefined;
    login(next);
  };

  const features = [
    { icon: KeyRound, text: t("ssoFeature1") },
    { icon: ShieldCheck, text: t("ssoFeature2") },
    { icon: Users, text: t("ssoFeature3") },
  ];

  return (
    <div className={s.form}>
      <Button onClick={handleLogin} className={s.ssoBtn} size="lg">
        <ShieldCheck className="h-5 w-5" />
        {t("ssoLoginBtn")}
      </Button>

      <p className={s.ssoHint}>{t("ssoRedirectNote")}</p>

      <div className={s.divider}>
        <span className={s.dividerLine} />
        <span className={s.dividerText}>{t("ssoWhy")}</span>
        <span className={s.dividerLine} />
      </div>

      <ul className={s.ssoFeatures}>
        {features.map(({ icon: Icon, text }) => (
          <li key={text} className={s.ssoFeatureItem}>
            <Icon className={s.ssoFeatureIcon} />
            <span>{text}</span>
          </li>
        ))}
      </ul>

      <p className={s.switchText}>
        {t("noAccount")}{" "}
        <Link href="/register" className={s.switchLink}>
          {t("registerNow")}
        </Link>
      </p>
    </div>
  );
}

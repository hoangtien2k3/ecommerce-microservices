"use client";

import { UserPlus, ShieldCheck, Mail, Gift } from "lucide-react";
import { useTranslations } from "next-intl";
import { Button } from "@ecommerce/ui";
import { useRegister } from "@/hooks";
import { Link } from "@/i18n/navigation";
import { authStyles as s } from "./auth.styles";

export default function RegisterForm() {
  const t = useTranslations("Auth");
  const { register } = useRegister();

  const features = [
    { icon: ShieldCheck, text: t("registerFeature1") },
    { icon: Mail, text: t("registerFeature2") },
    { icon: Gift, text: t("registerFeature3") },
  ];

  return (
    <div className={s.form}>
      <Button onClick={register} className={s.ssoBtn} size="lg">
        <UserPlus className="h-5 w-5" />
        {t("registerSsoBtn")}
      </Button>

      <p className={s.ssoHint}>{t("registerSsoNote")}</p>

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
        {t("hasAccount")}{" "}
        <Link href="/login" className={s.switchLink}>
          {t("loginLink")}
        </Link>
      </p>
    </div>
  );
}

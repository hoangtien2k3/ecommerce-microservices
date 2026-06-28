import type { Metadata } from "next";
import { getTranslations } from "next-intl/server";
import RegisterForm from "@/components/auth/RegisterForm";
import { authStyles as s } from "@/components/auth/auth.styles";

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("Auth");
  return { title: t("registerPageTitle") };
}

export default async function RegisterPage() {
  const t = await getTranslations("Auth");

  return (
    <div className={s.wrapperWide}>
      <div className={s.card}>
        <div className={s.cardHeader}>
          <h1 className={s.title}>{t("registerTitle")}</h1>
          <p className={s.subtitle}>{t("registerSubtitle")}</p>
        </div>
        <RegisterForm />
      </div>
    </div>
  );
}

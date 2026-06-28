import type { Metadata } from "next";
import { getTranslations } from "next-intl/server";
import LoginForm from "@/components/auth/LoginForm";
import { authStyles as s } from "@/components/auth/auth.styles";

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("Auth");
  return { title: t("loginPageTitle") };
}

export default async function LoginPage() {
  const t = await getTranslations("Auth");

  return (
    <div className={s.wrapper}>
      <div className={s.card}>
        <div className={s.cardHeader}>
          <h1 className={s.title}>{t("loginTitle")}</h1>
          <p className={s.subtitle}>{t("loginSubtitle")}</p>
        </div>
        <LoginForm />
      </div>
    </div>
  );
}

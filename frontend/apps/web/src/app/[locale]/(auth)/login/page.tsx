import type { Metadata } from "next";
import { getTranslations } from "next-intl/server";
import LoginForm from "@/components/auth/LoginForm";

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("Auth");
  return { title: t("loginPageTitle") };
}

export default async function LoginPage() {
  const t = await getTranslations("Auth");

  return (
    <div className="w-full max-w-md">
      <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold text-gray-900">{t("loginTitle")}</h1>
          <p className="text-gray-500 text-sm mt-1">{t("loginSubtitle")}</p>
        </div>
        <LoginForm />
      </div>
    </div>
  );
}

import type { Metadata } from "next";
import { getTranslations } from "next-intl/server";
import RegisterForm from "@/components/auth/RegisterForm";

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("Auth");
  return { title: t("registerPageTitle") };
}

export default async function RegisterPage() {
  const t = await getTranslations("Auth");

  return (
    <div className="w-full max-w-lg">
      <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold text-gray-900">{t("registerTitle")}</h1>
          <p className="text-gray-500 text-sm mt-1">{t("registerSubtitle")}</p>
        </div>
        <RegisterForm />
      </div>
    </div>
  );
}

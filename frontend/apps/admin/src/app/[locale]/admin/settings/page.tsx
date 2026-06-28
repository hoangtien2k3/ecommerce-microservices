"use client";
import { Settings } from "lucide-react";
import { useTranslations } from "next-intl";

export default function AdminSettingsPage() {
  const t = useTranslations("Settings");
  const tCommon = useTranslations("Common");

  return (
    <div className="space-y-5">
      <h1 className="text-2xl font-bold text-gray-900">{t("title")}</h1>
      <div className="bg-white rounded-xl border border-gray-200 p-12 text-center">
        <Settings className="h-16 w-16 text-gray-300 mx-auto mb-4" />
        <p className="text-gray-500">{tCommon("developing")}</p>
      </div>
    </div>
  );
}

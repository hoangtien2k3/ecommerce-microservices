"use client";
import { Truck } from "lucide-react";
import { useTranslations } from "next-intl";
import { adminStyles as s } from "@/components/admin.styles";

export default function AdminShippingPage() {
  const t = useTranslations("Shipping");
  const tCommon = useTranslations("Common");

  return (
    <div className={s.page}>
      <h1 className={s.pageTitle}>{t("title")}</h1>
      <div className={s.devCard}>
        <Truck className={s.devIcon} />
        <p className={s.devText}>{tCommon("developing")}</p>
      </div>
    </div>
  );
}

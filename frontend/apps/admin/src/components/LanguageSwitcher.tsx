"use client";

import { useLocale, useTranslations } from "next-intl";
import { useRouter, usePathname } from "@/i18n/navigation";
import { useTransition } from "react";
import { cn } from "@ecommerce/lib/utils";
import { adminStyles as s } from "./admin.styles";

export default function LanguageSwitcher() {
  const locale = useLocale();
  const t = useTranslations("Lang");
  const router = useRouter();
  const pathname = usePathname();
  const [isPending, startTransition] = useTransition();

  const switchTo = (next: string) => {
    startTransition(() => {
      router.replace(pathname, { locale: next });
    });
  };

  return (
    <div className={s.langWrap} title={t("switchLabel")}>
      {(["vi", "en"] as const).map((loc) => (
        <button
          key={loc}
          onClick={() => switchTo(loc)}
          disabled={isPending || locale === loc}
          className={cn(s.langBtnBase, locale === loc ? s.langBtnActive : s.langBtnIdle)}
        >
          {t(loc)}
        </button>
      ))}
    </div>
  );
}

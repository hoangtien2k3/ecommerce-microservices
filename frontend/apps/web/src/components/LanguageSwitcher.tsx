"use client";

import { useLocale, useTranslations } from "next-intl";
import { useRouter, usePathname } from "@/i18n/navigation";
import { useTransition } from "react";
import { cn } from "@ecommerce/lib/utils";

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
    <div className="flex items-center gap-0.5" title={t("switchLabel")}>
      {(["vi", "en"] as const).map((loc) => (
        <button
          key={loc}
          onClick={() => switchTo(loc)}
          disabled={isPending || locale === loc}
          className={cn(
            "px-2 py-1 text-xs font-semibold rounded transition-colors",
            locale === loc
              ? "bg-orange-500 text-white"
              : "text-gray-500 hover:text-orange-500"
          )}
        >
          {t(loc)}
        </button>
      ))}
    </div>
  );
}

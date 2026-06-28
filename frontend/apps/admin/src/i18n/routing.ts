import { defineRouting } from "next-intl/routing";
import { locales, defaultLocale } from "@ecommerce/lib/i18n";

export const routing = defineRouting({
  locales,
  defaultLocale,
  localePrefix: "as-needed",
});

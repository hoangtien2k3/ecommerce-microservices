// Shared Tailwind class tokens for the admin app (language switcher + the
// "in development" placeholder pages). Page-specific tokens are co-located with
// their own *.styles.ts file.
export const adminStyles = {
  // LanguageSwitcher
  langWrap: "flex items-center gap-0.5",
  langBtnBase: "px-2 py-1 text-xs font-semibold rounded transition-colors",
  langBtnActive: "bg-primary-500 text-white",
  langBtnIdle: "text-gray-400 hover:text-white",

  // Placeholder pages
  page: "space-y-5",
  pageTitle: "text-2xl font-bold text-gray-900",
  devCard: "bg-white rounded-xl border border-gray-200 p-12 text-center",
  devIcon: "h-16 w-16 text-gray-300 mx-auto mb-4",
  devText: "text-gray-500",
} as const;

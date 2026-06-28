// Co-located Tailwind class tokens for the auth module (layout shell, cards,
// forms). Static presentational classes live here so the JSX stays readable.
// Conditional/stateful classes (loading, error toggles) stay inline with `cn()`.

export const authStyles = {
  // Layout shell
  shell: "min-h-screen bg-gradient-to-br from-primary-50 via-white to-primary-50 flex flex-col",
  shellHeader: "p-4",
  shellMain: "flex-1 flex items-center justify-center px-4 py-8",
  shellFooter: "p-4 text-center text-xs text-gray-400",

  // Brand mark
  brand: "inline-flex items-center gap-2",
  brandLogo: "w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center",
  brandLogoIcon: "h-5 w-5 text-white",
  brandText: "text-xl font-bold text-gray-900",
  brandAccent: "text-primary-500",

  // Card
  wrapper: "w-full max-w-md",
  wrapperWide: "w-full max-w-lg",
  card: "bg-white rounded-2xl shadow-xl border border-gray-100 p-8",
  cardHeader: "text-center mb-6",
  title: "text-2xl font-bold text-gray-900",
  subtitle: "text-gray-500 text-sm mt-1",

  // Form
  form: "space-y-4",
  grid2: "grid grid-cols-2 gap-3",
  fieldLabel: "block text-sm font-medium text-gray-700 mb-1",
  select: "w-full rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-primary-500",
  errorBox: "bg-red-50 border border-red-200 text-red-600 rounded-lg px-4 py-3 text-sm",

  // Register success
  successWrap: "text-center py-8",
  successIcon: "w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4",
  successIconText: "text-3xl",
  successTitle: "text-lg font-semibold text-gray-900 mb-2",
  successText: "text-gray-600 text-sm",
  passwordWrap: "relative",
  passwordToggle: "absolute right-3 top-8 text-gray-400 hover:text-gray-600",
  optionsRow: "flex items-center justify-between text-sm",
  rememberLabel: "flex items-center gap-2 cursor-pointer",
  rememberCheckbox: "rounded border-gray-300 text-primary-500",
  rememberText: "text-gray-600",
  link: "text-primary-500 hover:text-primary-600",
  switchText: "text-center text-sm text-gray-600",
  switchLink: "text-primary-500 font-medium hover:text-primary-600",
} as const;

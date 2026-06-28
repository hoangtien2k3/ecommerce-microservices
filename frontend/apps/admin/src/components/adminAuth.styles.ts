// Co-located Tailwind tokens for the admin auth guard + SSO callback screens.
export const adminAuthStyles = {
  screen: "min-h-screen flex flex-col items-center justify-center gap-4 px-4 text-center bg-gray-100",
  spinner: "h-10 w-10 animate-spin rounded-full border-4 border-primary-100 border-t-primary-500",
  text: "text-gray-600 text-sm",
  errorIcon: "w-14 h-14 bg-red-50 rounded-full flex items-center justify-center",
  errorTitle: "text-lg font-semibold text-gray-900",
  errorText: "text-gray-500 text-sm",
  retryBtn: "text-primary-500 font-medium hover:text-primary-600 text-sm",
} as const;

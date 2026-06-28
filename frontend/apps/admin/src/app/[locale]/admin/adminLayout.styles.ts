// Co-located Tailwind class tokens for the admin shell (sidebar + topbar).
// Conditional/active classes stay inline with `cn()`.
export const adminLayoutStyles = {
  root: "min-h-screen bg-gray-100 flex",
  sidebarBase: "fixed inset-y-0 left-0 z-50 w-64 bg-gray-900 text-white flex flex-col transition-transform md:relative md:translate-x-0",
  sidebarOpen: "translate-x-0",
  sidebarClosed: "-translate-x-full",

  brandBox: "p-5 border-b border-gray-800",
  brand: "flex items-center gap-2",
  brandLogo: "w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center",
  brandName: "text-white font-bold leading-tight",
  brandSub: "text-gray-400 text-xs",

  nav: "flex-1 p-4 space-y-1 overflow-y-auto",
  navItemBase: "flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all",
  navItemActive: "bg-primary-500 text-white",
  navItemIdle: "text-gray-400 hover:bg-gray-800 hover:text-white",

  userBox: "p-4 border-t border-gray-800",
  userRow: "flex items-center gap-3 mb-3",
  userAvatar: "w-9 h-9 bg-primary-500 rounded-full flex items-center justify-center text-white text-sm font-semibold flex-shrink-0",
  userInfo: "min-w-0",
  userName: "text-sm font-medium text-white truncate",
  userEmail: "text-xs text-gray-400 truncate",
  logoutBtn: "w-full flex items-center gap-2 text-sm text-red-400 hover:text-red-300 transition-colors",

  overlay: "fixed inset-0 bg-black/50 z-40 md:hidden",
  content: "flex-1 flex flex-col min-w-0",
  topbar: "bg-white border-b border-gray-200 px-4 py-3 flex items-center gap-3 sticky top-0 z-30",
  topbarToggle: "md:hidden p-1.5 rounded-lg hover:bg-gray-100",
  topbarTitle: "flex-1",
  topbarCrumb: "text-sm text-gray-500",
  backLink: "text-sm text-primary-500 hover:text-primary-600",
  main: "flex-1 p-6",
} as const;

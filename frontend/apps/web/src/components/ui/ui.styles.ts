// Co-located Tailwind class tokens for storefront UI helpers
// (filter sidebar, sort select). Conditional/active classes stay inline via cn().
export const uiStyles = {
  // FilterSidebar
  sidebarBase: "w-64 flex-shrink-0 space-y-6 fixed md:relative inset-0 z-40 bg-white md:bg-transparent p-4 md:p-0 overflow-y-auto md:overflow-visible",
  sidebarOpen: "block",
  sidebarClosed: "hidden md:block",
  sidebarMobileHead: "flex items-center justify-between md:hidden mb-4",
  sidebarMobileTitle: "font-semibold",
  filterGroup: "bg-white rounded-xl border border-gray-200 p-4",
  filterGroupTitle: "font-semibold text-gray-900 mb-3",
  filterGroupBody: "space-y-1",
  filterBtnBase: "w-full text-left text-sm px-2 py-1.5 rounded-lg transition-colors",
  filterBtnActive: "bg-primary-50 text-primary-600 font-medium",
  filterBtnIdle: "text-gray-700 hover:bg-gray-50",

  // SortSelect
  sortSelect: "appearance-none border border-gray-300 rounded-lg px-3 py-2 pr-8 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-primary-500",
  sortCaret: "absolute right-2 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400 pointer-events-none",

  // LanguageSwitcher
  langWrap: "flex items-center gap-0.5",
  langBtnBase: "px-2 py-1 text-xs font-semibold rounded transition-colors",
  langBtnActive: "bg-primary-500 text-white",
  langBtnIdle: "text-gray-500 hover:text-primary-500",
} as const;

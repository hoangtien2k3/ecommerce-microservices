// Co-located Tailwind class tokens for the admin dashboard.
// Dynamic per-stat colors and up/down/stock states stay inline.
export const dashboardStyles = {
  root: "space-y-6",
  title: "text-2xl font-bold text-gray-900",
  subtitle: "text-gray-500 text-sm mt-1",
  panelsGrid: "grid grid-cols-1 xl:grid-cols-2 gap-6",

  // Stats
  statsGrid: "grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4",
  statCard: "bg-white rounded-xl border border-gray-200 p-5",
  statHead: "flex items-center justify-between mb-3",
  statValue: "text-2xl font-bold text-gray-900",
  statLabel: "text-sm text-gray-500 mt-0.5",

  // Panels
  panel: "bg-white rounded-xl border border-gray-200 p-5",
  panelHead: "flex items-center justify-between mb-4",
  panelTitle: "font-bold text-gray-900",
  panelLink: "text-xs text-primary-500 hover:text-primary-600",
  panelEmpty: "text-center py-8 text-gray-500 text-sm",
  panelList: "space-y-3",
  row: "flex items-center gap-3 p-3 rounded-lg hover:bg-gray-50",
  rowInfo: "flex-1 min-w-0",
  rowTitle: "text-sm font-medium text-gray-900 truncate",
  rowMeta: "text-xs text-gray-500 flex items-center gap-1",
  rowPrice: "text-sm font-bold text-primary-500",
  orderIcon: "w-9 h-9 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0",
  productIcon: "w-10 h-10 bg-primary-50 rounded-lg flex items-center justify-center flex-shrink-0 overflow-hidden",
} as const;

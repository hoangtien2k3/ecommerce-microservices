// Co-located Tailwind class tokens for the admin categories page.
export const categoriesStyles = {
  root: "space-y-5",
  header: "flex items-center justify-between",
  title: "text-2xl font-bold text-gray-900",
  grid: "grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4",
  skeleton: "bg-white rounded-xl border animate-pulse h-24",

  card: "bg-white rounded-xl border border-gray-200 p-4 flex items-center gap-3 group",
  cardIcon: "w-10 h-10 bg-primary-100 rounded-xl flex items-center justify-center shrink-0",
  cardBody: "flex-1 min-w-0",
  cardName: "font-medium text-gray-900 text-sm truncate",
  cardId: "text-xs text-gray-400",
  cardActions: "flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity",
  editBtn: "p-1 text-gray-400 hover:text-blue-600 rounded",
  deleteBtn: "p-1 text-gray-400 hover:text-red-600 rounded",
} as const;

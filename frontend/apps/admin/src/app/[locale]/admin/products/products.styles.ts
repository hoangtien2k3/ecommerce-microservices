// Co-located Tailwind class tokens for the admin products page.
export const productsStyles = {
  root: "space-y-5",
  header: "flex items-center justify-between",
  title: "text-2xl font-bold text-gray-900",
  count: "text-sm text-gray-500 mt-0.5",
  cell: "flex items-center gap-3",
  thumb: "w-10 h-10 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0",
  thumbEmpty: "w-full h-full flex items-center justify-center",
  cellName: "font-medium text-gray-900 line-clamp-1",
  muted: "text-gray-500",
  rowActions: "flex items-center justify-center gap-2",
  editBtn: "p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors",
  deleteBtn: "p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors",
  fieldLabel: "block text-sm font-medium text-gray-700 mb-1",
  select: "w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500",
  formGrid2: "grid grid-cols-2 gap-3",
} as const;

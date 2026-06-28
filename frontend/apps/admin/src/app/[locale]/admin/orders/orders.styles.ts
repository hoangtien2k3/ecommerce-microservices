// Co-located Tailwind class tokens for the admin orders page.
export const ordersStyles = {
  root: "space-y-5",
  title: "text-2xl font-bold text-gray-900",
  count: "text-sm text-gray-500 mt-0.5",
  idCell: "flex items-center gap-2",
  idIcon: "w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center",
  idText: "font-medium text-gray-900",
  dateCell: "flex items-center gap-1 text-gray-500",
  descCell: "text-gray-600 max-w-[200px] truncate block",
  viewBtn: "p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors",
} as const;

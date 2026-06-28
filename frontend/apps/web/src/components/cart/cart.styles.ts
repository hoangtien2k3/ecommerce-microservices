// Co-located Tailwind class tokens for the cart components.
export const cartStyles = {
  // CartItemRow
  row: "bg-white rounded-xl border border-gray-200 p-4 flex gap-4",
  thumbWrap: "w-20 h-20 relative rounded-lg overflow-hidden bg-gray-50",
  thumbPlaceholder: "w-full h-full flex items-center justify-center",
  info: "flex-1 min-w-0",
  title: "text-sm font-medium text-gray-900 hover:text-primary-500 line-clamp-2",
  category: "text-xs text-gray-500 mt-0.5",
  price: "text-base font-bold text-primary-500 mt-1",
  actions: "flex flex-col items-end gap-3",
  removeBtn: "text-gray-400 hover:text-red-500 transition-colors",
  lineTotal: "text-sm font-bold text-gray-900",

  // OrderSummary
  summary: "bg-white rounded-xl border border-gray-200 p-5 sticky top-24",
  summaryTitle: "font-bold text-gray-900 mb-4",
  miniList: "space-y-3 max-h-60 overflow-y-auto mb-4",
  miniRow: "flex gap-3",
  miniThumb: "w-14 h-14 relative bg-gray-50 rounded-lg overflow-hidden flex-shrink-0",
  miniThumbPlaceholder: "w-full h-full flex items-center justify-center",
  miniBadge: "absolute -top-1 -right-1 w-5 h-5 flex items-center justify-center p-0 text-xs",
  miniInfo: "flex-1 min-w-0",
  miniName: "text-xs font-medium text-gray-900 line-clamp-2",
  miniPrice: "text-xs text-primary-500 font-bold",
  totals: "space-y-3 text-sm",
  totalRow: "flex justify-between text-gray-600",
  promo: "text-xs text-primary-500 bg-primary-50 rounded-lg p-2",
  grandTotal: "border-t border-gray-200 pt-3 flex justify-between font-bold text-base",
  grandTotalValue: "text-primary-500",
  couponWrap: "mt-4 flex gap-2",
  couponInput: "flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500",
} as const;

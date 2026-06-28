// Co-located Tailwind class tokens for the orders route page.
export const ordersStyles = {
  page: "max-w-4xl mx-auto px-4 py-8",
  heading: "flex items-center gap-2 mb-6",
  title: "text-2xl font-bold text-gray-900",
  list: "space-y-4",

  card: "bg-white rounded-xl border border-gray-200 p-5 hover:shadow-md transition-shadow",
  cardHeader: "flex items-center justify-between mb-3",
  cardId: "flex items-center gap-2",
  cardIdText: "font-bold text-gray-900",
  cardMeta: "grid grid-cols-2 gap-4 text-sm text-gray-600",
  cardDate: "flex items-center gap-2",
  cardFee: "text-right",
  cardFeeValue: "font-bold text-primary-500 text-base",
  cardDesc: "text-sm text-gray-500 mt-2",
} as const;

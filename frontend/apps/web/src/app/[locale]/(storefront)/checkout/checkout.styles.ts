// Co-located Tailwind class tokens for the checkout route page.
export const checkoutPageStyles = {
  page: "max-w-5xl mx-auto px-4 py-8",
  title: "text-2xl font-bold text-gray-900 mb-6",
  layout: "grid grid-cols-1 lg:grid-cols-3 gap-6",
  main: "lg:col-span-2",
  side: "lg:col-span-1",

  // Step card
  card: "bg-white rounded-xl border border-gray-200 p-6",
  cardHead: "flex items-center gap-2 mb-4",
  cardTitle: "text-lg font-bold",
  fields: "space-y-4",
  grid2: "grid grid-cols-2 gap-4",
  fieldLabel: "block text-sm font-medium text-gray-700 mb-1",
  select: "w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500",
  textarea: "w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500",

  // Payment options
  payList: "space-y-3",
  payDesc: "text-xs text-gray-500",
  payName: "font-medium text-gray-900 text-sm",
  payEmoji: "text-2xl",
  payActions: "flex gap-3 mt-6",

  // Confirmation
  confirm: "bg-white rounded-xl border border-gray-200 p-8 text-center",
  confirmIcon: "w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4",
  confirmTitle: "text-2xl font-bold text-gray-900 mb-2",
  confirmCode: "text-gray-500 mb-1",
  confirmMsg: "text-sm text-gray-500 mb-6",
  confirmActions: "flex gap-3 justify-center",
} as const;

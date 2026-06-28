// Co-located Tailwind class tokens for the product detail route.
export const productDetailStyles = {
  page: "max-w-7xl mx-auto px-4 py-8",
  loadingGrid: "grid grid-cols-1 md:grid-cols-2 gap-8 animate-pulse",
  loadingImage: "aspect-square bg-gray-200 rounded-xl",
  loadingInfo: "space-y-4",
  errorPage: "max-w-7xl mx-auto px-4 py-16 text-center",
  errorText: "text-lg font-medium text-gray-700",

  layout: "grid grid-cols-1 md:grid-cols-2 gap-10",
  descBox: "mt-10 bg-white rounded-xl border border-gray-200 p-6",
  descTitle: "text-lg font-bold text-gray-900 mb-4",
  descText: "text-gray-700 leading-relaxed",

  // Breadcrumb
  breadcrumb: "flex items-center gap-2 text-sm text-gray-500 mb-6",
  breadcrumbLink: "hover:text-primary-500",
  breadcrumbCurrent: "text-gray-900 font-medium truncate max-w-[200px]",

  // Gallery
  gallery: "relative aspect-square bg-gray-50 rounded-2xl overflow-hidden border border-gray-200",
  galleryPlaceholder: "w-full h-full flex items-center justify-center bg-gradient-to-br from-primary-50 to-primary-100",

  // Info
  infoTopRow: "flex items-center gap-2 mb-2",
  sku: "text-xs text-gray-400",
  infoTitle: "text-2xl font-bold text-gray-900 mb-3",
  ratingRow: "flex items-center gap-2 mb-4",
  ratingStars: "flex items-center",
  ratingText: "text-sm text-gray-600",
  priceBox: "bg-primary-50 rounded-xl p-4 mb-5",
  priceValue: "text-3xl font-bold text-primary-500 mb-1",
  priceNote: "text-sm text-gray-500",
  qtyRow: "flex items-center gap-3 mb-5",
  qtyLabel: "text-sm font-medium text-gray-700",
  actionRow: "flex gap-3 mb-6",
  iconBtn: "p-3 border border-gray-300 rounded-lg hover:border-gray-400 transition-colors",
  perks: "space-y-2 border-t border-gray-200 pt-5",
  perkItem: "flex items-center gap-3 text-sm text-gray-600",
  stockRow: "flex items-center gap-2 mb-5",
} as const;

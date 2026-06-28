// Co-located Tailwind class tokens for the product card / grid components.
// Conditional/stateful classes (stock, added-to-cart) stay inline with `cn()`.

export const productStyles = {
  // Card
  card: "group bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-lg hover:border-primary-200 transition-all duration-300",
  imageWrap: "relative aspect-square bg-gray-50 overflow-hidden",
  badgeTopLeft: "absolute top-2 left-2 flex flex-col gap-1",
  badgeTopRight: "absolute top-2 right-2 flex flex-col gap-2 opacity-0 group-hover:opacity-100 transition-opacity",
  addToCartSlot: "absolute inset-x-0 bottom-0 translate-y-full group-hover:translate-y-0 transition-transform duration-300",
  body: "p-3",
  category: "text-xs text-gray-500 mb-1",
  title: "text-sm font-medium text-gray-900 line-clamp-2 mb-2 min-h-10",
  priceFooter: "flex items-center justify-between mt-2",

  // Parts
  imagePlaceholder: "w-full h-full flex items-center justify-center bg-linear-to-br from-primary-50 to-primary-100",
  outOfStockBadge: "bg-gray-800 text-white text-xs px-2 py-0.5 rounded-full",
  wishlistBtn: "w-8 h-8 bg-white rounded-full flex items-center justify-center shadow-md hover:scale-110 transition-transform",
  starsRow: "flex items-center gap-1",
  starCount: "text-xs text-gray-500",
  price: "text-base font-bold text-primary-500",

  // Grid
  grid: "grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4",
  skeletonCard: "bg-white rounded-xl border border-gray-200 overflow-hidden animate-pulse",
  skeletonImage: "aspect-square bg-gray-200",
  skeletonBody: "p-3 space-y-2",
  gridError: "text-center py-12 text-gray-500",
} as const;

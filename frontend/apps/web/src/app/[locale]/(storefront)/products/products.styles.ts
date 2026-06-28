// Co-located Tailwind class tokens for the products list route.
export const productsStyles = {
  page: "max-w-7xl mx-auto px-4 py-8",
  header: "flex items-center justify-between mb-6",
  title: "text-2xl font-bold text-gray-900",
  toolbar: "flex items-center gap-3",
  body: "flex gap-6",
  main: "flex-1 min-w-0",
  grid: "grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4",
  skeletonCard: "bg-white rounded-xl border animate-pulse",
  skeletonImage: "aspect-square bg-gray-200 rounded-t-xl",
  skeletonBody: "p-3 space-y-2",
  fallback: "max-w-7xl mx-auto px-4 py-8 text-center text-gray-500",
} as const;

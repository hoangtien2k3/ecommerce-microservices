// Co-located Tailwind class tokens for the first-come coupon page. The hero
// reuses the flash-sale look from home.styles so the page feels like part of
// the same storefront.

export const couponStyles = {
  // ---- Hero banner (same gradient and countdown boxes as FlashSale) ----
  hero: "rounded-xl overflow-hidden bg-gradient-to-b from-primary-700 to-primary-600 px-5 py-5 md:px-6 md:py-6 flex items-center justify-between flex-wrap gap-4",
  heroTitle: "flex items-center gap-2 text-white text-xl md:text-2xl font-extrabold italic uppercase tracking-wide",
  heroSub: "text-white/85 text-sm mt-1",
  heroCountWrap: "flex items-center gap-1",
  heroCountLabel: "text-white text-sm mr-1",
  heroCountBox: "bg-gray-900 text-white text-sm font-bold rounded px-1.5 py-0.5 min-w-7 text-center",

  // ---- Coupon grid ----
  grid: "grid grid-cols-1 md:grid-cols-2 gap-4",
  gridSingle: "grid grid-cols-1",

  // ---- Ticket card: coloured stub on the left, details on the right ----
  card: "flex bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-md transition-shadow",
  cardInactive: "opacity-60 hover:shadow-none",
  stub: "relative w-28 sm:w-32 shrink-0 flex flex-col items-center justify-center gap-0.5 bg-gradient-to-br from-primary-500 to-primary-700 text-white px-2 py-4 border-r-2 border-dashed border-white/70",
  stubMuted: "from-gray-400 to-gray-500",
  stubAmount: "text-lg sm:text-xl font-extrabold leading-tight text-center",
  stubLabel: "text-[11px] font-semibold tracking-widest text-white/90",
  body: "flex-1 min-w-0 p-4 flex flex-col gap-2",
  bodyHeader: "flex items-start justify-between gap-2",
  name: "font-semibold text-gray-900 leading-snug line-clamp-2",
  desc: "text-xs text-gray-500",
  minOrder: "text-xs text-gray-600",

  // Remaining-quantity bar (same look as the flash-sale sold bar)
  bar: "relative h-4 rounded-full bg-amber-100 overflow-hidden",
  barFill: "absolute inset-y-0 left-0 bg-gradient-to-r from-amber-400 to-primary-500 transition-[width] duration-500",
  barLabel: "absolute inset-0 flex items-center justify-center text-[10px] font-semibold text-primary-700",

  footer: "mt-auto flex items-center justify-between gap-2 pt-1",
  time: "text-xs text-gray-500 flex items-center gap-1 whitespace-nowrap",
  action: "shrink-0 min-w-28",

  // ---- Featured: the only coupon on the page, shown full width and larger ----
  stubFeatured: "sm:w-56 py-8",
  stubAmountFeatured: "text-xl sm:text-4xl",
  stubLabelFeatured: "text-xs sm:text-sm",
  bodyFeatured: "p-5 sm:p-6 gap-3",
  nameFeatured: "text-lg sm:text-xl font-bold",
  descFeatured: "text-sm",
  minOrderFeatured: "text-sm",
  barFeatured: "h-6",
  barLabelFeatured: "text-xs",
  timeFeatured: "text-sm",
  footerFeatured: "flex-wrap",
  actionFeatured: "w-full sm:w-auto sm:min-w-40",

  // ---- Issue result notice ----
  notice: "flex items-center gap-3 rounded-xl border px-4 py-3 text-sm",
  noticeText: "flex-1",
  noticeClose: "p-1 rounded hover:bg-black/5 transition-colors",
} as const;

// Notice colours per issue result.
export const noticeTone = {
  success: "bg-green-50 border-green-200 text-green-800",
  alreadyClaimed: "bg-blue-50 border-blue-200 text-blue-800",
  notOpen: "bg-yellow-50 border-yellow-200 text-yellow-800",
  soldOut: "bg-gray-50 border-gray-200 text-gray-700",
  error: "bg-red-50 border-red-200 text-red-700",
} as const;

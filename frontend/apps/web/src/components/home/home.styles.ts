// Co-located Tailwind class tokens for the homepage sections. Static
// presentational classes live here; conditional ones (active slide, discount
// badge visibility) stay inline with `cn()` in the components.

export const homeStyles = {
  // Generic section chrome
  section: "bg-white rounded-2xl p-4 md:p-5",
  sectionTitle: "text-lg md:text-xl font-bold text-gray-900 flex items-center gap-2",
  viewAll: "text-sm text-primary-600 font-medium flex items-center gap-1 hover:underline",

  // Hero slider
  heroGrid: "grid grid-cols-1 lg:grid-cols-4 gap-3",
  heroMain: "lg:col-span-3 relative rounded-2xl overflow-hidden h-52 sm:h-64 md:h-80",
  heroBadge: "inline-flex w-fit items-center gap-1.5 bg-white/20 backdrop-blur rounded-full px-3 py-1 text-xs font-medium mb-3",
  heroTitle: "text-2xl md:text-4xl font-bold mb-2 max-w-md leading-tight",
  heroSub: "text-white/90 text-sm md:text-lg mb-5 max-w-sm",
  heroCta: "inline-flex w-fit items-center gap-2 bg-white text-primary-600 font-semibold text-sm px-5 py-2.5 rounded-full hover:bg-white/90 transition-colors",
  heroArrow: "absolute top-1/2 -translate-y-1/2 w-9 h-9 rounded-full bg-white/30 hover:bg-white/50 backdrop-blur flex items-center justify-center text-white transition-colors",
  heroDots: "absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2",
  heroSide: "hidden lg:flex lg:col-span-1 flex-col gap-3",
  heroSideCard: "flex-1 rounded-2xl bg-gradient-to-br p-5 text-white flex flex-col justify-center hover:opacity-95 transition-opacity",
  heroSideTitle: "text-lg font-bold mb-1",
  heroSideSub: "text-sm text-white/90",

  // Quick deals
  quickGrid: "grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3",
  quickCard: "flex items-center gap-3 bg-white rounded-xl px-4 py-3 hover:shadow-md hover:-translate-y-0.5 transition-all",
  quickIcon: "w-10 h-10 rounded-full bg-primary-50 flex items-center justify-center text-xl shrink-0",
  quickLabel: "text-sm font-semibold text-gray-800 leading-tight",

  // Category bar
  catGrid: "grid grid-cols-5 md:grid-cols-10 gap-2 md:gap-3",
  catItem: "flex flex-col items-center gap-2 p-2 rounded-xl hover:bg-primary-50 transition-colors group",
  catIcon: "w-12 h-12 md:w-14 md:h-14 rounded-full bg-gray-50 group-hover:bg-white flex items-center justify-center text-2xl md:text-3xl transition-colors",
  catLabel: "text-[11px] md:text-xs text-center text-gray-700 font-medium leading-tight",

  // Product rail
  railGrid: "grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-3",

  // Brand strip
  brandGrid: "grid grid-cols-4 md:grid-cols-8 gap-3",
  brandCard: "flex items-center justify-center h-16 rounded-xl border border-gray-100 bg-gray-50 hover:border-primary-200 hover:bg-white hover:shadow-sm transition-all text-sm font-bold text-gray-700",

  // Product tile
  tile: "group h-full flex flex-col bg-white rounded-xl border border-gray-100 p-3 hover:shadow-lg hover:border-primary-200 transition-all duration-200",
  tileImage: "relative aspect-square bg-gray-50 rounded-lg overflow-hidden mb-2.5",
  tileImagePlaceholder: "w-full h-full flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100",
  tileImageEmoji: "text-5xl opacity-30 group-hover:scale-110 transition-transform duration-300",
  tileDiscount: "absolute top-2 left-2 bg-primary-500 text-white text-[11px] font-bold px-1.5 py-0.5 rounded",
  tileBadge: "absolute top-2 right-2 bg-amber-400 text-amber-900 text-[10px] font-bold px-1.5 py-0.5 rounded",
  tileTitle: "text-sm font-medium text-gray-800 line-clamp-2 min-h-10 mb-1.5 group-hover:text-primary-600 transition-colors",
  tilePriceRow: "mb-1.5",
  tilePrice: "text-base font-bold text-primary-600",
  tileOldPrice: "ml-2 text-xs text-gray-400 line-through",
  tileInstallment: "text-[11px] text-gray-500 mb-1.5",
  tileFooter: "mt-auto flex items-center justify-between pt-1.5 border-t border-gray-50",
  tileStars: "flex items-center gap-0.5",
  tileSold: "text-[11px] text-gray-400",
} as const;

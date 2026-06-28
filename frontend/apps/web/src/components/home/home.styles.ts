// Co-located Tailwind class tokens for the homepage sections. Static
// presentational classes live here; conditional ones (active slide, discount
// badge visibility) stay inline with `cn()` in the components.

export const homeStyles = {
  // Generic section chrome
  section: "bg-white rounded-xl p-4 md:p-5",
  sectionTitle: "text-lg md:text-xl font-bold text-gray-900 flex items-center gap-2",
  viewAll: "text-sm text-primary-600 font-medium flex items-center gap-1 hover:underline",

  // ---- Hero (3-column: sidebar + carousel + side banners) ----
  heroWrap: "grid grid-cols-1 lg:grid-cols-[240px_1fr_300px] gap-3",
  heroSidebar: "hidden lg:block bg-white rounded-xl py-2 overflow-hidden",
  heroSidebarItem: "flex items-center gap-3 px-4 py-[7px] text-sm text-gray-700 hover:bg-primary-50 hover:text-primary-600 transition-colors group",
  heroSidebarEmoji: "text-lg w-5 text-center",
  heroSidebarLabel: "flex-1 truncate",
  heroCarousel: "relative rounded-xl overflow-hidden h-56 sm:h-72 lg:h-full min-h-[300px]",
  heroSlide: "absolute inset-0 flex flex-col justify-center px-8 md:px-12 text-white bg-gradient-to-r transition-opacity duration-700",
  heroBadge: "inline-flex w-fit items-center gap-1.5 bg-white/20 backdrop-blur rounded-full px-3 py-1 text-xs font-medium mb-3",
  heroTitle: "text-2xl md:text-4xl font-bold mb-2 max-w-md leading-tight",
  heroSub: "text-white/90 text-sm md:text-lg mb-5 max-w-sm",
  heroCta: "inline-flex w-fit items-center gap-2 bg-white text-primary-600 font-semibold text-sm px-5 py-2.5 rounded-full hover:bg-white/90 transition-colors",
  heroArrow: "absolute top-1/2 -translate-y-1/2 w-9 h-9 rounded-full bg-white/30 hover:bg-white/50 backdrop-blur flex items-center justify-center text-white transition-colors z-10",
  heroDots: "absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2 z-10",
  heroSideCol: "hidden lg:flex flex-col gap-3",
  heroSideCard: "flex-1 rounded-xl bg-gradient-to-br p-4 text-white flex flex-col justify-center hover:opacity-95 transition-opacity min-h-0",
  heroSideTitle: "text-base font-bold mb-0.5",
  heroSideSub: "text-xs text-white/90",

  // ---- Quick deals strip ----
  quickGrid: "grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3",
  quickCard: "flex items-center gap-3 bg-white rounded-xl px-4 py-3 hover:shadow-md hover:-translate-y-0.5 transition-all",
  quickIcon: "w-10 h-10 rounded-full bg-primary-50 flex items-center justify-center text-xl shrink-0",
  quickLabel: "text-sm font-semibold text-gray-800 leading-tight",

  // ---- Category icon bar ----
  catGrid: "grid grid-cols-5 md:grid-cols-10 gap-2 md:gap-3",
  catItem: "flex flex-col items-center gap-2 p-2 rounded-xl hover:bg-primary-50 transition-colors group",
  catIcon: "w-12 h-12 md:w-14 md:h-14 rounded-full bg-gray-50 group-hover:bg-white flex items-center justify-center text-2xl md:text-3xl transition-colors",
  catLabel: "text-[11px] md:text-xs text-center text-gray-700 font-medium leading-tight",

  // ---- Flash sale (maroon) ----
  flashWrap: "rounded-xl overflow-hidden bg-gradient-to-b from-primary-700 to-primary-600",
  flashHeader: "flex items-center justify-between px-4 py-3 flex-wrap gap-2",
  flashTitle: "flex items-center gap-1.5 text-white text-lg md:text-2xl font-extrabold italic uppercase tracking-wide",
  flashTabs: "flex items-center gap-2",
  flashTab: "px-3 py-1.5 rounded-full text-xs font-semibold transition-colors",
  flashCountWrap: "flex items-center gap-1",
  flashCountLabel: "text-white text-sm mr-1 hidden sm:inline",
  flashCountBox: "bg-gray-900 text-white text-sm font-bold rounded px-1.5 py-0.5 min-w-7 text-center",
  flashTrack: "flex gap-3 overflow-x-auto no-scrollbar px-4 pb-4",

  // ---- Product tabs (Deal sốc / Hot trend / Hàng mới về) ----
  tabsBar: "flex items-center gap-1 border-b border-gray-100",
  tab: "px-4 py-3 text-sm font-bold transition-colors relative",
  tabActive: "text-primary-600",
  tabInactive: "text-gray-400 hover:text-gray-600",
  tabUnderline: "absolute bottom-0 left-0 right-0 h-0.5 bg-primary-600 rounded-full",
  chipRow: "flex items-center gap-2 overflow-x-auto no-scrollbar py-3",
  chip: "px-3 py-1.5 rounded-lg border text-xs font-medium whitespace-nowrap transition-colors",
  chipActive: "border-primary-500 bg-primary-50 text-primary-600",
  chipInactive: "border-gray-200 text-gray-600 hover:border-primary-300",

  // ---- Category section block (left banner + grid) ----
  catBlockGrid: "grid grid-cols-1 lg:grid-cols-[220px_1fr] gap-4",
  catBlockBanner: "hidden lg:flex flex-col justify-between rounded-xl p-4 text-white bg-gradient-to-br min-h-[420px]",
  catBlockBannerTitle: "text-xl font-extrabold",
  catBlockBannerSub: "text-sm text-white/90 mt-1",
  catBlockBannerCta: "inline-flex w-fit items-center gap-1 bg-white text-primary-600 text-xs font-semibold px-3 py-1.5 rounded-full mt-3",
  productGrid: "grid grid-cols-2 md:grid-cols-3 xl:grid-cols-5 gap-3",

  // ---- Product rail ----
  railGrid: "grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-3",

  // ---- Brand strip ----
  brandGrid: "grid grid-cols-4 md:grid-cols-8 gap-3",
  brandCard: "flex items-center justify-center h-16 rounded-xl border border-gray-100 bg-gray-50 hover:border-primary-200 hover:bg-white hover:shadow-sm transition-all text-sm font-bold text-gray-700",

  // ---- Product tile (CellphoneS card) ----
  tile: "group relative h-full flex flex-col bg-white rounded-xl border border-gray-100 p-2.5 hover:shadow-lg hover:border-primary-200 transition-all duration-200",
  tileDiscount: "absolute top-0 left-0 bg-primary-500 text-white text-[11px] font-bold px-1.5 py-0.5 rounded-br-lg rounded-tl-xl z-10",
  tileFav: "absolute top-1.5 right-1.5 w-7 h-7 rounded-full bg-white/80 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity z-10",
  tileImage: "relative aspect-square rounded-lg overflow-hidden mb-2 flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100",
  tileImageEmoji: "text-5xl opacity-30 group-hover:scale-110 transition-transform duration-300",
  tileTitle: "text-[13px] font-medium text-gray-800 line-clamp-2 min-h-9 mb-1.5 group-hover:text-primary-600 transition-colors leading-snug",
  tilePriceRow: "flex items-baseline gap-1.5 flex-wrap",
  tilePrice: "text-[15px] font-bold text-primary-600",
  tileOldPrice: "text-[11px] text-gray-400 line-through",
  tileInstallment: "text-[11px] text-gray-500 mt-0.5",
  tileMemberBox: "mt-1.5 text-[11px] text-gray-600 bg-gray-50 rounded px-2 py-1 leading-tight",
  tileMemberAmount: "text-primary-600 font-semibold",
  tileFooter: "mt-auto flex items-center justify-between pt-1.5",
  tileStars: "flex items-center gap-0.5",
  tileSold: "text-[11px] text-gray-400",

  // ---- Icon grid (accessories / appliances) ----
  iconGrid: "grid grid-cols-4 sm:grid-cols-6 lg:grid-cols-8 gap-2",
  iconCard: "flex flex-col items-center gap-2 p-3 rounded-xl border border-gray-100 hover:border-primary-200 hover:shadow-sm transition-all",
  iconEmoji: "text-3xl",
  iconLabel: "text-[11px] text-center text-gray-700 leading-tight",

  // ---- USP strip ----
  uspGrid: "grid grid-cols-2 md:grid-cols-4 gap-4",
  uspItem: "flex items-center gap-3",
  uspIcon: "w-11 h-11 rounded-full bg-primary-50 flex items-center justify-center shrink-0",
  uspTitle: "text-sm font-semibold text-gray-900 leading-tight",
  uspDesc: "text-xs text-gray-500",
} as const;

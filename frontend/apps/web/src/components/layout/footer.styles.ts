// Co-located Tailwind class tokens for the storefront footer.
export const footerStyles = {
  root: "bg-white border-t border-gray-200 mt-auto",
  inner: "max-w-7xl mx-auto px-4 py-10",
  grid: "grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-8",
  colTitle: "text-sm font-bold text-gray-900 mb-3 uppercase tracking-wide",
  link: "text-sm text-gray-500 hover:text-primary-600 transition-colors block py-0.5",
  hotline: "text-2xl font-bold text-primary-600",
  hotlineNote: "text-xs text-gray-400",
  payRow: "flex flex-wrap gap-2 mt-2",
  payChip: "h-7 px-2 rounded border border-gray-200 bg-gray-50 flex items-center text-[10px] font-semibold text-gray-500",
  social: "flex gap-2 mt-2",
  socialIcon: "w-8 h-8 bg-gray-100 rounded-lg flex items-center justify-center text-gray-500 hover:bg-primary-500 hover:text-white transition-colors",
  appBtn: "flex items-center gap-2 border border-gray-200 rounded-lg px-3 py-1.5 text-xs text-gray-600 hover:border-primary-300 transition-colors",
  bottom: "border-t border-gray-100",
  bottomInner: "max-w-7xl mx-auto px-4 py-4 flex flex-col md:flex-row justify-between items-center gap-2 text-xs text-gray-400",
  bottomLinks: "flex gap-4",
} as const;

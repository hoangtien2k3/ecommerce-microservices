// Co-located Tailwind class tokens for the header category dropdown.
export const categoryMenuStyles = {
  root: "relative hidden lg:block",
  trigger: "flex items-center gap-2 px-3 py-2 rounded-lg bg-white text-primary-600 font-medium text-sm hover:bg-white/90 transition-colors whitespace-nowrap",
  panel: "absolute left-0 top-full mt-2 w-60 bg-white rounded-xl shadow-xl border border-gray-100 py-2 z-50",
  item: "flex items-center gap-3 px-4 py-2.5 text-sm text-gray-700 hover:bg-primary-50 hover:text-primary-600 transition-colors group",
  itemEmoji: "text-lg",
  itemLabel: "flex-1",
  itemCaret: "h-4 w-4 text-gray-300 group-hover:text-primary-500",
} as const;

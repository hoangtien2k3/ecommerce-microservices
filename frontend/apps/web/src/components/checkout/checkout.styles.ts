// Co-located Tailwind class tokens for checkout components.
export const checkoutStyles = {
  stepBar: "flex items-center mb-8",
  stepItem: "flex items-center flex-1",
  stepCircleBase: "w-10 h-10 rounded-full flex items-center justify-center border-2 transition-colors",
  stepCircleActive: "bg-primary-500 border-primary-500 text-white",
  stepCircleIdle: "border-gray-300 text-gray-400",
  stepLabelBase: "text-xs mt-1 font-medium hidden md:block",
  stepLabelActive: "text-primary-500",
  stepLabelIdle: "text-gray-400",
  stepLineBase: "flex-1 h-0.5 mx-2",
  stepLineActive: "bg-primary-500",
  stepLineIdle: "bg-gray-200",
} as const;

"use client";

import { cn } from "@ecommerce/lib/utils";

interface FilterSidebarProps {
  children: React.ReactNode;
  open: boolean;
  onClose: () => void;
  title?: string;
  className?: string;
}

export function FilterSidebar({ children, open, onClose, title, className }: FilterSidebarProps) {
  return (
    <aside
      className={cn(
        "w-64 flex-shrink-0 space-y-6",
        "fixed md:relative inset-0 z-40 bg-white md:bg-transparent p-4 md:p-0 overflow-y-auto md:overflow-visible",
        open ? "block" : "hidden md:block",
        className
      )}
    >
      <div className="flex items-center justify-between md:hidden mb-4">
        {title && <h3 className="font-semibold">{title}</h3>}
        <button onClick={onClose} className="p-1">
          <span className="text-lg">&times;</span>
        </button>
      </div>
      {children}
    </aside>
  );
}

interface FilterGroupProps {
  title: string;
  children: React.ReactNode;
  className?: string;
}

export function FilterGroup({ title, children, className }: FilterGroupProps) {
  return (
    <div className={cn("bg-white rounded-xl border border-gray-200 p-4", className)}>
      <h3 className="font-semibold text-gray-900 mb-3">{title}</h3>
      <div className="space-y-1">{children}</div>
    </div>
  );
}

interface FilterButtonProps {
  active?: boolean;
  onClick: () => void;
  children: React.ReactNode;
}

export function FilterButton({ active, onClick, children }: FilterButtonProps) {
  return (
    <button
      onClick={onClick}
      className={cn(
        "w-full text-left text-sm px-2 py-1.5 rounded-lg transition-colors",
        active
          ? "bg-orange-50 text-orange-600 font-medium"
          : "text-gray-700 hover:bg-gray-50"
      )}
    >
      {children}
    </button>
  );
}

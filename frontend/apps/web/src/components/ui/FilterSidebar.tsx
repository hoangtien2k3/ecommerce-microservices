"use client";

import { cn } from "@ecommerce/lib/utils";
import { uiStyles as s } from "./ui.styles";

interface FilterSidebarProps {
  children: React.ReactNode;
  open: boolean;
  onClose: () => void;
  title?: string;
  className?: string;
}

export function FilterSidebar({ children, open, onClose, title, className }: FilterSidebarProps) {
  return (
    <aside className={cn(s.sidebarBase, open ? s.sidebarOpen : s.sidebarClosed, className)}>
      <div className={s.sidebarMobileHead}>
        {title && <h3 className={s.sidebarMobileTitle}>{title}</h3>}
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
    <div className={cn(s.filterGroup, className)}>
      <h3 className={s.filterGroupTitle}>{title}</h3>
      <div className={s.filterGroupBody}>{children}</div>
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
      className={cn(s.filterBtnBase, active ? s.filterBtnActive : s.filterBtnIdle)}
    >
      {children}
    </button>
  );
}

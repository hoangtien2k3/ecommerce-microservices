"use client";

import { ChevronDown } from "lucide-react";
import { cn } from "@ecommerce/lib/utils";
import { uiStyles as s } from "./ui.styles";

interface SortSelectProps {
  options: { label: string; value: string }[];
  value: string;
  onChange: (value: string) => void;
  className?: string;
}

export function SortSelect({ options, value, onChange, className }: SortSelectProps) {
  return (
    <div className={cn("relative", className)}>
      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className={s.sortSelect}
      >
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
      <ChevronDown className={s.sortCaret} />
    </div>
  );
}

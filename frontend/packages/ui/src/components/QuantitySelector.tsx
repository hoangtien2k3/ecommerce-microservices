"use client";

import { Minus, Plus } from "lucide-react";
import { cn } from "@ecommerce/lib/utils";

interface QuantitySelectorProps {
  value: number;
  min?: number;
  max?: number;
  onChange: (value: number) => void;
  className?: string;
}

export function QuantitySelector({
  value,
  min = 1,
  max = 999,
  onChange,
  className,
}: QuantitySelectorProps) {
  return (
    <div className={cn("flex items-center border border-gray-300 rounded-lg overflow-hidden", className)}>
      <button
        onClick={() => onChange(Math.max(min, value - 1))}
        disabled={value <= min}
        className="px-2 py-1.5 hover:bg-gray-100 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
      >
        <Minus className="h-3.5 w-3.5" />
      </button>
      <span className="px-3 py-1.5 text-sm font-semibold border-x border-gray-300 min-w-[40px] text-center">
        {value}
      </span>
      <button
        onClick={() => onChange(Math.min(max, value + 1))}
        disabled={value >= max}
        className="px-2 py-1.5 hover:bg-gray-100 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
      >
        <Plus className="h-3.5 w-3.5" />
      </button>
    </div>
  );
}

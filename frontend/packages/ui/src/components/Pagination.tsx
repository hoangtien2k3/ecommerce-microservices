"use client";

import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "../Button";
import { cn } from "@ecommerce/lib/utils";

interface PaginationProps {
  current: number;
  total: number;
  onChange: (page: number) => void;
  className?: string;
}

export function Pagination({ current, total, onChange, className }: PaginationProps) {
  if (total <= 1) return null;

  return (
    <div className={cn("flex items-center justify-center gap-2", className)}>
      <Button variant="outline" size="sm" disabled={current === 0} onClick={() => onChange(current - 1)}>
        <ChevronLeft className="h-4 w-4" />
      </Button>
      {Array.from({ length: Math.min(total, 7) }).map((_, i) => (
        <button
          key={i}
          onClick={() => onChange(i)}
          className={cn(
            "w-9 h-9 rounded-lg text-sm font-medium transition-colors",
            current === i
              ? "bg-orange-500 text-white"
              : "border border-gray-300 text-gray-700 hover:border-orange-500"
          )}
        >
          {i + 1}
        </button>
      ))}
      <Button variant="outline" size="sm" disabled={current === total - 1} onClick={() => onChange(current + 1)}>
        <ChevronRight className="h-4 w-4" />
      </Button>
    </div>
  );
}

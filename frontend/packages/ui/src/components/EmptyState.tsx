"use client";

import { Loader2, Package } from "lucide-react";
import { cn } from "@ecommerce/lib/utils";

interface EmptyStateProps {
  icon?: React.ReactNode;
  title: string;
  description?: string;
  action?: React.ReactNode;
  className?: string;
}

export function EmptyState({ icon, title, description, action, className }: EmptyStateProps) {
  return (
    <div className={cn("text-center py-16", className)}>
      <div className="text-gray-200 mb-4 flex justify-center">
        {icon ?? <Package className="h-16 w-16" />}
      </div>
      <p className="text-lg font-semibold text-gray-700 mb-1">{title}</p>
      {description && <p className="text-gray-500 text-sm mb-4">{description}</p>}
      {action}
    </div>
  );
}

export function LoadingSkeleton({ rows = 5, className }: { rows?: number; className?: string }) {
  return (
    <div className={cn("space-y-3", className)}>
      {Array.from({ length: rows }).map((_, i) => (
        <div key={i} className="animate-pulse bg-white rounded-xl border border-gray-200 p-5">
          <div className="h-4 bg-gray-200 rounded w-1/3 mb-2" />
          <div className="h-3 bg-gray-200 rounded w-1/2" />
        </div>
      ))}
    </div>
  );
}

export function Spinner({ className }: { className?: string }) {
  return <Loader2 className={cn("animate-spin h-5 w-5 text-orange-500", className)} />;
}

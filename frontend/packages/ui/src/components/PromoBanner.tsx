"use client";

import { cn } from "@ecommerce/lib/utils";

interface PromoBannerProps {
  className?: string;
  badge?: string;
  title: string;
  description?: string;
  action?: React.ReactNode;
  gradient?: string;
}

export function PromoBanner({ className, badge, title, description, action, gradient }: PromoBannerProps) {
  return (
    <div className={cn("rounded-2xl p-6 text-white", gradient ?? "bg-gradient-to-r from-purple-600 to-purple-700", className)}>
      {badge && <p className="text-sm font-medium text-white/70 mb-1">{badge}</p>}
      <h3 className="text-2xl font-bold mb-2">{title}</h3>
      {description && <p className="text-sm text-white/80 mb-3">{description}</p>}
      {action}
    </div>
  );
}

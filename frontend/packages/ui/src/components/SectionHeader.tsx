"use client";

import { ChevronRight } from "lucide-react";
import { cn } from "@ecommerce/lib/utils";

interface SectionHeaderProps {
  title: string;
  linkHref?: string;
  linkLabel?: string;
  className?: string;
}

export function SectionHeader({ title, linkHref, linkLabel, className }: SectionHeaderProps) {
  return (
    <div className={cn("flex items-center justify-between mb-6", className)}>
      <h2 className="text-2xl font-bold text-gray-900">{title}</h2>
      {linkHref && linkLabel && (
        <a href={linkHref} className="flex items-center gap-1 text-sm text-orange-500 hover:text-orange-600 font-medium">
          {linkLabel} <ChevronRight className="h-4 w-4" />
        </a>
      )}
    </div>
  );
}

interface SectionProps {
  title?: string;
  linkHref?: string;
  linkLabel?: string;
  children: React.ReactNode;
  className?: string;
}

export function Section({ title, linkHref, linkLabel, children, className }: SectionProps) {
  return (
    <section className={cn("max-w-7xl mx-auto px-4", className)}>
      {(title || linkHref) && <SectionHeader title={title!} linkHref={linkHref} linkLabel={linkLabel} />}
      {children}
    </section>
  );
}

"use client";

import { cn } from "@ecommerce/lib/utils";

interface FeatureItem {
  icon: React.ElementType;
  title: string;
  description: string;
}

interface FeatureStripProps {
  items: FeatureItem[];
  className?: string;
}

export function FeatureStrip({ items, className }: FeatureStripProps) {
  return (
    <section className={cn("bg-white border-b", className)}>
      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {items.map(({ icon: Icon, title, description }) => (
            <div key={title} className="flex items-center gap-3 p-3 rounded-xl hover:bg-orange-50 transition-colors">
              <div className="w-10 h-10 bg-orange-100 rounded-xl flex items-center justify-center flex-shrink-0">
                <Icon className="h-5 w-5 text-orange-500" />
              </div>
              <div>
                <p className="text-sm font-semibold text-gray-900">{title}</p>
                <p className="text-xs text-gray-500">{description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

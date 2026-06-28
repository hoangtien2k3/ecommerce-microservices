"use client";

import Image from "next/image";
import { cn } from "@ecommerce/lib/utils";

interface CategoryGridItem {
  id: number | string;
  name: string;
  href: string;
  emoji?: string;
  imageUrl?: string;
  bgColor?: string;
}

interface CategoryGridProps {
  items: CategoryGridItem[];
  columns?: 4 | 8;
  className?: string;
}

export default function CategoryGrid({ items, columns = 8, className }: CategoryGridProps) {
  return (
    <div className={cn(
      "grid gap-3",
      columns === 8 ? "grid-cols-4 md:grid-cols-8" : "grid-cols-2 md:grid-cols-4",
      className
    )}>
      {items.map((cat) => (
        <a
          key={cat.id}
          href={cat.href}
          className="flex flex-col items-center gap-2 p-3 rounded-xl hover:shadow-md transition-all hover:-translate-y-1"
        >
          <div className={cn("w-12 h-12 rounded-xl flex items-center justify-center text-2xl", cat.bgColor ?? "bg-gray-100")}>
            {cat.emoji}
            {cat.imageUrl && <Image src={cat.imageUrl} alt={cat.name} width={48} height={48} className="object-cover rounded-xl" />}
          </div>
          <span className="text-xs text-center text-gray-700 font-medium">{cat.name}</span>
        </a>
      ))}
    </div>
  );
}

"use client";

import { useState, useRef, useEffect } from "react";
import { useTranslations } from "next-intl";
import { Menu, ChevronRight } from "lucide-react";
import { cn } from "@ecommerce/lib/utils";
import { Link } from "@/i18n/navigation";
import { categoryIcons } from "@/data/homeMock";

export default function CategoryMenu() {
  const t = useTranslations("Category");
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  return (
    <div className="relative hidden lg:block" ref={ref}>
      <button
        onClick={() => setOpen((o) => !o)}
        className="flex items-center gap-2 px-3 py-2 rounded-lg bg-primary-50 text-primary-600 font-medium text-sm hover:bg-primary-100 transition-colors whitespace-nowrap"
      >
        <Menu className="h-4 w-4" /> {t("menuTitle")}
      </button>

      {open && (
        <div className="absolute left-0 top-full mt-2 w-60 bg-white rounded-xl shadow-xl border border-gray-100 py-2 z-50">
          {categoryIcons.map((cat) => (
            <Link
              key={cat.id}
              href={cat.href}
              onClick={() => setOpen(false)}
              className={cn(
                "flex items-center gap-3 px-4 py-2.5 text-sm text-gray-700",
                "hover:bg-primary-50 hover:text-primary-600 transition-colors group"
              )}
            >
              <span className="text-lg">{cat.emoji}</span>
              <span className="flex-1">{t(cat.key)}</span>
              <ChevronRight className="h-4 w-4 text-gray-300 group-hover:text-primary-500" />
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}

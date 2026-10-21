"use client";

import { useState, useRef, useEffect } from "react";
import { useTranslations } from "next-intl";
import { Menu, ChevronRight } from "lucide-react";
import { Link } from "@/i18n/navigation";
import { useCategoryMenu } from "@/hooks";
import { categoryEmoji } from "@/data/homeMock";
import { categoryMenuStyles as s } from "./categoryMenu.styles";

export default function CategoryMenu() {
  const t = useTranslations("Category");
  const [open, setOpen] = useState(false);
  const items = useCategoryMenu();
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  return (
    <div className={s.root} ref={ref}>
      <button onClick={() => setOpen((o) => !o)} className={s.trigger}>
        <Menu className="h-4 w-4" /> {t("menuTitle")}
      </button>

      {open && (
        <div className={s.panel}>
          {items.map((item, i) => (
            <Link key={item.key} href={item.href} onClick={() => setOpen(false)} className={s.item}>
              <span className={s.itemEmoji}>{categoryEmoji(i)}</span>
              <span className={s.itemLabel}>{item.label}</span>
              <ChevronRight className={s.itemCaret} />
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}

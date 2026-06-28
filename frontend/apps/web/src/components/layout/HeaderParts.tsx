"use client";

import { useTranslations } from "next-intl";
import { Phone, MapPin, FileText, Store } from "lucide-react";
import { Link } from "@/i18n/navigation";

// Top utility bar (dark red): quick category links on the left, support links on
// the right — mirrors the CellphoneS top strip.
const TOP_CATS: Array<[string, string]> = [
  ["/products?categoryId=1", "c1"],
  ["/products?categoryId=2", "c2"],
  ["/products?categoryId=3", "c3"],
  ["/products?categoryId=4", "c4"],
  ["/products?categoryId=5", "c5"],
  ["/products?categoryId=7", "c7"],
];

export function TopBar() {
  const t = useTranslations("Header");
  const tCat = useTranslations("Category");
  return (
    <div className="bg-primary-700 text-white/90 text-xs">
      <div className="max-w-7xl mx-auto px-4 h-8 flex items-center justify-between">
        <nav className="flex items-center gap-4 overflow-x-auto no-scrollbar">
          {TOP_CATS.map(([href, key]) => (
            <Link key={key} href={href} className="hover:text-white whitespace-nowrap transition-colors">
              {tCat(key)}
            </Link>
          ))}
        </nav>
        <div className="flex items-center gap-4 shrink-0">
          <Link href="/orders" className="hover:text-white items-center gap-1 hidden sm:flex">
            <FileText className="h-3.5 w-3.5" /> {t("topTrackOrder")}
          </Link>
          <Link href="/stores" className="hover:text-white items-center gap-1 hidden md:flex">
            <Store className="h-3.5 w-3.5" /> {t("storeSystem")}
          </Link>
          <span className="flex items-center gap-1 font-medium text-white">
            <Phone className="h-3.5 w-3.5" /> {t("hotline")}
          </span>
        </div>
      </div>
    </div>
  );
}

export function Logo({ light = false }: { light?: boolean }) {
  return (
    <Link href="/" className="flex items-center gap-2 flex-shrink-0">
      <div className={light ? "w-9 h-9 bg-white rounded-lg flex items-center justify-center" : "w-9 h-9 bg-primary-500 rounded-lg flex items-center justify-center"}>
        <svg className={light ? "h-5 w-5 text-primary-600" : "h-5 w-5 text-white"} fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" /></svg>
      </div>
      <span className={light ? "text-xl font-bold text-white" : "text-xl font-bold text-gray-900"}>
        Ez<span className={light ? "text-amber-300" : "text-primary-500"}>Buy</span>
      </span>
    </Link>
  );
}

export function LocationPill() {
  return (
    <button className="hidden lg:flex items-center gap-1.5 text-white text-xs hover:bg-white/10 rounded-lg px-2.5 py-1.5 transition-colors">
      <MapPin className="h-4 w-4" />
      <span className="text-left leading-tight">
        Khu vực<br /><span className="font-semibold">Hồ Chí Minh</span>
      </span>
    </button>
  );
}

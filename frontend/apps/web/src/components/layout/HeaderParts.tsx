"use client";

import { useTranslations } from "next-intl";
import { Heart, Bell, Phone, MapPin, FileText } from "lucide-react";
import { Link } from "@/i18n/navigation";

export function TopBar() {
  const t = useTranslations("Header");
  return (
    <div className="bg-primary-600 text-white text-xs py-1.5">
      <div className="max-w-7xl mx-auto px-4 flex justify-between items-center">
        <span className="flex items-center gap-1.5 font-medium">
          <Phone className="h-3.5 w-3.5" /> {t("hotline")}
        </span>
        <div className="flex items-center gap-4">
          <Link href="/orders" className="hover:underline flex items-center gap-1">
            <FileText className="h-3.5 w-3.5" /> {t("topTrackOrder")}
          </Link>
          <Link href="/stores" className="hover:underline items-center gap-1 hidden sm:flex">
            <MapPin className="h-3.5 w-3.5" /> {t("storeSystem")}
          </Link>
        </div>
      </div>
    </div>
  );
}

export function Logo() {
  return (
    <Link href="/" className="flex items-center gap-2 flex-shrink-0">
      <div className="w-8 h-8 bg-orange-500 rounded-lg flex items-center justify-center">
        <svg className="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" /></svg>
      </div>
      <span className="text-xl font-bold text-gray-900">
        Ez<span className="text-orange-500">Buy</span>
      </span>
    </Link>
  );
}

export function WishlistIcon() {
  return (
    <Link href="/favourites" className="p-2 text-gray-600 hover:text-orange-500 transition-colors hidden md:flex">
      <Heart className="h-5 w-5" />
    </Link>
  );
}

export function NotificationBell() {
  return (
    <button className="p-2 text-gray-600 hover:text-orange-500 transition-colors relative hidden md:flex">
      <Bell className="h-5 w-5" />
    </button>
  );
}

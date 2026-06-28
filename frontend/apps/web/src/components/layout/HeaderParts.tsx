"use client";

import { Heart, Bell } from "lucide-react";
import { Link } from "@/i18n/navigation";

export function TopBar() {
  return (
    <div className="bg-orange-500 text-white text-xs py-1.5">
      <div className="max-w-7xl mx-auto px-4 flex justify-between items-center">
        <span>Free shipping for orders over $50</span>
        <div className="flex items-center gap-4">
          <Link href="/about" className="hover:underline">About</Link>
          <Link href="/contact" className="hover:underline">Contact</Link>
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

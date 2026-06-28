"use client";

import { useState, useEffect, useRef } from "react";
import { useTranslations } from "next-intl";
import {
  ShoppingCart, Search, User, Menu, X, Heart,
  ChevronDown, LogOut, Package, LayoutDashboard, Bell,
} from "lucide-react";
import { useCartStore } from "@ecommerce/lib/store";
import { useAuthStore } from "@ecommerce/lib/store";
import { useLogout } from "@/hooks";
import { Button } from "@ecommerce/ui";
import { cn } from "@ecommerce/lib/utils";
import { Link } from "@/i18n/navigation";
import LanguageSwitcher from "@/components/LanguageSwitcher";
import { TopBar, Logo } from "./HeaderParts";
import CategoryMenu from "./CategoryMenu";

export default function Header() {
  const t = useTranslations("Header");
  const tCat = useTranslations("Category");
  const { totalItems } = useCartStore();
  const { user, isAuthenticated } = useAuthStore();
  const logoutMutation = useLogout();
  const [searchQuery, setSearchQuery] = useState("");
  const [mobileOpen, setMobileOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const [cartCount, setCartCount] = useState(0);
  const profileRef = useRef<HTMLDivElement>(null);

  useEffect(() => { setCartCount(totalItems()); }, [totalItems]);

  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (profileRef.current && !profileRef.current.contains(e.target as Node)) {
        setProfileOpen(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  const isAdmin = !!user?.roles?.some((r) => r.name === "ADMIN" || r.name === "ROLE_ADMIN");

  return (
    <header className="sticky top-0 z-50 bg-white shadow-sm border-b border-gray-200">
      <TopBar />

      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center gap-4 h-16">
          <Logo />

          <CategoryMenu />

          {/* Search form */}
          <form
            onSubmit={(e) => {
              e.preventDefault();
              if (searchQuery.trim()) {
                window.location.href = `/products?search=${encodeURIComponent(searchQuery.trim())}`;
                setSearchQuery("");
              }
            }}
            className="flex-1 max-w-xl hidden md:flex"
          >
            <div className="flex w-full border-2 border-orange-500 rounded-lg overflow-hidden">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder={t("searchPlaceholder")}
                className="flex-1 px-4 py-2 text-sm text-gray-900 outline-none"
              />
              <button type="submit" className="bg-orange-500 px-4 text-white hover:bg-orange-600 transition-colors">
                <Search className="h-4 w-4" />
              </button>
            </div>
          </form>

          <div className="flex items-center gap-2 ml-auto">
            {isAuthenticated && (
              <Link href="/favourites" className="p-2 text-gray-600 hover:text-orange-500 transition-colors hidden md:flex">
                <Heart className="h-5 w-5" />
              </Link>
            )}
            {isAuthenticated && (
              <button className="p-2 text-gray-600 hover:text-orange-500 transition-colors relative hidden md:flex">
                <Bell className="h-5 w-5" />
              </button>
            )}

            <Link href="/cart" className="relative p-2 text-gray-600 hover:text-orange-500 transition-colors">
              <ShoppingCart className="h-5 w-5" />
              {cartCount > 0 && (
                <span className="absolute -top-0.5 -right-0.5 bg-orange-500 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center font-medium">
                  {cartCount > 9 ? "9+" : cartCount}
                </span>
              )}
            </Link>

            {isAuthenticated ? (
              <div className="relative" ref={profileRef}>
                <button
                  onClick={() => setProfileOpen(!profileOpen)}
                  className="flex items-center gap-2 p-1.5 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  <div className="w-8 h-8 bg-orange-500 rounded-full flex items-center justify-center text-white text-sm font-semibold">
                    {user?.fullName?.[0]?.toUpperCase() ?? "U"}
                  </div>
                  <span className="text-sm font-medium text-gray-700 hidden md:block max-w-[100px] truncate">
                    {user?.fullName}
                  </span>
                  <ChevronDown className={cn("h-4 w-4 text-gray-500 hidden md:block transition-transform", profileOpen && "rotate-180")} />
                </button>

                {profileOpen && (
                  <ProfileDropdown
                    user={user}
                    isAdmin={isAdmin}
                    onLogout={() => logoutMutation.mutate()}
                    onClose={() => setProfileOpen(false)}
                  />
                )}
              </div>
            ) : (
              <div className="hidden md:flex items-center gap-2">
                <Link href="/login"><Button variant="ghost" size="sm">{t("login")}</Button></Link>
                <Link href="/register"><Button size="sm">{t("register")}</Button></Link>
              </div>
            )}

            <button onClick={() => setMobileOpen(!mobileOpen)} className="p-2 text-gray-600 md:hidden">
              {mobileOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
            </button>
          </div>
        </div>

        <nav className="hidden md:flex items-center gap-5 pb-3 text-sm overflow-x-auto no-scrollbar">
          <Link href="/products" className="text-gray-600 hover:text-primary-600 font-medium transition-colors whitespace-nowrap">{t("allProducts")}</Link>
          {(["c1", "c2", "c3", "c4", "c5", "c9"] as const).map((key, i) => (
            <Link key={key} href={`/products?categoryId=${[1, 2, 3, 4, 5, 9][i]}`} className="text-gray-600 hover:text-primary-600 transition-colors whitespace-nowrap">{tCat(key)}</Link>
          ))}
          <Link href="/products?sort=priceUnit,asc" className="text-primary-600 font-semibold hover:text-primary-700 transition-colors whitespace-nowrap">{t("promo")}</Link>
        </nav>
      </div>

      {mobileOpen && <MobileMenu
        onSearch={(q) => { setSearchQuery(q); }}
        onClose={() => setMobileOpen(false)}
        isAuthenticated={isAuthenticated}
      />}
    </header>
  );
}

function ProfileDropdown({
  user, isAdmin, onLogout, onClose,
}: {
  user: { fullName?: string; email?: string } | null;
  isAdmin: boolean;
  onLogout: () => void;
  onClose: () => void;
}) {
  const t = useTranslations("Header");
  return (
    <div className="absolute right-0 top-full mt-2 w-56 bg-white rounded-xl shadow-lg border border-gray-100 py-1 z-50">
      <div className="px-4 py-3 border-b border-gray-100">
        <p className="text-sm font-semibold text-gray-900">{user?.fullName}</p>
        <p className="text-xs text-gray-500">{user?.email}</p>
      </div>
      <Link href="/profile" onClick={onClose} className="flex items-center gap-3 px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50">
        <User className="h-4 w-4" /> {t("myAccount")}
      </Link>
      <Link href="/orders" onClick={onClose} className="flex items-center gap-3 px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50">
        <Package className="h-4 w-4" /> {t("myOrders")}
      </Link>
      {isAdmin && (
        <Link href="/admin/dashboard" onClick={onClose} className="flex items-center gap-3 px-4 py-2.5 text-sm text-orange-600 hover:bg-orange-50">
          <LayoutDashboard className="h-4 w-4" /> {t("adminPanel")}
        </Link>
      )}
      <div className="border-t border-gray-100 mt-1">
        <button onClick={onLogout} className="flex items-center gap-3 px-4 py-2.5 text-sm text-red-500 hover:bg-red-50 w-full">
          <LogOut className="h-4 w-4" /> {t("logout")}
        </button>
      </div>
    </div>
  );
}

function MobileMenu({
  onSearch, onClose, isAuthenticated,
}: {
  onSearch: (q: string) => void;
  onClose: () => void;
  isAuthenticated: boolean;
}) {
  const t = useTranslations("Header");
  const [q, setQ] = useState("");

  return (
    <div className="md:hidden border-t border-gray-200 bg-white px-4 py-4 space-y-3">
      <form
        onSubmit={(e) => {
          e.preventDefault();
          if (q.trim()) window.location.href = `/products?search=${encodeURIComponent(q.trim())}`;
        }}
        className="flex border border-gray-300 rounded-lg overflow-hidden"
      >
        <input
          type="text" value={q} onChange={(e) => setQ(e.target.value)}
          placeholder={t("searchMobile")}
          className="flex-1 px-3 py-2 text-sm outline-none"
        />
        <button type="submit" className="bg-orange-500 px-3 text-white"><Search className="h-4 w-4" /></button>
      </form>
      {!isAuthenticated && (
        <div className="flex gap-2">
          <Link href="/login" onClick={onClose} className="flex-1"><Button variant="outline" className="w-full" size="sm">{t("login")}</Button></Link>
          <Link href="/register" onClick={onClose} className="flex-1"><Button className="w-full" size="sm">{t("register")}</Button></Link>
        </div>
      )}
      <nav className="space-y-1">
        {([
          ["/products", t("allProducts")],
          ["/cart", t("cart")],
          ["/orders", t("orders")],
          ["/favourites", t("favourites")],
        ] as const).map(([href, label]) => (
          <Link key={href} href={href} onClick={onClose} className="block py-2 text-sm text-gray-700 hover:text-orange-500">{label}</Link>
        ))}
      </nav>
      <div className="pt-2 border-t border-gray-100"><LanguageSwitcher /></div>
    </div>
  );
}

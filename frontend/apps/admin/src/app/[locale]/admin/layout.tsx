"use client";

import { useTranslations } from "next-intl";
import {
  LayoutDashboard, Package, ShoppingBag, Users, BarChart3,
  Tag, Truck, Settings, LogOut, Menu, X, ChevronRight,
} from "lucide-react";
import { useAuthStore } from "@ecommerce/lib/store";
import { useState } from "react";
import { cn } from "@ecommerce/lib/utils";
import { Link, usePathname, useRouter } from "@/i18n/navigation";
import LanguageSwitcher from "@/components/LanguageSwitcher";

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const t = useTranslations("Sidebar");
  const pathname = usePathname();
  const { user, logout } = useAuthStore();
  const router = useRouter();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const navItems = [
    { href: "/admin/dashboard", label: t("dashboard"), icon: LayoutDashboard },
    { href: "/admin/products",  label: t("products"),  icon: Package },
    { href: "/admin/orders",    label: t("orders"),    icon: ShoppingBag },
    { href: "/admin/users",     label: t("users"),     icon: Users },
    { href: "/admin/categories",label: t("categories"),icon: Tag },
    { href: "/admin/inventory", label: t("inventory"), icon: BarChart3 },
    { href: "/admin/shipping",  label: t("shipping"),  icon: Truck },
    { href: "/admin/settings",  label: t("settings"),  icon: Settings },
  ];

  const handleLogout = () => {
    logout();
    router.push("/");
  };

  return (
    <div className="min-h-screen bg-gray-100 flex">
      <aside className={cn(
        "fixed inset-y-0 left-0 z-50 w-64 bg-gray-900 text-white flex flex-col transition-transform",
        "md:relative md:translate-x-0",
        sidebarOpen ? "translate-x-0" : "-translate-x-full"
      )}>
        <div className="p-5 border-b border-gray-800">
          <Link href="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-orange-500 rounded-lg flex items-center justify-center">
              <Package className="h-5 w-5 text-white" />
            </div>
            <div>
              <p className="text-white font-bold leading-tight">{t("brand")}</p>
              <p className="text-gray-400 text-xs">{t("brandSub")}</p>
            </div>
          </Link>
        </div>

        <nav className="flex-1 p-4 space-y-1 overflow-y-auto">
          {navItems.map(({ href, label, icon: Icon }) => {
            const active = pathname === href || pathname.startsWith(href + "/");
            return (
              <Link
                key={href}
                href={href}
                onClick={() => setSidebarOpen(false)}
                className={cn(
                  "flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all",
                  active
                    ? "bg-orange-500 text-white"
                    : "text-gray-400 hover:bg-gray-800 hover:text-white"
                )}
              >
                <Icon className="h-4 w-4 flex-shrink-0" />
                {label}
                {active && <ChevronRight className="h-3.5 w-3.5 ml-auto" />}
              </Link>
            );
          })}
        </nav>

        <div className="p-4 border-t border-gray-800">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-9 h-9 bg-orange-500 rounded-full flex items-center justify-center text-white text-sm font-semibold flex-shrink-0">
              {user?.fullName?.[0]?.toUpperCase() ?? "A"}
            </div>
            <div className="min-w-0">
              <p className="text-sm font-medium text-white truncate">{user?.fullName}</p>
              <p className="text-xs text-gray-400 truncate">{user?.email}</p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-2 text-sm text-red-400 hover:text-red-300 transition-colors"
          >
            <LogOut className="h-4 w-4" />
            {t("logout")}
          </button>
        </div>
      </aside>

      {sidebarOpen && (
        <div className="fixed inset-0 bg-black/50 z-40 md:hidden" onClick={() => setSidebarOpen(false)} />
      )}

      <div className="flex-1 flex flex-col min-w-0">
        <header className="bg-white border-b border-gray-200 px-4 py-3 flex items-center gap-3 sticky top-0 z-30">
          <button onClick={() => setSidebarOpen(!sidebarOpen)} className="md:hidden p-1.5 rounded-lg hover:bg-gray-100">
            {sidebarOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
          <div className="flex-1">
            <p className="text-sm text-gray-500">
              {navItems.find(n => pathname.startsWith(n.href))?.label ?? "Admin"}
            </p>
          </div>
          <LanguageSwitcher />
          <Link href="/" className="text-sm text-orange-500 hover:text-orange-600">
            {t("backToStore")}
          </Link>
        </header>

        <main className="flex-1 p-6">
          {children}
        </main>
      </div>
    </div>
  );
}

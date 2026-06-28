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
import { adminLayoutStyles as s } from "./adminLayout.styles";

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
    <div className={s.root}>
      <aside className={cn(s.sidebarBase, sidebarOpen ? s.sidebarOpen : s.sidebarClosed)}>
        <div className={s.brandBox}>
          <Link href="/" className={s.brand}>
            <div className={s.brandLogo}>
              <Package className="h-5 w-5 text-white" />
            </div>
            <div>
              <p className={s.brandName}>{t("brand")}</p>
              <p className={s.brandSub}>{t("brandSub")}</p>
            </div>
          </Link>
        </div>

        <nav className={s.nav}>
          {navItems.map(({ href, label, icon: Icon }) => {
            const active = pathname === href || pathname.startsWith(href + "/");
            return (
              <Link
                key={href}
                href={href}
                onClick={() => setSidebarOpen(false)}
                className={cn(s.navItemBase, active ? s.navItemActive : s.navItemIdle)}
              >
                <Icon className="h-4 w-4 flex-shrink-0" />
                {label}
                {active && <ChevronRight className="h-3.5 w-3.5 ml-auto" />}
              </Link>
            );
          })}
        </nav>

        <div className={s.userBox}>
          <div className={s.userRow}>
            <div className={s.userAvatar}>
              {user?.fullName?.[0]?.toUpperCase() ?? "A"}
            </div>
            <div className={s.userInfo}>
              <p className={s.userName}>{user?.fullName}</p>
              <p className={s.userEmail}>{user?.email}</p>
            </div>
          </div>
          <button onClick={handleLogout} className={s.logoutBtn}>
            <LogOut className="h-4 w-4" />
            {t("logout")}
          </button>
        </div>
      </aside>

      {sidebarOpen && (
        <div className={s.overlay} onClick={() => setSidebarOpen(false)} />
      )}

      <div className={s.content}>
        <header className={s.topbar}>
          <button onClick={() => setSidebarOpen(!sidebarOpen)} className={s.topbarToggle}>
            {sidebarOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
          <div className={s.topbarTitle}>
            <p className={s.topbarCrumb}>
              {navItems.find(n => pathname.startsWith(n.href))?.label ?? "Admin"}
            </p>
          </div>
          <LanguageSwitcher />
          <Link href="/" className={s.backLink}>
            {t("backToStore")}
          </Link>
        </header>

        <main className={s.main}>
          {children}
        </main>
      </div>
    </div>
  );
}

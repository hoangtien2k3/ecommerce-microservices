import { getTranslations } from "next-intl/server";
import { Package } from "lucide-react";
import React from "react";
import { Link } from "@/i18n/navigation";
import { authStyles as s } from "@/components/auth/auth.styles";

export default async function AuthLayout({ children }: { children: React.ReactNode }) {
  const t = await getTranslations("Auth");

  return (
    <div className={s.shell}>
      <header className={s.shellHeader}>
        <Link href="/" className={s.brand}>
          <div className={s.brandLogo}>
            <Package className={s.brandLogoIcon} />
          </div>
          <span className={s.brandText}>
            Ez<span className={s.brandAccent}>Buy</span>
          </span>
        </Link>
      </header>

      <main className={s.shellMain}>
        {children}
      </main>

      <footer className={s.shellFooter}>
        {t("copyright")}
      </footer>
    </div>
  );
}

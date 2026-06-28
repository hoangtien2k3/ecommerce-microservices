import { useTranslations } from "next-intl";
import { Package, Phone, MapPin, Globe, MessageCircle, Send, Video, Smartphone } from "lucide-react";
import { Link } from "@/i18n/navigation";
import { footerStyles as s } from "./footer.styles";

export default function Footer() {
  const t = useTranslations("Footer");

  const infoLinks = [
    { href: "/help", label: t("helpCenter") },
    { href: "/warranty", label: t("warrantyPolicy") },
    { href: "/returns", label: t("returnPolicy") },
    { href: "/installment", label: t("installmentPolicy") },
    { href: "/shipping", label: t("shippingInfo") },
    { href: "/privacy", label: t("privacy") },
  ];

  const aboutLinks = [
    { href: "/about", label: t("aboutUs") },
    { href: "/contact", label: t("contact") },
    { href: "/orders", label: t("trackOrder") },
    { href: "/terms", label: t("terms") },
  ];

  return (
    <footer className={s.root}>
      <div className={s.inner}>
        <div className={s.grid}>
          {/* Support hotline */}
          <div>
            <h3 className={s.colTitle}>{t("supportTitle")}</h3>
            <p className={s.hotline}>{t("phone")}</p>
            <p className={s.hotlineNote}>{t("salesNote")}</p>
            <p className={`${s.hotline} mt-2`}>1800 2064</p>
            <p className={s.hotlineNote}>{t("warrantyNote")}</p>
          </div>

          {/* Information & policies */}
          <div>
            <h3 className={s.colTitle}>{t("infoTitle")}</h3>
            {infoLinks.map(({ href, label }) => (
              <Link key={href} href={href} className={s.link}>{label}</Link>
            ))}
          </div>

          {/* About */}
          <div>
            <h3 className={s.colTitle}>{t("aboutUs")}</h3>
            {aboutLinks.map(({ href, label }) => (
              <Link key={href} href={href} className={s.link}>{label}</Link>
            ))}
          </div>

          {/* Store system */}
          <div>
            <h3 className={s.colTitle}>{t("storeTitle")}</h3>
            <p className="text-sm text-gray-500 mb-1 flex items-start gap-2">
              <MapPin className="h-4 w-4 text-primary-500 shrink-0 mt-0.5" />
              {t("storeDesc")}
            </p>
            <Link href="/stores" className={s.link}>{t("viewStores")}</Link>
            <h3 className={`${s.colTitle} mt-4`}>{t("paymentTitle")}</h3>
            <div className={s.payRow}>
              {["VISA", "MOMO", "VNPAY", "ZALOPAY", "COD"].map((p) => (
                <span key={p} className={s.payChip}>{p}</span>
              ))}
            </div>
          </div>

          {/* Connect + app */}
          <div>
            <h3 className={s.colTitle}>{t("connectTitle")}</h3>
            <div className={s.social}>
              {[Globe, MessageCircle, Send, Video].map((Icon, i) => (
                <a key={i} href="#" className={s.socialIcon} aria-label="social">
                  <Icon className="h-4 w-4" />
                </a>
              ))}
            </div>
            <h3 className={`${s.colTitle} mt-4`}>{t("downloadApp")}</h3>
            <div className="flex flex-col gap-2 mt-1">
              <a href="#" className={s.appBtn}><Smartphone className="h-4 w-4" /> App Store</a>
              <a href="#" className={s.appBtn}><Smartphone className="h-4 w-4" /> Google Play</a>
            </div>
          </div>
        </div>
      </div>

      <div className={s.bottom}>
        <div className={s.bottomInner}>
          <span className="flex items-center gap-2">
            <span className="w-6 h-6 bg-primary-500 rounded flex items-center justify-center">
              <Package className="h-3.5 w-3.5 text-white" />
            </span>
            {t("copyright")}
          </span>
          <div className={s.bottomLinks}>
            <Link href="/privacy" className="hover:text-primary-600">{t("privacy")}</Link>
            <Link href="/terms" className="hover:text-primary-600">{t("terms")}</Link>
            <span className="flex items-center gap-1"><Phone className="h-3 w-3" /> {t("phone")}</span>
          </div>
        </div>
      </div>
    </footer>
  );
}

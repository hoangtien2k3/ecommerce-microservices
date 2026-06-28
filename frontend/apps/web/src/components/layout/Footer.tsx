import { useTranslations } from "next-intl";
import { Package, Mail, Phone, MapPin, Globe, MessageCircle, Send, Video } from "lucide-react";
import { Link } from "@/i18n/navigation";

export default function Footer() {
  const t = useTranslations("Footer");

  const categories = [
    { label: t("electronics"), slug: "electronics" },
    { label: t("fashion"),     slug: "fashion" },
    { label: t("homeLifestyle"), slug: "home" },
    { label: t("healthBeauty"), slug: "health" },
    { label: t("sports"),      slug: "sports" },
    { label: t("toys"),        slug: "toys" },
  ];

  const helpLinks = [
    { href: "/help",     label: t("helpCenter") },
    { href: "/orders",   label: t("trackOrder") },
    { href: "/returns",  label: t("returnPolicy") },
    { href: "/shipping", label: t("shippingInfo") },
    { href: "/contact",  label: t("contact") },
    { href: "/about",    label: t("aboutUs") },
  ];

  return (
    <footer className="bg-gray-900 text-gray-400 mt-auto">
      <div className="max-w-7xl mx-auto px-4 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Brand */}
          <div>
            <Link href="/" className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 bg-orange-500 rounded-lg flex items-center justify-center">
                <Package className="h-5 w-5 text-white" />
              </div>
              <span className="text-xl font-bold text-white">Ez<span className="text-orange-500">Buy</span></span>
            </Link>
            <p className="text-sm leading-relaxed mb-4">{t("tagline")}</p>
            <div className="flex gap-3">
              {[Globe, MessageCircle, Send, Video].map((Icon, i) => (
                <a key={i} href="#" className="w-8 h-8 bg-gray-800 rounded-lg flex items-center justify-center hover:bg-orange-500 transition-colors">
                  <Icon className="h-4 w-4 text-white" />
                </a>
              ))}
            </div>
          </div>

          {/* Categories */}
          <div>
            <h3 className="text-white font-semibold mb-4">{t("categories")}</h3>
            <ul className="space-y-2 text-sm">
              {categories.map(({ label, slug }) => (
                <li key={slug}>
                  <Link href={`/products?category=${encodeURIComponent(slug)}`} className="hover:text-orange-500 transition-colors">{label}</Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Help */}
          <div>
            <h3 className="text-white font-semibold mb-4">{t("support")}</h3>
            <ul className="space-y-2 text-sm">
              {helpLinks.map(({ href, label }) => (
                <li key={href}><Link href={href} className="hover:text-orange-500 transition-colors">{label}</Link></li>
              ))}
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h3 className="text-white font-semibold mb-4">{t("contactTitle")}</h3>
            <ul className="space-y-3 text-sm">
              <li className="flex items-start gap-3"><MapPin className="h-4 w-4 text-orange-500 flex-shrink-0 mt-0.5" /><span>{t("address")}</span></li>
              <li className="flex items-center gap-3"><Phone className="h-4 w-4 text-orange-500 flex-shrink-0" /><span>{t("phone")}</span></li>
              <li className="flex items-center gap-3"><Mail className="h-4 w-4 text-orange-500 flex-shrink-0" /><span>{t("email")}</span></li>
            </ul>
            <div className="mt-4">
              <p className="text-sm font-medium text-white mb-2">{t("newsletter")}</p>
              <div className="flex">
                <input type="email" placeholder={t("newsletterPlaceholder")} className="flex-1 bg-gray-800 text-white text-sm px-3 py-2 rounded-l-lg outline-none border border-gray-700 focus:border-orange-500" />
                <button className="bg-orange-500 text-white text-sm px-3 py-2 rounded-r-lg hover:bg-orange-600 transition-colors">{t("subscribe")}</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="border-t border-gray-800">
        <div className="max-w-7xl mx-auto px-4 py-4 flex flex-col md:flex-row justify-between items-center gap-2 text-xs">
          <span>{t("copyright")}</span>
          <div className="flex gap-4">
            <Link href="/privacy" className="hover:text-orange-500">{t("privacy")}</Link>
            <Link href="/terms" className="hover:text-orange-500">{t("terms")}</Link>
          </div>
        </div>
      </div>
    </footer>
  );
}

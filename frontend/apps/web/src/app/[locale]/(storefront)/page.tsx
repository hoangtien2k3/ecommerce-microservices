import { getTranslations } from "next-intl/server";
import { ArrowRight, Shield, Truck, RefreshCw, Headphones } from "lucide-react";
import { Button, SectionHeader, Section, FeatureStrip, PromoBanner } from "@ecommerce/ui";
import ProductGrid from "@/components/product/ProductGrid";
import CategoryGrid from "@/components/ui/CategoryGrid";
import { Link } from "@/i18n/navigation";

export default async function HomePage() {
  const t = await getTranslations("Home");

  const features = [
    { icon: Truck,        title: t("freeShipping"),  description: t("freeShippingDesc") },
    { icon: Shield,       title: t("securePay"),      description: t("securePayDesc") },
    { icon: RefreshCw,    title: t("easyReturn"),     description: t("easyReturnDesc") },
    { icon: Headphones,   title: t("support"),        description: t("supportDesc") },
  ];

  const categories = [
    { id: 1, name: t("cat1"), emoji: "📱", bgColor: "bg-blue-100", href: "/products?categoryId=1" },
    { id: 2, name: t("cat2"), emoji: "👗", bgColor: "bg-pink-100", href: "/products?categoryId=2" },
    { id: 3, name: t("cat3"), emoji: "🏠", bgColor: "bg-yellow-100", href: "/products?categoryId=3" },
    { id: 4, name: t("cat4"), emoji: "💊", bgColor: "bg-green-100", href: "/products?categoryId=4" },
    { id: 5, name: t("cat5"), emoji: "⚽", bgColor: "bg-orange-100", href: "/products?categoryId=5" },
    { id: 6, name: t("cat6"), emoji: "🧸", bgColor: "bg-purple-100", href: "/products?categoryId=6" },
    { id: 7, name: t("cat7"), emoji: "📚", bgColor: "bg-red-100", href: "/products?categoryId=7" },
    { id: 8, name: t("cat8"), emoji: "🚗", bgColor: "bg-gray-100", href: "/products?categoryId=8" },
  ];

  return (
    <div>
      <section className="bg-gradient-to-r from-orange-500 to-orange-600 text-white">
        <div className="max-w-7xl mx-auto px-4 py-16 md:py-20">
          <div className="max-w-2xl">
            <div className="inline-flex items-center gap-2 bg-white/20 backdrop-blur rounded-full px-3 py-1 text-sm mb-4">
              {t("flashSale")}
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-4 leading-tight">
              {t("heroTitle")}<br />
              <span className="text-yellow-300">{t("heroHighlight")}</span>
            </h1>
            <p className="text-orange-100 text-lg mb-8 max-w-md">{t("heroSubtitle")}</p>
            <div className="flex flex-wrap gap-3">
              <Link href="/products">
                <Button variant="secondary" size="lg" className="bg-white text-orange-500 hover:bg-orange-50 border-0 font-semibold">
                  {t("shopNow")}
                  <ArrowRight className="h-5 w-5" />
                </Button>
              </Link>
              <Link href="/promotions">
                <Button variant="outline" size="lg" className="border-white text-white hover:bg-white/10">
                  {t("viewPromos")}
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </section>

      <FeatureStrip items={features} />

      <Section className="py-10" title={t("featuredCategories")} linkHref="/products" linkLabel={t("shopNow")}>
        <CategoryGrid items={categories} />
      </Section>

      <Section className="pb-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <PromoBanner
            className="md:col-span-2"
            badge={t("promoTodayLabel")}
            title={t("promoElectronics")}
            gradient="bg-gradient-to-r from-purple-600 to-purple-700"
            action={
              <Link href="/products?categoryId=1">
                <Button size="sm" className="bg-white text-purple-600 hover:bg-purple-50 border-0 font-semibold">
                  {t("promoElectronicsBtn")}
                </Button>
              </Link>
            }
          />
          <PromoBanner
            badge={t("promoNewLabel")}
            title={t("promoNew")}
            gradient="bg-gradient-to-br from-green-500 to-emerald-600"
            action={
              <Link href="/register">
                <Button size="sm" className="bg-white text-green-600 hover:bg-green-50 border-0 font-semibold">
                  {t("promoNewBtn")}
                </Button>
              </Link>
            }
          />
        </div>
      </Section>

      <Section className="pb-12" title={t("featuredProducts")} linkHref="/products" linkLabel={t("shopNow")}>
        <ProductGrid size={8} />
      </Section>
    </div>
  );
}

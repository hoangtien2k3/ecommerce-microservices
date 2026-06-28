import { getTranslations } from "next-intl/server";
import { ArrowRight, Shield, Truck, RefreshCw, Headphones, ChevronRight } from "lucide-react";
import { Button } from "@ecommerce/ui";
import ProductGrid from "@/components/product/ProductGrid";
import { Link } from "@/i18n/navigation";

export default async function HomePage() {
  const t = await getTranslations("Home");

  const features = [
    { icon: Truck,        title: t("freeShipping"),  desc: t("freeShippingDesc") },
    { icon: Shield,       title: t("securePay"),      desc: t("securePayDesc") },
    { icon: RefreshCw,    title: t("easyReturn"),     desc: t("easyReturnDesc") },
    { icon: Headphones,   title: t("support"),        desc: t("supportDesc") },
  ];

  const categories = [
    { id: 1, name: t("cat1"), emoji: "📱", color: "bg-blue-100" },
    { id: 2, name: t("cat2"), emoji: "👗", color: "bg-pink-100" },
    { id: 3, name: t("cat3"), emoji: "🏠", color: "bg-yellow-100" },
    { id: 4, name: t("cat4"), emoji: "💊", color: "bg-green-100" },
    { id: 5, name: t("cat5"), emoji: "⚽", color: "bg-orange-100" },
    { id: 6, name: t("cat6"), emoji: "🧸", color: "bg-purple-100" },
    { id: 7, name: t("cat7"), emoji: "📚", color: "bg-red-100" },
    { id: 8, name: t("cat8"), emoji: "🚗", color: "bg-gray-100" },
  ];

  return (
    <div>
      {/* Hero Banner */}
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

      {/* Features */}
      <section className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 py-6">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {features.map(({ icon: Icon, title, desc }) => (
              <div key={title} className="flex items-center gap-3 p-3 rounded-xl hover:bg-orange-50 transition-colors">
                <div className="w-10 h-10 bg-orange-100 rounded-xl flex items-center justify-center flex-shrink-0">
                  <Icon className="h-5 w-5 text-orange-500" />
                </div>
                <div>
                  <p className="text-sm font-semibold text-gray-900">{title}</p>
                  <p className="text-xs text-gray-500">{desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Categories */}
      <section className="max-w-7xl mx-auto px-4 py-10">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-gray-900">{t("featuredCategories")}</h2>
          <Link href="/products" className="flex items-center gap-1 text-sm text-orange-500 hover:text-orange-600 font-medium">
            {t("shopNow")} <ChevronRight className="h-4 w-4" />
          </Link>
        </div>
        <div className="grid grid-cols-4 md:grid-cols-8 gap-3">
          {categories.map((cat) => (
            <Link
              key={cat.id}
              href={`/products?categoryId=${cat.id}`}
              className="flex flex-col items-center gap-2 p-3 rounded-xl hover:shadow-md transition-all hover:-translate-y-1"
            >
              <div className={`w-12 h-12 ${cat.color} rounded-xl flex items-center justify-center text-2xl`}>
                {cat.emoji}
              </div>
              <span className="text-xs text-center text-gray-700 font-medium">{cat.name}</span>
            </Link>
          ))}
        </div>
      </section>

      {/* Promo banners */}
      <section className="max-w-7xl mx-auto px-4 pb-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="md:col-span-2 bg-gradient-to-r from-purple-600 to-purple-700 rounded-2xl p-6 text-white">
            <p className="text-sm font-medium text-purple-200 mb-1">{t("promoTodayLabel")}</p>
            <h3 className="text-2xl font-bold mb-2">{t("promoElectronics")}</h3>
            <Link href="/products?categoryId=1">
              <Button size="sm" className="bg-white text-purple-600 hover:bg-purple-50 border-0 font-semibold">
                {t("promoElectronicsBtn")}
              </Button>
            </Link>
          </div>
          <div className="bg-gradient-to-br from-green-500 to-emerald-600 rounded-2xl p-6 text-white">
            <p className="text-sm font-medium text-green-200 mb-1">{t("promoNewLabel")}</p>
            <h3 className="text-2xl font-bold mb-2">{t("promoNew")}</h3>
            <Link href="/register">
              <Button size="sm" className="bg-white text-green-600 hover:bg-green-50 border-0 font-semibold">
                {t("promoNewBtn")}
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* Featured Products */}
      <section className="max-w-7xl mx-auto px-4 pb-12">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-gray-900">{t("featuredProducts")}</h2>
          <Link href="/products" className="flex items-center gap-1 text-sm text-orange-500 hover:text-orange-600 font-medium">
            {t("shopNow")} <ChevronRight className="h-4 w-4" />
          </Link>
        </div>
        <ProductGrid size={8} />
      </section>
    </div>
  );
}

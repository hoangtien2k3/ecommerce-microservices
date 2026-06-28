import { getTranslations } from "next-intl/server";
import { ShieldCheck, Truck, RefreshCw, Headphones } from "lucide-react";
import HeroSlider from "@/components/home/HeroSlider";
import QuickDeals from "@/components/home/QuickDeals";
import CategoryBar from "@/components/home/CategoryBar";
import FlashSale from "@/components/home/FlashSale";
import ProductRail from "@/components/home/ProductRail";
import BrandStrip from "@/components/home/BrandStrip";
import { hotProducts } from "@/data/homeMock";

export async function generateMetadata() {
  const t = await getTranslations("Home");
  return { title: t("metaTitle"), description: t("metaDesc") };
}

export default async function HomePage() {
  const t = await getTranslations("Home");

  // The hot list is split into two rails to mirror CellphoneS's "hot trend" and
  // "hàng mới về" sections. Backed by mock data until the product service is live.
  const hotTrend = hotProducts.slice(0, 5);
  const newArrivals = hotProducts.slice(5, 10);

  const usp = [
    { icon: ShieldCheck, title: t("uspGenuine"), desc: t("uspGenuineDesc") },
    { icon: Truck, title: t("uspShipping"), desc: t("uspShippingDesc") },
    { icon: RefreshCw, title: t("uspReturn"), desc: t("uspReturnDesc") },
    { icon: Headphones, title: t("uspSupport"), desc: t("uspSupportDesc") },
  ];

  return (
    <div className="bg-[#f4f4f4]">
      <div className="max-w-7xl mx-auto px-3 md:px-4 py-4 space-y-5">
        <HeroSlider />
        <QuickDeals />
        <CategoryBar />
        <FlashSale />
        <ProductRail title={t("hotProducts")} icon="🔥" products={hotTrend} viewAllLabel={t("viewAll")} />
        <ProductRail title={t("newArrivals")} icon="🆕" products={newArrivals} viewAllLabel={t("viewAll")} />
        <BrandStrip />

        {/* USP strip */}
        <section className="bg-white rounded-2xl p-4 md:p-5">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {usp.map(({ icon: Icon, title, desc }) => (
              <div key={title} className="flex items-center gap-3">
                <div className="w-11 h-11 rounded-full bg-primary-50 flex items-center justify-center shrink-0">
                  <Icon className="h-5 w-5 text-primary-600" />
                </div>
                <div>
                  <p className="text-sm font-semibold text-gray-900 leading-tight">{title}</p>
                  <p className="text-xs text-gray-500">{desc}</p>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}

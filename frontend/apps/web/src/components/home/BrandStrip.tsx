import { getTranslations } from "next-intl/server";
import { Link } from "@/i18n/navigation";
import { brands } from "@/data/homeMock";
import { homeStyles as s } from "./home.styles";

export default async function BrandStrip() {
  const t = await getTranslations("Home");

  return (
    <section className={s.section}>
      <h2 className="text-lg md:text-xl font-bold text-gray-900 mb-4">{t("featuredBrands")}</h2>
      <div className={s.brandGrid}>
        {brands.map((brand) => (
          <Link key={brand.id} href={brand.href} className={s.brandCard}>
            {brand.name}
          </Link>
        ))}
      </div>
    </section>
  );
}

import { ArrowRight } from "lucide-react";
import { Link } from "@/i18n/navigation";
import type { ShowcaseProduct, BrandChip } from "@/data/homeMock";
import ProductTile from "./ProductTile";
import { homeStyles as s } from "./home.styles";
import { cn } from "@ecommerce/lib/utils";

interface CategorySectionProps {
  title: string;
  icon?: string;
  href?: string;
  viewAllLabel: string;
  bannerTitle: string;
  bannerSub: string;
  bannerCta: string;
  bannerGradient: string;
  brands: BrandChip[];
  products: ShowcaseProduct[];
}

export default function CategorySection({
  title, icon, href = "/products", viewAllLabel,
  bannerTitle, bannerSub, bannerCta, bannerGradient, brands, products,
}: CategorySectionProps) {
  return (
    <section className={s.section}>
      {/* Header: title + brand chips + view all */}
      <div className="flex items-center gap-4 mb-4 flex-wrap">
        <h2 className={s.sectionTitle}>
          {icon && <span>{icon}</span>}
          {title}
        </h2>
        <div className={cn(s.chipRow, "flex-1 py-0")}>
          {brands.map((b) => (
            <Link key={b.id} href={b.href} className={cn(s.chip, s.chipInactive)}>
              {b.label}
            </Link>
          ))}
        </div>
        <Link href={href} className={s.viewAll}>
          {viewAllLabel} <ArrowRight className="h-4 w-4" />
        </Link>
      </div>

      {/* Left banner + product grid */}
      <div className={s.catBlockGrid}>
        <Link href={href} className={cn(s.catBlockBanner, bannerGradient)}>
          <div>
            <p className={s.catBlockBannerTitle}>{bannerTitle}</p>
            <p className={s.catBlockBannerSub}>{bannerSub}</p>
          </div>
          <span className={s.catBlockBannerCta}>
            {bannerCta} <ArrowRight className="h-3 w-3" />
          </span>
        </Link>

        <div className={s.productGrid}>
          {products.map((p) => (
            <ProductTile key={p.productId} product={p} />
          ))}
        </div>
      </div>
    </section>
  );
}

import { ArrowRight } from "lucide-react";
import { Link } from "@/i18n/navigation";
import type { ShowcaseProduct } from "@/data/homeMock";
import ProductTile from "./ProductTile";
import { homeStyles as s } from "./home.styles";

interface ProductRailProps {
  title: string;
  icon?: string;
  products: ShowcaseProduct[];
  href?: string;
  viewAllLabel?: string;
}

export default function ProductRail({
  title, icon, products, href = "/products", viewAllLabel = "Xem tất cả",
}: ProductRailProps) {
  return (
    <section className={s.section}>
      <div className="flex items-center justify-between mb-4">
        <h2 className={s.sectionTitle}>
          {icon && <span>{icon}</span>}
          {title}
        </h2>
        <Link href={href} className={s.viewAll}>
          {viewAllLabel} <ArrowRight className="h-4 w-4" />
        </Link>
      </div>

      <div className={s.railGrid}>
        {products.map((p) => (
          <ProductTile key={p.productId} product={p} />
        ))}
      </div>
    </section>
  );
}

"use client";

import { Star } from "lucide-react";
import { useTranslations } from "next-intl";
import { formatPrice, cn } from "@ecommerce/lib/utils";
import { Link } from "@/i18n/navigation";
import type { ShowcaseProduct } from "@/data/homeMock";
import { homeStyles as s } from "./home.styles";

interface ProductTileProps {
  product: ShowcaseProduct;
  className?: string;
}

export default function ProductTile({ product, className }: ProductTileProps) {
  const t = useTranslations("Home");
  const {
    productId, productTitle, priceUnit, oldPrice,
    discountPercent, rating = 5, sold, installment, badge,
  } = product;

  return (
    <Link href={`/products/${productId}`} className={cn("block h-full", className)}>
      <div className={s.tile}>
        {/* Image + discount badge */}
        <div className={s.tileImage}>
          <div className={s.tileImagePlaceholder}>
            <span className={s.tileImageEmoji}>📦</span>
          </div>
          {!!discountPercent && (
            <span className={s.tileDiscount}>-{discountPercent}%</span>
          )}
          {badge && <span className={s.tileBadge}>{badge}</span>}
        </div>

        {/* Title */}
        <h3 className={s.tileTitle}>{productTitle}</h3>

        {/* Price */}
        <div className={s.tilePriceRow}>
          <span className={s.tilePrice}>{formatPrice(priceUnit)}</span>
          {oldPrice && oldPrice > priceUnit && (
            <span className={s.tileOldPrice}>{formatPrice(oldPrice)}</span>
          )}
        </div>

        {/* Installment line */}
        {installment && (
          <p className={s.tileInstallment}>
            {t("installment")} <span className="text-primary-600 font-semibold">0%</span>
          </p>
        )}

        {/* Footer: rating + sold */}
        <div className={s.tileFooter}>
          <div className={s.tileStars}>
            {[...Array(5)].map((_, i) => (
              <Star key={i} className={cn("h-3 w-3", i < rating ? "fill-amber-400 text-amber-400" : "text-gray-200")} />
            ))}
          </div>
          {typeof sold === "number" && (
            <span className={s.tileSold}>{t("sold", { count: sold > 999 ? `${(sold / 1000).toFixed(1)}k` : sold })}</span>
          )}
        </div>
      </div>
    </Link>
  );
}

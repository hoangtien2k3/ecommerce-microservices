"use client";

import { Star, Heart } from "lucide-react";
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
    discountPercent, rating = 5, sold, memberExtra, installment,
  } = product;

  return (
    <Link href={`/products/${productId}`} className={cn("block h-full", className)}>
      <div className={s.tile}>
        {!!discountPercent && <span className={s.tileDiscount}>-{discountPercent}%</span>}
        <span className={s.tileFav}><Heart className="h-3.5 w-3.5 text-gray-400" /></span>

        <div className={s.tileImage}>
          <span className={s.tileImageEmoji}>📦</span>
        </div>

        <h3 className={s.tileTitle}>{productTitle}</h3>

        <div className={s.tilePriceRow}>
          <span className={s.tilePrice}>{formatPrice(priceUnit)}</span>
          {oldPrice && oldPrice > priceUnit && (
            <span className={s.tileOldPrice}>{formatPrice(oldPrice)}</span>
          )}
        </div>

        {installment && (
          <p className={s.tileInstallment}>
            {t("installment")} <span className="text-primary-600 font-semibold">0%</span>
          </p>
        )}

        {!!memberExtra && (
          <p className={s.tileMemberBox}>
            Smember giảm thêm{" "}
            <span className={s.tileMemberAmount}>{formatPrice(memberExtra)}</span>
          </p>
        )}

        <div className={s.tileFooter}>
          <div className={s.tileStars}>
            {[...Array(5)].map((_, i) => (
              <Star key={i} className={cn("h-3 w-3", i < rating ? "fill-amber-400 text-amber-400" : "text-gray-200")} />
            ))}
          </div>
          {typeof sold === "number" && (
            <span className={s.tileSold}>
              {t("sold", { count: sold > 999 ? `${(sold / 1000).toFixed(1)}k` : sold })}
            </span>
          )}
        </div>
      </div>
    </Link>
  );
}

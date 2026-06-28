"use client";

import { useState, useEffect } from "react";
import { useTranslations } from "next-intl";
import { Zap } from "lucide-react";
import { cn, formatPrice } from "@ecommerce/lib/utils";
import { Link } from "@/i18n/navigation";
import { flashSaleProducts, nextFlashSaleDeadline, type ShowcaseProduct } from "@/data/homeMock";
import { homeStyles as s } from "./home.styles";

function pad(n: number) {
  return n.toString().padStart(2, "0");
}

function FlashTile({ product }: { product: ShowcaseProduct }) {
  const { productId, productTitle, priceUnit, discountPercent, sold = 0, stockTotal = 100 } = product;
  const ratio = Math.min(100, Math.round((sold / stockTotal) * 100));

  return (
    <Link href={`/products/${productId}`} className="w-40 sm:w-44 shrink-0">
      <div className="bg-white rounded-xl p-2.5 h-full flex flex-col">
        <div className="relative aspect-square rounded-lg overflow-hidden mb-2 flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100">
          <span className="text-5xl opacity-30">📦</span>
          {!!discountPercent && (
            <span className="absolute top-0 left-0 bg-amber-400 text-amber-900 text-[11px] font-bold px-1.5 py-0.5 rounded-br-lg">
              -{discountPercent}%
            </span>
          )}
        </div>
        <h3 className="text-[13px] font-medium text-gray-800 line-clamp-2 min-h-9 mb-1 leading-snug">{productTitle}</h3>
        <p className="text-[15px] font-bold text-primary-600 mb-2">{formatPrice(priceUnit)}</p>
        {/* Sold progress bar */}
        <div className="mt-auto">
          <div className="relative h-4 rounded-full bg-amber-100 overflow-hidden">
            <div className="absolute inset-y-0 left-0 bg-gradient-to-r from-amber-400 to-primary-500" style={{ width: `${ratio}%` }} />
            <span className="absolute inset-0 flex items-center justify-center text-[10px] font-semibold text-primary-700">
              Đã bán {sold}
            </span>
          </div>
        </div>
      </div>
    </Link>
  );
}

export default function FlashSale() {
  const t = useTranslations("Home");
  const [deadline] = useState(nextFlashSaleDeadline);
  // Lazy initializer keeps the impure Date.now() out of the render body.
  const [remaining, setRemaining] = useState(() => deadline - Date.now());

  useEffect(() => {
    const timer = setInterval(() => setRemaining(deadline - Date.now()), 1000);
    return () => clearInterval(timer);
  }, [deadline]);

  const total = Math.max(0, remaining);
  const hours = Math.floor(total / 3_600_000);
  const minutes = Math.floor((total % 3_600_000) / 60_000);
  const seconds = Math.floor((total % 60_000) / 1000);

  return (
    <section className={s.flashWrap}>
      <div className={s.flashHeader}>
        <div className="flex items-center gap-4">
          <span className={s.flashTitle}>
            <Zap className="h-6 w-6 fill-amber-300 text-amber-300" />
            {t("flashSaleTitle")}
          </span>
          <div className={s.flashTabs}>
            <span className={cn(s.flashTab, "bg-white text-primary-600")}>{t("flashTabHot")}</span>
            <span className={cn(s.flashTab, "bg-white/15 text-white")}>{t("flashTabWeekend")}</span>
          </div>
        </div>
        <div className={s.flashCountWrap}>
          <span className={s.flashCountLabel}>{t("flashSaleEndsIn")}</span>
          <span className={s.flashCountBox}>{pad(hours)}</span>
          <span className="text-white font-bold">:</span>
          <span className={s.flashCountBox}>{pad(minutes)}</span>
          <span className="text-white font-bold">:</span>
          <span className={s.flashCountBox}>{pad(seconds)}</span>
        </div>
      </div>

      <div className={s.flashTrack}>
        {flashSaleProducts.map((p) => (
          <FlashTile key={p.productId} product={p} />
        ))}
      </div>
    </section>
  );
}

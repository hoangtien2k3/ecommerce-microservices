"use client";

import { useState, useEffect } from "react";
import { useTranslations } from "next-intl";
import { Zap, ArrowRight } from "lucide-react";
import { Link } from "@/i18n/navigation";
import { flashSaleProducts, nextFlashSaleDeadline } from "@/data/homeMock";
import ProductTile from "./ProductTile";

function pad(n: number) {
  return n.toString().padStart(2, "0");
}

function CountdownBox({ value }: { value: string }) {
  return (
    <span className="bg-gray-900 text-white text-sm font-bold rounded px-1.5 py-0.5 min-w-7 text-center">
      {value}
    </span>
  );
}

export default function FlashSale() {
  const t = useTranslations("Home");
  const [deadline] = useState(nextFlashSaleDeadline);
  const [remaining, setRemaining] = useState(deadline - Date.now());

  useEffect(() => {
    const timer = setInterval(() => setRemaining(deadline - Date.now()), 1000);
    return () => clearInterval(timer);
  }, [deadline]);

  const total = Math.max(0, remaining);
  const hours = Math.floor(total / 3_600_000);
  const minutes = Math.floor((total % 3_600_000) / 60_000);
  const seconds = Math.floor((total % 60_000) / 1000);

  return (
    <section className="bg-gradient-to-r from-primary-600 to-primary-500 rounded-2xl p-4 md:p-5">
      <div className="flex items-center justify-between mb-4 flex-wrap gap-2">
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1.5 text-white">
            <Zap className="h-6 w-6 fill-amber-300 text-amber-300" />
            <span className="text-lg md:text-xl font-bold uppercase tracking-wide">{t("flashSaleTitle")}</span>
          </div>
          <div className="flex items-center gap-1">
            <span className="text-white text-sm mr-1 hidden sm:inline">{t("flashSaleEndsIn")}</span>
            <CountdownBox value={pad(hours)} />
            <span className="text-white font-bold">:</span>
            <CountdownBox value={pad(minutes)} />
            <span className="text-white font-bold">:</span>
            <CountdownBox value={pad(seconds)} />
          </div>
        </div>
        <Link href="/products" className="text-white text-sm font-medium flex items-center gap-1 hover:underline">
          {t("viewAll")} <ArrowRight className="h-4 w-4" />
        </Link>
      </div>

      <div className="flex gap-3 overflow-x-auto no-scrollbar pb-1">
        {flashSaleProducts.map((p) => (
          <div key={p.productId} className="w-40 sm:w-44 shrink-0">
            <ProductTile product={p} />
          </div>
        ))}
      </div>
    </section>
  );
}

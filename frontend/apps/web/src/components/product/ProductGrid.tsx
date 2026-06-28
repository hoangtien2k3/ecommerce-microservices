"use client";

import { useTranslations } from "next-intl";
import { useProducts } from "@/hooks";
import { EmptyState } from "@ecommerce/ui";
import { ShoppingBag } from "lucide-react";
import ProductCard from "./ProductCard";
import { productStyles as s } from "./product.styles";

interface ProductGridProps {
  categoryId?: number;
  search?: string;
  page?: number;
  size?: number;
}

export default function ProductGrid({ categoryId, search, page = 0, size = 12 }: ProductGridProps) {
  const t = useTranslations("ProductGrid");
  const { data: products, isLoading, isError } = useProducts({ categoryId, search, page, size });

  if (isLoading) {
    return (
      <div className={s.grid}>
        {[...Array(8)].map((_, i) => (
          <div key={i} className={s.skeletonCard}>
            <div className={s.skeletonImage} />
            <div className={s.skeletonBody}>
              <div className="h-3 bg-gray-200 rounded w-1/2" />
              <div className="h-4 bg-gray-200 rounded" />
              <div className="h-4 bg-gray-200 rounded w-3/4" />
              <div className="h-5 bg-gray-200 rounded w-1/2" />
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (isError) {
    return (
      <div className={s.gridError}>
        <p>{t("loadError")}</p>
      </div>
    );
  }

  const items = products ?? [];

  if (items.length === 0) {
    return (
      <EmptyState
        icon={<ShoppingBag className="h-16 w-16 text-gray-300" />}
        title={t("notFound")}
        description={t("notFoundHint")}
      />
    );
  }

  return (
    <div className={s.grid}>
      {items.map((product) => (
        <ProductCard key={product.productId} product={product} />
      ))}
    </div>
  );
}

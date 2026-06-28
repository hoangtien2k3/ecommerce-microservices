"use client";

import { useState, useEffect, Suspense } from "react";
import { useSearchParams } from "next/navigation";
import { useTranslations } from "next-intl";
import { SlidersHorizontal } from "lucide-react";
import { useProducts, useCategories } from "@/hooks";
import { Button, Pagination, EmptyState } from "@ecommerce/ui";
import { cn } from "@ecommerce/lib/utils";
import ProductCard from "@/components/product/ProductCard";
import { SortSelect } from "@/components/ui/SortSelect";
import { FilterSidebar, FilterGroup, FilterButton } from "@/components/ui/FilterSidebar";
import { productsStyles as s } from "./products.styles";

const SORT_OPTIONS = [
  { label: "Newest", value: "productId,desc" },
  { label: "Price: Low-High", value: "priceUnit,asc" },
  { label: "Price: High-Low", value: "priceUnit,desc" },
  { label: "Name: A-Z", value: "productTitle,asc" },
];

const PRICE_RANGES = [
  [0, 100000, "Under 100,000"] as const,
  [100000, 500000, "100,000 - 500,000"] as const,
  [500000, 1000000, "500,000 - 1,000,000"] as const,
  [1000000, 5000000, "1,000,000 - 5,000,000"] as const,
  [5000000, 50000000, "Above 5,000,000"] as const,
];

function ProductsContent() {
  const t = useTranslations("Products");
  const tCommon = useTranslations("Common");
  const searchParams = useSearchParams();
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState("productId,desc");
  const [selectedCategory, setSelectedCategory] = useState<number | undefined>(
    searchParams.get("categoryId") ? Number(searchParams.get("categoryId")) : undefined
  );
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 50000000]);

  const search = searchParams.get("search") ?? undefined;

  const { data: categoriesData } = useCategories();
  const { data: productsData, isLoading } = useProducts({ page, sort, categoryId: selectedCategory, search });

  const categories = categoriesData?.data?.content ?? [];
  const products = productsData ?? [];
  const totalPages = productsData?.length ? Math.ceil(productsData.length / 12) : 0;

  useEffect(() => { setPage(0); }, [selectedCategory, sort, search]);

  return (
    <div className={s.page}>
      <div className={s.header}>
        <div>
          <h1 className={s.title}>
            {search ? t("searchResults", { query: search }) : t("title")}
          </h1>
        </div>
        <div className={s.toolbar}>
          <SortSelect options={SORT_OPTIONS} value={sort} onChange={setSort} />
          <Button variant="outline" size="sm" onClick={() => setFiltersOpen(!filtersOpen)} className="md:hidden">
            <SlidersHorizontal className="h-4 w-4" />
            {t("filter")}
          </Button>
        </div>
      </div>

      <div className={s.body}>
        <FilterSidebar open={filtersOpen} onClose={() => setFiltersOpen(false)} title={t("filtersTitle")}>
          <FilterGroup title={t("categoriesTitle")}>
            <FilterButton active={!selectedCategory} onClick={() => setSelectedCategory(undefined)}>
              {t("allCategories")}
            </FilterButton>
            {categories.map((cat) => (
              <FilterButton
                key={cat.categoryId}
                active={selectedCategory === cat.categoryId}
                onClick={() => setSelectedCategory(cat.categoryId)}
              >
                {cat.categoryTitle}
              </FilterButton>
            ))}
          </FilterGroup>

          <FilterGroup title={t("priceTitle")}>
            {PRICE_RANGES.map(([min, max, label]) => (
              <FilterButton
                key={label}
                active={priceRange[0] === min && priceRange[1] === max}
                onClick={() => setPriceRange([min, max])}
              >
                {label}
              </FilterButton>
            ))}
          </FilterGroup>
        </FilterSidebar>

        <div className={s.main}>
          {isLoading ? (
            <div className={s.grid}>
              {[...Array(12)].map((_, i) => (
                <div key={i} className={s.skeletonCard}>
                  <div className={s.skeletonImage} />
                  <div className={s.skeletonBody}>
                    <div className="h-3 bg-gray-200 rounded w-1/2" />
                    <div className="h-4 bg-gray-200 rounded" />
                    <div className="h-5 bg-gray-200 rounded w-1/3" />
                  </div>
                </div>
              ))}
            </div>
          ) : products.length === 0 ? (
            <EmptyState
              title={t("notFound")}
              description={t("notFoundHint")}
            />
          ) : (
            <>
              <div className={s.grid}>
                {products.map((product) => (
                  <ProductCard key={product.productId} product={product} />
                ))}
              </div>
              <Pagination
                current={page}
                total={totalPages}
                onChange={setPage}
                className="mt-8"
              />
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default function ProductsPage() {
  const t = useTranslations("Products");
  return (
    <Suspense fallback={<div className={s.fallback}>{t("loadingFallback")}</div>}>
      <ProductsContent />
    </Suspense>
  );
}

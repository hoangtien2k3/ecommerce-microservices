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
    <div className="max-w-7xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">
            {search ? t("searchResults", { query: search }) : t("title")}
          </h1>
        </div>
        <div className="flex items-center gap-3">
          <SortSelect options={SORT_OPTIONS} value={sort} onChange={setSort} />
          <Button variant="outline" size="sm" onClick={() => setFiltersOpen(!filtersOpen)} className="md:hidden">
            <SlidersHorizontal className="h-4 w-4" />
            {t("filter")}
          </Button>
        </div>
      </div>

      <div className="flex gap-6">
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

        <div className="flex-1 min-w-0">
          {isLoading ? (
            <div className="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {[...Array(12)].map((_, i) => (
                <div key={i} className="bg-white rounded-xl border animate-pulse">
                  <div className="aspect-square bg-gray-200 rounded-t-xl" />
                  <div className="p-3 space-y-2">
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
              <div className="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
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
    <Suspense fallback={<div className="max-w-7xl mx-auto px-4 py-8 text-center text-gray-500">{t("loadingFallback")}</div>}>
      <ProductsContent />
    </Suspense>
  );
}

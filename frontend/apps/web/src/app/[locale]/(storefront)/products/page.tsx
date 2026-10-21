"use client";

import { useState, Suspense } from "react";
import { useSearchParams } from "next/navigation";
import { useTranslations } from "next-intl";
import { SlidersHorizontal } from "lucide-react";
import { useProducts, useCategories } from "@/hooks";
import { Button, Pagination, EmptyState } from "@ecommerce/ui";
import ProductCard from "@/components/product/ProductCard";
import { SortSelect } from "@/components/ui/SortSelect";
import { FilterSidebar, FilterGroup, FilterButton } from "@/components/ui/FilterSidebar";
import { productsStyles as s } from "./products.styles";

const PAGE_SIZE = 12;

// 라벨은 messages 의 Products.sort* / price* 키
const SORT_OPTIONS = [
  { labelKey: "sortNewest", value: "productId,desc" },
  { labelKey: "sortPriceLow", value: "priceUnit,asc" },
  { labelKey: "sortPriceHigh", value: "priceUnit,desc" },
  { labelKey: "sortNameAZ", value: "productTitle,asc" },
] as const;

const PRICE_RANGES = [
  { labelKey: "price1", min: 0, max: 100000 },
  { labelKey: "price2", min: 100000, max: 500000 },
  { labelKey: "price3", min: 500000, max: 1000000 },
  { labelKey: "price4", min: 1000000, max: 5000000 },
  { labelKey: "price5", min: 5000000, max: undefined },
] as const;

type PriceRange = (typeof PRICE_RANGES)[number];

function ProductsContent() {
  const t = useTranslations("Products");
  const searchParams = useSearchParams();
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState("productId,desc");
  const [selectedCategory, setSelectedCategory] = useState<number | undefined>(
    searchParams.get("categoryId") ? Number(searchParams.get("categoryId")) : undefined
  );
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [priceRange, setPriceRange] = useState<PriceRange | undefined>(undefined);

  const search = searchParams.get("search") ?? undefined;

  const { data: categoriesData } = useCategories();
  // 백엔드가 전체 목록만 주므로 필터·정렬은 useProducts(select)에서, 페이지 나누기는 여기서 한다.
  const { data: productsData, isLoading } = useProducts({
    sort,
    categoryId: selectedCategory,
    search,
    minPrice: priceRange?.min,
    maxPrice: priceRange?.max,
  });

  // 사이드바에는 parent id 가 없는 카테고리만 보여 준다.
  const categories = (categoriesData?.data?.content ?? []).filter((c) => !c.parentCategory);
  const filtered = productsData ?? [];
  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  // 검색어가 바뀌어 결과가 줄면 범위를 넘지 않게 맞춘다.
  const currentPage = Math.min(page, Math.max(totalPages - 1, 0));
  const products = filtered.slice(currentPage * PAGE_SIZE, (currentPage + 1) * PAGE_SIZE);

  // 필터를 바꾸면 첫 페이지로
  const changeSort = (value: string) => { setSort(value); setPage(0); };
  const changeCategory = (id: number | undefined) => { setSelectedCategory(id); setPage(0); };
  const changePrice = (range: PriceRange | undefined) => { setPriceRange(range); setPage(0); };

  return (
    <div className={s.page}>
      <div className={s.header}>
        <div>
          <h1 className={s.title}>
            {search ? t("searchResults", { query: search }) : t("title")}
          </h1>
          {!isLoading && <p className="text-sm text-gray-500">{t("count", { count: filtered.length })}</p>}
        </div>
        <div className={s.toolbar}>
          <SortSelect
            options={SORT_OPTIONS.map((o) => ({ label: t(o.labelKey), value: o.value }))}
            value={sort}
            onChange={changeSort}
          />
          <Button variant="outline" size="sm" onClick={() => setFiltersOpen(!filtersOpen)} className="md:hidden">
            <SlidersHorizontal className="h-4 w-4" />
            {t("filter")}
          </Button>
        </div>
      </div>

      <div className={s.body}>
        <FilterSidebar open={filtersOpen} onClose={() => setFiltersOpen(false)} title={t("filtersTitle")}>
          <FilterGroup title={t("categoriesTitle")}>
            <FilterButton active={!selectedCategory} onClick={() => changeCategory(undefined)}>
              {t("allCategories")}
            </FilterButton>
            {categories.map((cat) => (
              <FilterButton
                key={cat.categoryId}
                active={selectedCategory === cat.categoryId}
                onClick={() => changeCategory(cat.categoryId)}
              >
                {cat.categoryTitle}
              </FilterButton>
            ))}
          </FilterGroup>

          <FilterGroup title={t("priceTitle")}>
            <FilterButton active={!priceRange} onClick={() => changePrice(undefined)}>
              {t("allCategories")}
            </FilterButton>
            {PRICE_RANGES.map((range) => (
              <FilterButton
                key={range.labelKey}
                active={priceRange?.labelKey === range.labelKey}
                onClick={() => changePrice(range)}
              >
                {t(range.labelKey)}
              </FilterButton>
            ))}
          </FilterGroup>
        </FilterSidebar>

        <div className={s.main}>
          {isLoading ? (
            <div className={s.grid}>
              {[...Array(PAGE_SIZE)].map((_, i) => (
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
                current={currentPage}
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

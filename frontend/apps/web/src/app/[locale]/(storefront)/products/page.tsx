"use client";

import { useState, Suspense } from "react";
import { useSearchParams } from "next/navigation";
import { useTranslations } from "next-intl";
import { SlidersHorizontal } from "lucide-react";
import { useProducts, useCategoryTree } from "@/hooks";
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
  const tCat = useTranslations("Category");
  const searchParams = useSearchParams();
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState("productId,desc");
  const [selectedCategory, setSelectedCategory] = useState<number | undefined>(
    searchParams.get("categoryId") ? Number(searchParams.get("categoryId")) : undefined
  );
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [priceRange, setPriceRange] = useState<PriceRange | undefined>(undefined);

  const search = searchParams.get("search") ?? undefined;

  const { parents, childrenOf, findById } = useCategoryTree();
  // 선택한 카테고리가 소분류면 그 부모가, 대분류면 자기가 사이드바 그룹이 된다.
  // 소분류가 없는 카테고리(대분류를 지워 평평해진 경우 포함)는 그룹 없이 전체 목록에서 고른다.
  const selected = selectedCategory !== undefined ? findById(selectedCategory) : undefined;
  const groupCandidate = selected ? (selected.parentCategory ? findById(selected.parentCategory.categoryId) : selected) : undefined;
  const group = groupCandidate && childrenOf(groupCandidate.categoryId).length > 0 ? groupCandidate : undefined;
  // 대분류를 고르면 그 아래 소분류 상품까지 모두 보여 준다.
  const categoryIds =
    selectedCategory === undefined
      ? undefined
      : selected && !selected.parentCategory
        ? [selectedCategory, ...childrenOf(selectedCategory).map((c) => c.categoryId)]
        : [selectedCategory];
  // 백엔드가 전체 목록만 주므로 필터·정렬은 useProducts(select)에서, 페이지 나누기는 여기서 한다.
  const { data: productsData, isLoading } = useProducts({
    sort,
    categoryIds,
    search,
    minPrice: priceRange?.min,
    maxPrice: priceRange?.max,
  });

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
            {search ? t("searchResults", { query: search }) : selected?.categoryTitle ?? t("title")}
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
          {group ? (
            <FilterGroup title={group.categoryTitle}>
              <FilterButton active={false} onClick={() => changeCategory(undefined)}>
                ← {tCat("all")}
              </FilterButton>
              <FilterButton active={selectedCategory === group.categoryId} onClick={() => changeCategory(group.categoryId)}>
                {t("allCategories")}
              </FilterButton>
              {childrenOf(group.categoryId).map((cat) => (
                <FilterButton
                  key={cat.categoryId}
                  active={selectedCategory === cat.categoryId}
                  onClick={() => changeCategory(cat.categoryId)}
                >
                  {cat.categoryTitle}
                </FilterButton>
              ))}
            </FilterGroup>
          ) : (
            <FilterGroup title={t("categoriesTitle")}>
              <FilterButton active={!selectedCategory} onClick={() => changeCategory(undefined)}>
                {t("allCategories")}
              </FilterButton>
              {parents.map((cat) => (
                <FilterButton
                  key={cat.categoryId}
                  active={selectedCategory === cat.categoryId}
                  onClick={() => changeCategory(cat.categoryId)}
                >
                  {cat.categoryTitle}
                </FilterButton>
              ))}
            </FilterGroup>
          )}

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

// 메뉴에서 다른 카테고리 링크(/products?categoryId=..)를 누르면 주소만 바뀌므로,
// key 로 화면 상태(선택 카테고리·페이지·필터)를 새로 시작한다.
function ProductsRoute() {
  const searchParams = useSearchParams();
  return <ProductsContent key={searchParams.get("categoryId") ?? "all"} />;
}

export default function ProductsPage() {
  const t = useTranslations("Products");
  return (
    <Suspense fallback={<div className={s.fallback}>{t("loadingFallback")}</div>}>
      <ProductsRoute />
    </Suspense>
  );
}

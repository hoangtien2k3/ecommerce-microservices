"use client";

import { useState, useEffect, Suspense } from "react";
import { useSearchParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { SlidersHorizontal, ChevronDown, X } from "lucide-react";
import { categoryApi, productApi } from "@/lib/api";
import ProductCard from "@/components/product/ProductCard";
import type { ApiResponse, Category, PaginatedResponse, Product } from "@/types";
import { Button } from "@/components/ui/Button";
import { cn } from "@/lib/utils";

const SORT_OPTIONS = [
  { label: "Mới nhất", value: "productId,desc" },
  { label: "Giá thấp đến cao", value: "priceUnit,asc" },
  { label: "Giá cao đến thấp", value: "priceUnit,desc" },
  { label: "Tên A-Z", value: "productTitle,asc" },
];

function ProductsContent() {
  const searchParams = useSearchParams();
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState("productId,desc");
  const [selectedCategory, setSelectedCategory] = useState<number | undefined>(
    searchParams.get("categoryId") ? Number(searchParams.get("categoryId")) : undefined
  );
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 50000000]);

  const search = searchParams.get("search") ?? undefined;

  const { data: categoriesData } = useQuery({
    queryKey: ["categories"],
    queryFn: async () => {
      const res = await categoryApi.getAll({ page: 0, size: 50 });
      return res.data as ApiResponse<PaginatedResponse<Category>>;
    },
  });

  const { data: productsData, isLoading } = useQuery({
    queryKey: ["products", { page, sort, selectedCategory, search }],
    queryFn: async () => {
      const res = await productApi.getAll({
        page,
        size: 12,
        sort,
        categoryId: selectedCategory,
      });
      return res.data as ApiResponse<PaginatedResponse<Product>>;
    },
  });

  const categories = categoriesData?.data?.content ?? [];
  const products = productsData?.data?.content ?? [];
  const totalPages = productsData?.data?.totalPages ?? 0;
  const totalElements = productsData?.data?.totalElements ?? 0;

  useEffect(() => {
    setPage(0);
  }, [selectedCategory, sort, search]);

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">
            {search ? `Kết quả cho "${search}"` : "Tất cả sản phẩm"}
          </h1>
          <p className="text-sm text-gray-500 mt-1">{totalElements} sản phẩm</p>
        </div>
        <div className="flex items-center gap-3">
          {/* Sort */}
          <div className="relative">
            <select
              value={sort}
              onChange={(e) => setSort(e.target.value)}
              className="appearance-none border border-gray-300 rounded-lg px-3 py-2 pr-8 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-orange-500"
            >
              {SORT_OPTIONS.map((opt) => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
            <ChevronDown className="absolute right-2 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400 pointer-events-none" />
          </div>
          {/* Filter toggle (mobile) */}
          <Button
            variant="outline"
            size="sm"
            onClick={() => setFiltersOpen(!filtersOpen)}
            className="md:hidden"
          >
            <SlidersHorizontal className="h-4 w-4" />
            Lọc
          </Button>
        </div>
      </div>

      <div className="flex gap-6">
        {/* Sidebar Filters */}
        <aside className={cn(
          "w-64 flex-shrink-0 space-y-6",
          "fixed md:relative inset-0 z-40 bg-white md:bg-transparent p-4 md:p-0 overflow-y-auto md:overflow-visible",
          filtersOpen ? "block" : "hidden md:block"
        )}>
          <div className="flex items-center justify-between md:hidden">
            <h3 className="font-semibold">Bộ lọc</h3>
            <button onClick={() => setFiltersOpen(false)}><X className="h-5 w-5" /></button>
          </div>

          {/* Categories */}
          <div className="bg-white rounded-xl border border-gray-200 p-4">
            <h3 className="font-semibold text-gray-900 mb-3">Danh mục</h3>
            <ul className="space-y-1">
              <li>
                <button
                  onClick={() => setSelectedCategory(undefined)}
                  className={cn(
                    "w-full text-left text-sm px-2 py-1.5 rounded-lg transition-colors",
                    !selectedCategory ? "bg-orange-50 text-orange-600 font-medium" : "text-gray-700 hover:bg-gray-50"
                  )}
                >
                  Tất cả
                </button>
              </li>
              {categories.map((cat) => (
                <li key={cat.categoryId}>
                  <button
                    onClick={() => setSelectedCategory(cat.categoryId)}
                    className={cn(
                      "w-full text-left text-sm px-2 py-1.5 rounded-lg transition-colors",
                      selectedCategory === cat.categoryId
                        ? "bg-orange-50 text-orange-600 font-medium"
                        : "text-gray-700 hover:bg-gray-50"
                    )}
                  >
                    {cat.categoryTitle}
                  </button>
                </li>
              ))}
            </ul>
          </div>

          {/* Price range */}
          <div className="bg-white rounded-xl border border-gray-200 p-4">
            <h3 className="font-semibold text-gray-900 mb-3">Giá</h3>
            <div className="space-y-2">
              {[
                [0, 100000, "Dưới 100.000đ"],
                [100000, 500000, "100k - 500k"],
                [500000, 1000000, "500k - 1 triệu"],
                [1000000, 5000000, "1 - 5 triệu"],
                [5000000, 50000000, "Trên 5 triệu"],
              ].map(([min, max, label]) => (
                <button
                  key={String(label)}
                  onClick={() => setPriceRange([Number(min), Number(max)])}
                  className={cn(
                    "w-full text-left text-sm px-2 py-1.5 rounded-lg transition-colors",
                    priceRange[0] === min && priceRange[1] === max
                      ? "bg-orange-50 text-orange-600 font-medium"
                      : "text-gray-700 hover:bg-gray-50"
                  )}
                >
                  {String(label)}
                </button>
              ))}
            </div>
          </div>
        </aside>

        {/* Product grid */}
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
            <div className="text-center py-20 text-gray-500">
              <p className="text-lg font-medium">Không tìm thấy sản phẩm</p>
              <p className="text-sm mt-1">Thử thay đổi bộ lọc</p>
              {selectedCategory && (
                <Button variant="outline" size="sm" className="mt-4" onClick={() => setSelectedCategory(undefined)}>
                  Xóa bộ lọc
                </Button>
              )}
            </div>
          ) : (
            <>
              <div className="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                {products.map((product) => (
                  <ProductCard key={product.productId} product={product} />
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="flex items-center justify-center gap-2 mt-8">
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={page === 0}
                    onClick={() => setPage(page - 1)}
                  >
                    Trước
                  </Button>
                  {[...Array(Math.min(totalPages, 7))].map((_, i) => (
                    <button
                      key={i}
                      onClick={() => setPage(i)}
                      className={cn(
                        "w-9 h-9 rounded-lg text-sm font-medium transition-colors",
                        page === i ? "bg-orange-500 text-white" : "border border-gray-300 text-gray-700 hover:border-orange-500"
                      )}
                    >
                      {i + 1}
                    </button>
                  ))}
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={page === totalPages - 1}
                    onClick={() => setPage(page + 1)}
                  >
                    Sau
                  </Button>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default function ProductsPage() {
  return (
    <Suspense fallback={<div className="max-w-7xl mx-auto px-4 py-8 text-center text-gray-500">Đang tải...</div>}>
      <ProductsContent />
    </Suspense>
  );
}

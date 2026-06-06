"use client";

import { useQuery } from "@tanstack/react-query";
import { productApi } from "@/lib/api";
import ProductCard from "./ProductCard";
import type { Product, ApiResponse, PaginatedResponse } from "@/types";
import { ShoppingBag } from "lucide-react";

interface ProductGridProps {
  categoryId?: number;
  search?: string;
  page?: number;
  size?: number;
}

export default function ProductGrid({
  categoryId,
  search,
  page = 0,
  size = 12,
}: ProductGridProps) {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["products", { categoryId, search, page, size }],
    queryFn: async (): Promise<Product[]> => {
      const res = await productApi.getAll({ page, size, categoryId });
      // product-service returns Flux<List<ProductDto>> which serializes as [[...]]
      const raw = res.data;
      if (Array.isArray(raw) && Array.isArray(raw[0])) return raw[0] as Product[];
      if (Array.isArray(raw)) return raw as Product[];
      // ApiResponse wrapper format
      const wrapped = raw as ApiResponse<PaginatedResponse<Product>>;
      return wrapped?.data?.content ?? [];
    },
  });

  if (isLoading) {
    return (
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {[...Array(8)].map((_, i) => (
          <div key={i} className="bg-white rounded-xl border border-gray-200 overflow-hidden animate-pulse">
            <div className="aspect-square bg-gray-200" />
            <div className="p-3 space-y-2">
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
      <div className="text-center py-12 text-gray-500">
        <p>Không thể tải sản phẩm. Vui lòng thử lại.</p>
      </div>
    );
  }

  const products = data ?? [];

  if (products.length === 0) {
    return (
      <div className="text-center py-16 text-gray-500">
        <ShoppingBag className="h-16 w-16 mx-auto mb-4 text-gray-300" />
        <p className="text-lg font-medium">Không tìm thấy sản phẩm nào</p>
        <p className="text-sm mt-1">Thử thay đổi bộ lọc hoặc tìm kiếm từ khóa khác</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      {products.map((product) => (
        <ProductCard key={product.productId} product={product} />
      ))}
    </div>
  );
}

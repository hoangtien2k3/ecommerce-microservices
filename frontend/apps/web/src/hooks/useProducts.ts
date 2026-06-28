import { useQuery } from "@tanstack/react-query";
import { productApi } from "@ecommerce/lib/api";
import type { ApiResponse, PaginatedResponse, Product } from "@ecommerce/lib/types";

interface ProductFilters {
  page?: number;
  size?: number;
  sort?: string;
  categoryId?: number;
  search?: string;
}

export function useProducts(filters: ProductFilters = {}) {
  const { page = 0, size = 12, sort, categoryId, search } = filters;

  return useQuery({
    queryKey: ["products", { page, size, sort, categoryId, search }],
    queryFn: async () => {
      const res = await productApi.getAll({ page, size, sort, categoryId });
      const raw = res.data;
      if (Array.isArray(raw) && Array.isArray(raw[0])) return raw[0] as Product[];
      if (Array.isArray(raw)) return raw as Product[];
      const wrapped = raw as ApiResponse<PaginatedResponse<Product>>;
      return wrapped?.data?.content ?? [];
    },
  });
}

export function useProduct(id: string | number) {
  const productId = typeof id === "string" ? Number(id) : id;

  return useQuery({
    queryKey: ["product", productId],
    queryFn: async () => {
      const res = await productApi.getById(productId);
      return (res.data as ApiResponse<Product>).data ?? null;
    },
    enabled: !!productId,
  });
}

export function useAdminProducts(page = 0) {
  return useQuery({
    queryKey: ["admin-products-list", page],
    queryFn: async () => {
      const res = await productApi.getAll({ page, size: 10 });
      return res.data as ApiResponse<PaginatedResponse<Product>>;
    },
  });
}

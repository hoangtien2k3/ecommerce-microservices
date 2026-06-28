import { useQuery } from "@tanstack/react-query";
import { productApi } from "@ecommerce/lib/api";
import type { ApiResponse, PaginatedResponse, Product } from "@ecommerce/lib/types";

export function useAdminProducts(page = 0) {
  return useQuery({
    queryKey: ["admin-products-list", page],
    queryFn: async () => {
      const res = await productApi.getAll({ page, size: 10 });
      return res.data as ApiResponse<PaginatedResponse<Product>>;
    },
  });
}

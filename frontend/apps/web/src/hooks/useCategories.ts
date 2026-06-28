import { useQuery } from "@tanstack/react-query";
import { categoryApi } from "@ecommerce/lib/api";
import type { ApiResponse, PaginatedResponse, Category } from "@ecommerce/lib/types";

export function useCategories(page = 0, size = 50) {
  return useQuery({
    queryKey: ["categories", { page, size }],
    queryFn: async () => {
      const res = await categoryApi.getAll({ page, size });
      return res.data as ApiResponse<PaginatedResponse<Category>>;
    },
  });
}

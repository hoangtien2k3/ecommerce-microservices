import { useQuery } from "@tanstack/react-query";
import { orderApi } from "@ecommerce/lib/api";
import type { ApiResponse, PaginatedResponse, Order } from "@ecommerce/lib/types";

export function useAdminOrders(page = 0) {
  return useQuery({
    queryKey: ["admin-orders-list", page],
    queryFn: async () => {
      const res = await orderApi.getAll({ page, size: 10 });
      return res.data as ApiResponse<PaginatedResponse<Order>>;
    },
  });
}

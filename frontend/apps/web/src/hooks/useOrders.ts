import { useQuery } from "@tanstack/react-query";
import { orderApi } from "@ecommerce/lib/api";
import type { ApiResponse, PaginatedResponse, Order } from "@ecommerce/lib/types";

// 백엔드 GET /api/orders 는 ApiResponse 없이 주문 배열(List<OrderDto>)을 그대로 준다.
export function useOrders() {
  return useQuery({
    queryKey: ["my-orders"],
    queryFn: async () => {
      const res = await orderApi.getAll();
      const raw = res.data;
      if (Array.isArray(raw)) return raw as Order[];
      const wrapped = raw as ApiResponse<PaginatedResponse<Order>>;
      return wrapped?.data?.content ?? [];
    },
  });
}

export function useAdminOrders(page = 0) {
  return useQuery({
    queryKey: ["admin-orders-list", page],
    queryFn: async () => {
      const res = await orderApi.getAll({ page, size: 10 });
      return res.data as ApiResponse<PaginatedResponse<Order>>;
    },
  });
}

"use client";

import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { ShoppingBag, Package, Clock, ArrowRight } from "lucide-react";
import { orderApi } from "@/lib/api";
import { useAuthStore } from "@/store/authStore";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { formatPrice, formatDate } from "@/lib/utils";
import type { ApiResponse, PaginatedResponse, Order } from "@/types";
import { useEffect } from "react";

export default function OrdersPage() {
  const router = useRouter();
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (!isAuthenticated) router.replace("/login?redirect=/orders");
  }, [isAuthenticated, router]);

  const { data, isLoading } = useQuery({
    queryKey: ["my-orders"],
    queryFn: async () => {
      const res = await orderApi.getAll({ page: 0, size: 20 });
      return res.data as ApiResponse<PaginatedResponse<Order>>;
    },
    enabled: isAuthenticated,
  });

  const orders = data?.data?.content ?? [];

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <div className="flex items-center gap-2 mb-6">
        <Package className="h-6 w-6 text-orange-500" />
        <h1 className="text-2xl font-bold text-gray-900">Đơn hàng của tôi</h1>
      </div>

      {isLoading ? (
        <div className="space-y-3">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="bg-white rounded-xl border border-gray-200 p-5 animate-pulse">
              <div className="h-5 bg-gray-200 rounded w-1/4 mb-3" />
              <div className="h-4 bg-gray-200 rounded w-1/2" />
            </div>
          ))}
        </div>
      ) : orders.length === 0 ? (
        <div className="text-center py-16">
          <ShoppingBag className="h-20 w-20 text-gray-200 mx-auto mb-4" />
          <p className="text-lg font-semibold text-gray-700 mb-2">Chưa có đơn hàng nào</p>
          <p className="text-gray-500 text-sm mb-6">Hãy mua sắm và đặt hàng đầu tiên của bạn!</p>
          <Link href="/products">
            <Button>
              Mua sắm ngay <ArrowRight className="h-4 w-4" />
            </Button>
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <div key={order.orderId} className="bg-white rounded-xl border border-gray-200 p-5 hover:shadow-md transition-shadow">
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2">
                  <ShoppingBag className="h-5 w-5 text-orange-500" />
                  <span className="font-bold text-gray-900">Đơn #{order.orderId}</span>
                </div>
                <Badge variant="warning">Đang xử lý</Badge>
              </div>
              <div className="grid grid-cols-2 gap-4 text-sm text-gray-600">
                <div className="flex items-center gap-2">
                  <Clock className="h-4 w-4 text-gray-400" />
                  <span>{order.orderDate ? formatDate(order.orderDate) : "—"}</span>
                </div>
                <div className="text-right">
                  <span className="font-bold text-orange-500 text-base">{formatPrice(order.orderFee ?? 0)}</span>
                </div>
              </div>
              {order.orderDesc && (
                <p className="text-sm text-gray-500 mt-2">{order.orderDesc}</p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

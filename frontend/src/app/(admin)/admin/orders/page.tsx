"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { ShoppingBag, Search, Eye, Clock } from "lucide-react";
import { orderApi } from "@/lib/api";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { formatPrice, formatDate } from "@/lib/utils";
import type { ApiResponse, PaginatedResponse, Order } from "@/types";

export default function AdminOrdersPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");

  const { data, isLoading } = useQuery({
    queryKey: ["admin-orders-list", page],
    queryFn: async () => {
      const res = await orderApi.getAll({ page, size: 10 });
      return res.data as ApiResponse<PaginatedResponse<Order>>;
    },
  });

  const orders = data?.data?.content ?? [];
  const totalPages = data?.data?.totalPages ?? 0;

  const filtered = orders.filter(
    (o) => !search || String(o.orderId).includes(search)
  );

  return (
    <div className="space-y-5">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Đơn hàng</h1>
        <p className="text-sm text-gray-500 mt-0.5">{data?.data?.totalElements ?? 0} đơn hàng</p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-4">
        <Input
          placeholder="Tìm theo mã đơn hàng..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          leftIcon={<Search className="h-4 w-4" />}
          className="max-w-sm"
        />
      </div>

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-200 bg-gray-50">
                <th className="text-left px-4 py-3 font-semibold text-gray-700">Mã đơn</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-700">Ngày đặt</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-700">Mô tả</th>
                <th className="text-right px-4 py-3 font-semibold text-gray-700">Tổng tiền</th>
                <th className="text-center px-4 py-3 font-semibold text-gray-700">Trạng thái</th>
                <th className="text-center px-4 py-3 font-semibold text-gray-700">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {isLoading ? (
                [...Array(5)].map((_, i) => (
                  <tr key={i} className="border-b border-gray-100">
                    {[...Array(6)].map((_, j) => (
                      <td key={j} className="px-4 py-3">
                        <div className="h-4 bg-gray-200 rounded animate-pulse" />
                      </td>
                    ))}
                  </tr>
                ))
              ) : filtered.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center py-12 text-gray-500">
                    <ShoppingBag className="h-12 w-12 mx-auto mb-2 text-gray-300" />
                    Chưa có đơn hàng nào
                  </td>
                </tr>
              ) : (
                filtered.map((order) => (
                  <tr key={order.orderId} className="border-b border-gray-100 hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2">
                        <div className="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
                          <ShoppingBag className="h-4 w-4 text-blue-600" />
                        </div>
                        <span className="font-medium text-gray-900">#{order.orderId}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-gray-500">
                      <div className="flex items-center gap-1">
                        <Clock className="h-3.5 w-3.5" />
                        {order.orderDate ? formatDate(order.orderDate) : "—"}
                      </div>
                    </td>
                    <td className="px-4 py-3 text-gray-600 max-w-[200px] truncate">
                      {order.orderDesc ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-right font-bold text-orange-500">
                      {formatPrice(order.orderFee ?? 0)}
                    </td>
                    <td className="px-4 py-3 text-center">
                      <Badge variant="warning">Đang xử lý</Badge>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex justify-center">
                        <button className="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                          <Eye className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div className="flex justify-center gap-2 p-4 border-t border-gray-200">
            <Button variant="outline" size="sm" disabled={page === 0} onClick={() => setPage(page - 1)}>Trước</Button>
            <span className="px-3 py-1.5 text-sm text-gray-600">Trang {page + 1}/{totalPages}</span>
            <Button variant="outline" size="sm" disabled={page === totalPages - 1} onClick={() => setPage(page + 1)}>Sau</Button>
          </div>
        )}
      </div>
    </div>
  );
}

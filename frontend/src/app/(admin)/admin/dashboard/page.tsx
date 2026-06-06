"use client";

import { useQuery } from "@tanstack/react-query";
import { ShoppingBag, Package, Users, TrendingUp, ArrowUpRight, ArrowDownRight, Clock } from "lucide-react";
import { orderApi, productApi } from "@/lib/api";
import { formatPrice } from "@/lib/utils";
import type { ApiResponse, PaginatedResponse, Order, Product } from "@/types";

export default function DashboardPage() {
  const { data: ordersData } = useQuery({
    queryKey: ["admin-orders"],
    queryFn: async () => {
      const res = await orderApi.getAll({ page: 0, size: 10 });
      return res.data as ApiResponse<PaginatedResponse<Order>>;
    },
  });

  const { data: productsData } = useQuery({
    queryKey: ["admin-products"],
    queryFn: async () => {
      const res = await productApi.getAll({ page: 0, size: 10 });
      return res.data as ApiResponse<PaginatedResponse<Product>>;
    },
  });

  const orders = ordersData?.data?.content ?? [];
  const products = productsData?.data?.content ?? [];
  const totalOrders = ordersData?.data?.totalElements ?? 0;
  const totalProducts = productsData?.data?.totalElements ?? 0;
  const revenue = orders.reduce((sum, o) => sum + (o.orderFee ?? 0), 0);

  const stats = [
    {
      label: "Tổng doanh thu",
      value: formatPrice(revenue),
      icon: TrendingUp,
      color: "bg-green-100 text-green-600",
      change: "+12.5%",
      up: true,
    },
    {
      label: "Đơn hàng",
      value: totalOrders.toLocaleString(),
      icon: ShoppingBag,
      color: "bg-blue-100 text-blue-600",
      change: "+8.2%",
      up: true,
    },
    {
      label: "Sản phẩm",
      value: totalProducts.toLocaleString(),
      icon: Package,
      color: "bg-orange-100 text-orange-600",
      change: "+3.1%",
      up: true,
    },
    {
      label: "Người dùng",
      value: "—",
      icon: Users,
      color: "bg-purple-100 text-purple-600",
      change: "-2.4%",
      up: false,
    },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-500 text-sm mt-1">Tổng quan hệ thống</p>
      </div>

      {/* Stats grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        {stats.map(({ label, value, icon: Icon, color, change, up }) => (
          <div key={label} className="bg-white rounded-xl border border-gray-200 p-5">
            <div className="flex items-center justify-between mb-3">
              <div className={`w-10 h-10 ${color} rounded-xl flex items-center justify-center`}>
                <Icon className="h-5 w-5" />
              </div>
              <span className={`flex items-center gap-1 text-xs font-medium ${up ? "text-green-500" : "text-red-500"}`}>
                {up ? <ArrowUpRight className="h-3.5 w-3.5" /> : <ArrowDownRight className="h-3.5 w-3.5" />}
                {change}
              </span>
            </div>
            <p className="text-2xl font-bold text-gray-900">{value}</p>
            <p className="text-sm text-gray-500 mt-0.5">{label}</p>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
        {/* Recent Orders */}
        <div className="bg-white rounded-xl border border-gray-200 p-5">
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-bold text-gray-900">Đơn hàng gần đây</h2>
            <a href="/admin/orders" className="text-xs text-orange-500 hover:text-orange-600">Xem tất cả →</a>
          </div>
          {orders.length === 0 ? (
            <div className="text-center py-8 text-gray-500 text-sm">Chưa có đơn hàng</div>
          ) : (
            <div className="space-y-3">
              {orders.slice(0, 5).map((order) => (
                <div key={order.orderId} className="flex items-center gap-3 p-3 rounded-lg hover:bg-gray-50">
                  <div className="w-9 h-9 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                    <ShoppingBag className="h-4 w-4 text-blue-600" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900">Đơn #{order.orderId}</p>
                    <p className="text-xs text-gray-500 flex items-center gap-1">
                      <Clock className="h-3 w-3" />
                      {order.orderDate ? new Date(order.orderDate).toLocaleDateString("vi-VN") : "—"}
                    </p>
                  </div>
                  <p className="text-sm font-bold text-orange-500">{formatPrice(order.orderFee ?? 0)}</p>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Recent Products */}
        <div className="bg-white rounded-xl border border-gray-200 p-5">
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-bold text-gray-900">Sản phẩm</h2>
            <a href="/admin/products" className="text-xs text-orange-500 hover:text-orange-600">Xem tất cả →</a>
          </div>
          {products.length === 0 ? (
            <div className="text-center py-8 text-gray-500 text-sm">Chưa có sản phẩm</div>
          ) : (
            <div className="space-y-3">
              {products.slice(0, 5).map((product) => (
                <div key={product.productId} className="flex items-center gap-3 p-3 rounded-lg hover:bg-gray-50">
                  <div className="w-10 h-10 bg-orange-50 rounded-lg flex items-center justify-center flex-shrink-0 overflow-hidden">
                    {product.imageUrl ? (
                      // eslint-disable-next-line @next/next/no-img-element
                      <img src={product.imageUrl} alt={product.productTitle} className="w-full h-full object-cover rounded-lg" />
                    ) : (
                      <Package className="h-5 w-5 text-orange-300" />
                    )}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900 truncate">{product.productTitle}</p>
                    <p className="text-xs text-gray-500">{product.category?.categoryTitle ?? "Chưa phân loại"}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-bold text-orange-500">{formatPrice(product.priceUnit)}</p>
                    <p className={`text-xs ${product.quantity > 0 ? "text-green-600" : "text-red-500"}`}>
                      {product.quantity > 0 ? `${product.quantity} sp` : "Hết hàng"}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

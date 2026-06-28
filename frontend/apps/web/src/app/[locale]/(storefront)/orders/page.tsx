"use client";

import { useEffect } from "react";
import { useTranslations } from "next-intl";
import { ShoppingBag, Package, Clock, ArrowRight } from "lucide-react";
import { useOrders } from "@/hooks";
import { useAuthStore } from "@ecommerce/lib/store";
import { Badge, Button, EmptyState, LoadingSkeleton } from "@ecommerce/ui";
import { formatPrice, formatDate } from "@ecommerce/lib/utils";
import { Link, useRouter } from "@/i18n/navigation";

export default function OrdersPage() {
  const router = useRouter();
  const t = useTranslations("Orders");
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (!isAuthenticated) router.replace("/login?redirect=/orders");
  }, [isAuthenticated, router]);

  const { data, isLoading } = useOrders();
  const orders = data?.data?.content ?? [];

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <div className="flex items-center gap-2 mb-6">
        <Package className="h-6 w-6 text-orange-500" />
        <h1 className="text-2xl font-bold text-gray-900">{t("title")}</h1>
      </div>

      {isLoading ? (
        <LoadingSkeleton rows={3} />
      ) : orders.length === 0 ? (
        <EmptyState
          icon={<ShoppingBag className="h-20 w-20 text-gray-200" />}
          title={t("emptyTitle")}
          description={t("emptyHint")}
          action={
            <Link href="/products">
              <Button>{t("shopNow")} <ArrowRight className="h-4 w-4" /></Button>
            </Link>
          }
        />
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <OrderCard key={order.orderId} order={order} />
          ))}
        </div>
      )}
    </div>
  );
}

function OrderCard({ order }: { order: { orderId: number; orderDate: string; orderFee: number; orderDesc?: string } }) {
  const t = useTranslations("Orders");
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-5 hover:shadow-md transition-shadow">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <ShoppingBag className="h-5 w-5 text-orange-500" />
          <span className="font-bold text-gray-900">{t("orderLabel", { id: order.orderId })}</span>
        </div>
        <Badge variant="warning">{t("processing")}</Badge>
      </div>
      <div className="grid grid-cols-2 gap-4 text-sm text-gray-600">
        <div className="flex items-center gap-2">
          <Clock className="h-4 w-4 text-gray-400" />
          <span>{order.orderDate ? formatDate(order.orderDate) : "\u2014"}</span>
        </div>
        <div className="text-right">
          <span className="font-bold text-orange-500 text-base">{formatPrice(order.orderFee ?? 0)}</span>
        </div>
      </div>
      {order.orderDesc && (
        <p className="text-sm text-gray-500 mt-2">{order.orderDesc}</p>
      )}
    </div>
  );
}

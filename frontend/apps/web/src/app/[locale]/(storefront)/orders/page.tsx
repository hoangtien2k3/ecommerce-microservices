"use client";

import { useEffect } from "react";
import { useTranslations } from "next-intl";
import { ShoppingBag, Package, Clock, ArrowRight } from "lucide-react";
import { useOrders } from "@/hooks";
import { useAuthStore } from "@ecommerce/lib/store";
import { Badge, Button, EmptyState, LoadingSkeleton } from "@ecommerce/ui";
import { formatPrice, formatDate } from "@ecommerce/lib/utils";
import { Link, useRouter } from "@/i18n/navigation";
import { ordersStyles as s } from "./orders.styles";

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
    <div className={s.page}>
      <div className={s.heading}>
        <Package className="h-6 w-6 text-primary-500" />
        <h1 className={s.title}>{t("title")}</h1>
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
        <div className={s.list}>
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
    <div className={s.card}>
      <div className={s.cardHeader}>
        <div className={s.cardId}>
          <ShoppingBag className="h-5 w-5 text-primary-500" />
          <span className={s.cardIdText}>{t("orderLabel", { id: order.orderId })}</span>
        </div>
        <Badge variant="warning">{t("processing")}</Badge>
      </div>
      <div className={s.cardMeta}>
        <div className={s.cardDate}>
          <Clock className="h-4 w-4 text-gray-400" />
          <span>{order.orderDate ? formatDate(order.orderDate) : "\u2014"}</span>
        </div>
        <div className={s.cardFee}>
          <span className={s.cardFeeValue}>{formatPrice(order.orderFee ?? 0)}</span>
        </div>
      </div>
      {order.orderDesc && (
        <p className={s.cardDesc}>{order.orderDesc}</p>
      )}
    </div>
  );
}

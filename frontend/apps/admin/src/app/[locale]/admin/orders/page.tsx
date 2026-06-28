"use client";

import { useState } from "react";
import { useTranslations } from "next-intl";
import { ShoppingBag, Eye, Clock } from "lucide-react";
import { useAdminOrders } from "@/hooks";
import { Badge, DataTable } from "@ecommerce/ui";
import { formatPrice, formatDate } from "@ecommerce/lib/utils";
import type { Order } from "@ecommerce/lib/types";
import { ordersStyles as s } from "./orders.styles";

export default function AdminOrdersPage() {
  const t = useTranslations("Orders");
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");

  const { data, isLoading } = useAdminOrders(page);

  const orders = data?.data?.content ?? [];
  const totalPages = data?.data?.totalPages ?? 0;
  const filtered = orders.filter((o) => !search || String(o.orderId).includes(search));

  const columns = [
    {
      key: "id",
      header: t("colId"),
      render: (o: Order) => (
        <div className={s.idCell}>
          <div className={s.idIcon}>
            <ShoppingBag className="h-4 w-4 text-blue-600" />
          </div>
          <span className={s.idText}>{t("order", { id: o.orderId })}</span>
        </div>
      ),
    },
    {
      key: "date",
      header: t("colDate"),
      render: (o: Order) => (
        <div className={s.dateCell}>
          <Clock className="h-3.5 w-3.5" />
          {o.orderDate ? formatDate(o.orderDate) : "\u2014"}
        </div>
      ),
    },
    { key: "desc", header: t("colDesc"), render: (o: Order) => (
      <span className={s.descCell}>{o.orderDesc ?? "\u2014"}</span>
    )},
    {
      key: "total",
      header: t("colTotal"),
      className: "text-right font-bold text-primary-500",
      render: (o: Order) => formatPrice(o.orderFee ?? 0),
    },
    {
      key: "status",
      header: t("colStatus"),
      className: "text-center",
      render: () => <Badge variant="warning">{t("processing")}</Badge>,
    },
    {
      key: "actions",
      header: t("colActions"),
      className: "text-center",
      render: () => (
        <button className={s.viewBtn}>
          <Eye className="h-4 w-4" />
        </button>
      ),
    },
  ];

  return (
    <div className={s.root}>
      <div>
        <h1 className={s.title}>{t("title")}</h1>
        <p className={s.count}>{t("count", { count: data?.data?.totalElements ?? 0 })}</p>
      </div>

      <DataTable
        columns={columns}
        data={filtered}
        isLoading={isLoading}
        search={search}
        onSearchChange={setSearch}
        searchPlaceholder={t("searchPlaceholder")}
        rowKey={(o) => o.orderId}
        page={page}
        totalPages={totalPages}
        onPageChange={setPage}
        emptyMessage={t("noOrders")}
      />
    </div>
  );
}

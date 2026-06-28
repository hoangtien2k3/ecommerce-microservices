"use client";

import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { Plus, Edit, Trash2, Package } from "lucide-react";
import { useAdminProducts, useCategories } from "@/hooks";
import { productApi } from "@ecommerce/lib/api";
import { Button, Badge, DataTable, Modal, ModalBody, ModalFooter, Input } from "@ecommerce/ui";
import { formatPrice } from "@ecommerce/lib/utils";
import type { Product } from "@ecommerce/lib/types";

type ProductForm = {
  productTitle: string; imageUrl: string; sku: string;
  priceUnit: string; quantity: string; categoryId: string;
};

const INITIAL_FORM: ProductForm = {
  productTitle: "", imageUrl: "", sku: "", priceUnit: "", quantity: "", categoryId: "",
};

export default function AdminProductsPage() {
  const t = useTranslations("Products");
  const tCommon = useTranslations("Common");
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [editProduct, setEditProduct] = useState<Product | null>(null);
  const [form, setForm] = useState<ProductForm>(INITIAL_FORM);

  const { data, isLoading } = useAdminProducts(page);
  const { data: categoriesData } = useCategories();

  const deleteMutation = useMutation({
    mutationFn: (id: number) => productApi.delete(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-products-list"] }),
  });

  const saveMutation = useMutation({
    mutationFn: (data: unknown) =>
      editProduct ? productApi.update(editProduct.productId, data) : productApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin-products-list"] });
      closeModal();
    },
  });

  const products = data?.data?.content ?? [];
  const totalPages = data?.data?.totalPages ?? 0;
  const categories = categoriesData?.data?.content ?? [];

  const openAdd = () => {
    setEditProduct(null);
    setForm(INITIAL_FORM);
    setShowModal(true);
  };

  const openEdit = (product: Product) => {
    setEditProduct(product);
    setForm({
      productTitle: product.productTitle,
      imageUrl: product.imageUrl ?? "",
      sku: product.sku ?? "",
      priceUnit: String(product.priceUnit),
      quantity: String(product.quantity),
      categoryId: String(product.category?.categoryId ?? ""),
    });
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditProduct(null);
    setForm(INITIAL_FORM);
  };

  const handleSave = () => {
    saveMutation.mutate({
      productTitle: form.productTitle,
      imageUrl: form.imageUrl || null,
      sku: form.sku || null,
      priceUnit: Number(form.priceUnit),
      quantity: Number(form.quantity),
      categoryId: form.categoryId ? Number(form.categoryId) : null,
    });
  };

  const columns = [
    {
      key: "product",
      header: t("colProduct"),
      render: (p: Product) => (
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0">
            {p.imageUrl ? (
              // eslint-disable-next-line @next/next/no-img-element
              <img src={p.imageUrl} alt={p.productTitle} className="w-full h-full object-cover" />
            ) : (
              <div className="w-full h-full flex items-center justify-center">
                <Package className="h-5 w-5 text-gray-300" />
              </div>
            )}
          </div>
          <span className="font-medium text-gray-900 line-clamp-1">{p.productTitle}</span>
        </div>
      ),
    },
    { key: "sku", header: t("colSku"), render: (p: Product) => <span className="text-gray-500">{p.sku ?? "\u2014"}</span> },
    {
      key: "category", header: t("colCategory"), render: (p: Product) => (
        <Badge variant="info">{p.category?.categoryTitle ?? t("uncategorized")}</Badge>
      ),
    },
    {
      key: "price", header: t("colPrice"), className: "text-right font-semibold text-orange-500",
      render: (p: Product) => formatPrice(p.priceUnit),
    },
    {
      key: "stock", header: t("colStock"), className: "text-right",
      render: (p: Product) => (
        <Badge variant={p.quantity > 0 ? "success" : "danger"}>
          {p.quantity > 0 ? p.quantity : t("outOfStock")}
        </Badge>
      ),
    },
    {
      key: "actions", header: t("colActions"), className: "text-center",
      render: (p: Product) => (
        <div className="flex items-center justify-center gap-2">
          <button onClick={() => openEdit(p)} className="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
            <Edit className="h-4 w-4" />
          </button>
          <button
            onClick={() => { if (confirm(t("deleteConfirm"))) deleteMutation.mutate(p.productId); }}
            className="p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
          >
            <Trash2 className="h-4 w-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">{t("title")}</h1>
          <p className="text-sm text-gray-500 mt-0.5">{t("count", { count: data?.data?.totalElements ?? 0 })}</p>
        </div>
        <Button onClick={openAdd}><Plus className="h-4 w-4" /> {t("addBtn")}</Button>
      </div>

      <DataTable
        columns={columns}
        data={products}
        isLoading={isLoading}
        rowKey={(p) => p.productId}
        page={page}
        totalPages={totalPages}
        onPageChange={setPage}
        emptyMessage={t("noProducts")}
      />

      <Modal open={showModal} onClose={closeModal} title={editProduct ? t("modalEditTitle") : t("modalAddTitle")}>
        <ModalBody>
          <Input label={t("nameLabel")} value={form.productTitle} onChange={(e) => setForm(p => ({...p, productTitle: e.target.value}))} />
          <Input label={t("imageLabel")} value={form.imageUrl} onChange={(e) => setForm(p => ({...p, imageUrl: e.target.value}))} />
          <div className="grid grid-cols-2 gap-3">
            <Input label={t("skuLabel")} value={form.sku} onChange={(e) => setForm(p => ({...p, sku: e.target.value}))} />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">{t("categoryLabel")}</label>
              <select value={form.categoryId} onChange={(e) => setForm(p => ({...p, categoryId: e.target.value}))} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500">
                <option value="">{t("selectCategory")}</option>
                {categories.map(c => <option key={c.categoryId} value={c.categoryId}>{c.categoryTitle}</option>)}
              </select>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <Input label={t("priceLabel")} type="number" value={form.priceUnit} onChange={(e) => setForm(p => ({...p, priceUnit: e.target.value}))} />
            <Input label={t("quantityLabel")} type="number" value={form.quantity} onChange={(e) => setForm(p => ({...p, quantity: e.target.value}))} />
          </div>
        </ModalBody>
        <ModalFooter>
          <Button variant="outline" className="flex-1" onClick={closeModal}>{tCommon("cancel")}</Button>
          <Button className="flex-1" loading={saveMutation.isPending} onClick={handleSave}>
            {editProduct ? tCommon("update") : tCommon("add")}
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}

"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Plus, Search, Edit, Trash2, Package, X } from "lucide-react";
import { productApi, categoryApi } from "@/lib/api";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { formatPrice } from "@/lib/utils";
import type { ApiResponse, PaginatedResponse, Product, Category } from "@/types";

export default function AdminProductsPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [editProduct, setEditProduct] = useState<Product | null>(null);
  const [form, setForm] = useState({
    productTitle: "", imageUrl: "", sku: "", priceUnit: "", quantity: "", categoryId: "",
  });

  const { data, isLoading } = useQuery({
    queryKey: ["admin-products-list", page],
    queryFn: async () => {
      const res = await productApi.getAll({ page, size: 10 });
      return res.data as ApiResponse<PaginatedResponse<Product>>;
    },
  });

  const { data: categoriesData } = useQuery({
    queryKey: ["categories"],
    queryFn: async () => {
      const res = await categoryApi.getAll({ page: 0, size: 50 });
      return res.data as ApiResponse<PaginatedResponse<Category>>;
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => productApi.delete(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-products-list"] }),
  });

  const saveMutation = useMutation({
    mutationFn: (data: unknown) =>
      editProduct ? productApi.update(editProduct.productId, data) : productApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin-products-list"] });
      setShowModal(false);
      setEditProduct(null);
      setForm({ productTitle: "", imageUrl: "", sku: "", priceUnit: "", quantity: "", categoryId: "" });
    },
  });

  const products = data?.data?.content ?? [];
  const totalPages = data?.data?.totalPages ?? 0;
  const categories = categoriesData?.data?.content ?? [];

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

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Sản phẩm</h1>
          <p className="text-sm text-gray-500 mt-0.5">{data?.data?.totalElements ?? 0} sản phẩm</p>
        </div>
        <Button onClick={() => { setEditProduct(null); setForm({ productTitle: "", imageUrl: "", sku: "", priceUnit: "", quantity: "", categoryId: "" }); setShowModal(true); }}>
          <Plus className="h-4 w-4" />
          Thêm sản phẩm
        </Button>
      </div>

      {/* Search */}
      <div className="bg-white rounded-xl border border-gray-200 p-4">
        <Input
          placeholder="Tìm kiếm sản phẩm..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          leftIcon={<Search className="h-4 w-4" />}
          className="max-w-sm"
        />
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-200 bg-gray-50">
                <th className="text-left px-4 py-3 font-semibold text-gray-700">Sản phẩm</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-700">SKU</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-700">Danh mục</th>
                <th className="text-right px-4 py-3 font-semibold text-gray-700">Giá</th>
                <th className="text-right px-4 py-3 font-semibold text-gray-700">Kho</th>
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
              ) : products.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center py-12 text-gray-500">
                    <Package className="h-12 w-12 mx-auto mb-2 text-gray-300" />
                    Chưa có sản phẩm nào
                  </td>
                </tr>
              ) : (
                products
                  .filter(p => !search || p.productTitle.toLowerCase().includes(search.toLowerCase()))
                  .map((product) => (
                    <tr key={product.productId} className="border-b border-gray-100 hover:bg-gray-50 transition-colors">
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0">
                            {product.imageUrl ? (
                              // eslint-disable-next-line @next/next/no-img-element
                              <img src={product.imageUrl} alt={product.productTitle} className="w-full h-full object-cover" />
                            ) : (
                              <div className="w-full h-full flex items-center justify-center">
                                <Package className="h-5 w-5 text-gray-300" />
                              </div>
                            )}
                          </div>
                          <span className="font-medium text-gray-900 line-clamp-1">{product.productTitle}</span>
                        </div>
                      </td>
                      <td className="px-4 py-3 text-gray-500">{product.sku ?? "—"}</td>
                      <td className="px-4 py-3">
                        <Badge variant="info">{product.category?.categoryTitle ?? "Chưa phân loại"}</Badge>
                      </td>
                      <td className="px-4 py-3 text-right font-semibold text-orange-500">
                        {formatPrice(product.priceUnit)}
                      </td>
                      <td className="px-4 py-3 text-right">
                        <Badge variant={product.quantity > 0 ? "success" : "danger"}>
                          {product.quantity > 0 ? product.quantity : "Hết hàng"}
                        </Badge>
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center justify-center gap-2">
                          <button onClick={() => openEdit(product)} className="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                            <Edit className="h-4 w-4" />
                          </button>
                          <button
                            onClick={() => { if (confirm("Xóa sản phẩm này?")) deleteMutation.mutate(product.productId); }}
                            className="p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex justify-center gap-2 p-4 border-t border-gray-200">
            <Button variant="outline" size="sm" disabled={page === 0} onClick={() => setPage(page - 1)}>Trước</Button>
            <span className="px-3 py-1.5 text-sm text-gray-600">Trang {page + 1}/{totalPages}</span>
            <Button variant="outline" size="sm" disabled={page === totalPages - 1} onClick={() => setPage(page + 1)}>Sau</Button>
          </div>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-lg shadow-xl">
            <div className="flex items-center justify-between p-6 border-b">
              <h3 className="text-lg font-bold">{editProduct ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm"}</h3>
              <button onClick={() => setShowModal(false)} className="p-1.5 rounded-lg hover:bg-gray-100">
                <X className="h-5 w-5" />
              </button>
            </div>
            <div className="p-6 space-y-4">
              <Input label="Tên sản phẩm *" value={form.productTitle} onChange={(e) => setForm(p => ({...p, productTitle: e.target.value}))} />
              <Input label="URL hình ảnh" value={form.imageUrl} onChange={(e) => setForm(p => ({...p, imageUrl: e.target.value}))} />
              <div className="grid grid-cols-2 gap-3">
                <Input label="SKU" value={form.sku} onChange={(e) => setForm(p => ({...p, sku: e.target.value}))} />
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Danh mục</label>
                  <select value={form.categoryId} onChange={(e) => setForm(p => ({...p, categoryId: e.target.value}))} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500">
                    <option value="">Chọn danh mục</option>
                    {categories.map(c => <option key={c.categoryId} value={c.categoryId}>{c.categoryTitle}</option>)}
                  </select>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <Input label="Giá (VND) *" type="number" value={form.priceUnit} onChange={(e) => setForm(p => ({...p, priceUnit: e.target.value}))} />
                <Input label="Số lượng *" type="number" value={form.quantity} onChange={(e) => setForm(p => ({...p, quantity: e.target.value}))} />
              </div>
            </div>
            <div className="flex gap-3 p-6 border-t">
              <Button variant="outline" className="flex-1" onClick={() => setShowModal(false)}>Hủy</Button>
              <Button className="flex-1" loading={saveMutation.isPending} onClick={handleSave}>
                {editProduct ? "Cập nhật" : "Thêm mới"}
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

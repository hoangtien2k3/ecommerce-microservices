"use client";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { Plus, Edit, Trash2, Tag, X } from "lucide-react";
import { categoryApi } from "@/lib/api";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import type { ApiResponse, Category, PaginatedResponse } from "@/types";

export default function AdminCategoriesPage() {
  const queryClient = useQueryClient();
  const [showModal, setShowModal] = useState(false);
  const [editItem, setEditItem] = useState<Category | null>(null);
  const [form, setForm] = useState({ categoryTitle: "", imageUrl: "" });

  const { data, isLoading } = useQuery({
    queryKey: ["admin-categories"],
    queryFn: async () => {
      const res = await categoryApi.getAll({ page: 0, size: 50 });
      return res.data as ApiResponse<PaginatedResponse<Category>>;
    },
  });

  const saveMutation = useMutation({
    mutationFn: (d: unknown) =>
      editItem ? categoryApi.update(editItem.categoryId, d) : categoryApi.create(d),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ["admin-categories"] }); setShowModal(false); setEditItem(null); setForm({ categoryTitle: "", imageUrl: "" }); },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => categoryApi.delete(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-categories"] }),
  });

  const categories = data?.data?.content ?? [];

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Danh mục ({categories.length})</h1>
        <Button onClick={() => { setEditItem(null); setForm({ categoryTitle: "", imageUrl: "" }); setShowModal(true); }}>
          <Plus className="h-4 w-4" /> Thêm danh mục
        </Button>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {isLoading ? [...Array(8)].map((_, i) => (
          <div key={i} className="bg-white rounded-xl border animate-pulse h-24" />
        )) : categories.map((cat) => (
          <div key={cat.categoryId} className="bg-white rounded-xl border border-gray-200 p-4 flex items-center gap-3 group">
            <div className="w-10 h-10 bg-orange-100 rounded-xl flex items-center justify-center flex-shrink-0">
              <Tag className="h-5 w-5 text-orange-500" />
            </div>
            <div className="flex-1 min-w-0">
              <p className="font-medium text-gray-900 text-sm truncate">{cat.categoryTitle}</p>
              <p className="text-xs text-gray-400">ID: {cat.categoryId}</p>
            </div>
            <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
              <button onClick={() => { setEditItem(cat); setForm({ categoryTitle: cat.categoryTitle, imageUrl: cat.imageUrl ?? "" }); setShowModal(true); }} className="p-1 text-gray-400 hover:text-blue-600 rounded">
                <Edit className="h-3.5 w-3.5" />
              </button>
              <button onClick={() => { if (confirm("Xóa danh mục này?")) deleteMutation.mutate(cat.categoryId); }} className="p-1 text-gray-400 hover:text-red-600 rounded">
                <Trash2 className="h-3.5 w-3.5" />
              </button>
            </div>
          </div>
        ))}
      </div>
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-md shadow-xl">
            <div className="flex items-center justify-between p-6 border-b">
              <h3 className="text-lg font-bold">{editItem ? "Chỉnh sửa" : "Thêm danh mục"}</h3>
              <button onClick={() => setShowModal(false)}><X className="h-5 w-5" /></button>
            </div>
            <div className="p-6 space-y-4">
              <Input label="Tên danh mục *" value={form.categoryTitle} onChange={(e) => setForm(p => ({...p, categoryTitle: e.target.value}))} />
              <Input label="URL hình ảnh" value={form.imageUrl} onChange={(e) => setForm(p => ({...p, imageUrl: e.target.value}))} />
            </div>
            <div className="flex gap-3 p-6 border-t">
              <Button variant="outline" className="flex-1" onClick={() => setShowModal(false)}>Hủy</Button>
              <Button className="flex-1" loading={saveMutation.isPending} onClick={() => saveMutation.mutate({ categoryTitle: form.categoryTitle, imageUrl: form.imageUrl || null })}>
                {editItem ? "Cập nhật" : "Thêm mới"}
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

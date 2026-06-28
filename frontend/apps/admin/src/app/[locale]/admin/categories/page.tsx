"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { useTranslations } from "next-intl";
import { Plus, Edit, Trash2, Tag } from "lucide-react";
import { categoryApi } from "@ecommerce/lib/api";
import { useCategories } from "@/hooks";
import { Button, Input, Modal, ModalBody, ModalFooter } from "@ecommerce/ui";
import type { Category } from "@ecommerce/lib/types";

export default function AdminCategoriesPage() {
  const t = useTranslations("Categories");
  const tCommon = useTranslations("Common");
  const queryClient = useQueryClient();
  const [showModal, setShowModal] = useState(false);
  const [editItem, setEditItem] = useState<Category | null>(null);
  const [form, setForm] = useState({ categoryTitle: "", imageUrl: "" });

  const { data, isLoading } = useCategories();

  const saveMutation = useMutation({
    mutationFn: (d: unknown) =>
      editItem ? categoryApi.update(editItem.categoryId, d) : categoryApi.create(d),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories"] });
      closeModal();
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => categoryApi.delete(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["categories"] }),
  });

  const categories = data?.data?.content ?? [];

  const openAdd = () => {
    setEditItem(null);
    setForm({ categoryTitle: "", imageUrl: "" });
    setShowModal(true);
  };

  const openEdit = (cat: Category) => {
    setEditItem(cat);
    setForm({ categoryTitle: cat.categoryTitle, imageUrl: cat.imageUrl ?? "" });
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditItem(null);
    setForm({ categoryTitle: "", imageUrl: "" });
  };

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">{t("title", { count: categories.length })}</h1>
        <Button onClick={openAdd}><Plus className="h-4 w-4" /> {t("addBtn")}</Button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {isLoading
          ? [...Array(8)].map((_, i) => (
              <div key={i} className="bg-white rounded-xl border animate-pulse h-24" />
            ))
          : categories.map((cat) => (
              <CategoryCard
                key={cat.categoryId}
                category={cat}
                onEdit={() => openEdit(cat)}
                onDelete={() => deleteMutation.mutate(cat.categoryId)}
                deleteConfirm={t("deleteConfirm")}
              />
            ))}
      </div>

      <Modal open={showModal} onClose={closeModal} title={editItem ? t("modalEditTitle") : t("modalAddTitle")}>
        <ModalBody>
          <Input label={t("nameLabel")} value={form.categoryTitle} onChange={(e) => setForm(p => ({...p, categoryTitle: e.target.value}))} />
          <Input label={t("imageLabel")} value={form.imageUrl} onChange={(e) => setForm(p => ({...p, imageUrl: e.target.value}))} />
        </ModalBody>
        <ModalFooter>
          <Button variant="outline" className="flex-1" onClick={closeModal}>{tCommon("cancel")}</Button>
          <Button className="flex-1" loading={saveMutation.isPending} onClick={() => saveMutation.mutate({ categoryTitle: form.categoryTitle, imageUrl: form.imageUrl || null })}>
            {editItem ? tCommon("update") : tCommon("add")}
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
}

function CategoryCard({
  category, onEdit, onDelete, deleteConfirm,
}: {
  category: Category;
  onEdit: () => void;
  onDelete: () => void;
  deleteConfirm: string;
}) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-4 flex items-center gap-3 group">
      <div className="w-10 h-10 bg-orange-100 rounded-xl flex items-center justify-center shrink-0">
        <Tag className="h-5 w-5 text-orange-500" />
      </div>
      <div className="flex-1 min-w-0">
        <p className="font-medium text-gray-900 text-sm truncate">{category.categoryTitle}</p>
        <p className="text-xs text-gray-400">ID: {category.categoryId}</p>
      </div>
      <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
        <button onClick={onEdit} className="p-1 text-gray-400 hover:text-blue-600 rounded">
          <Edit className="h-3.5 w-3.5" />
        </button>
        <button onClick={() => { if (confirm(deleteConfirm)) onDelete(); }} className="p-1 text-gray-400 hover:text-red-600 rounded">
          <Trash2 className="h-3.5 w-3.5" />
        </button>
      </div>
    </div>
  );
}

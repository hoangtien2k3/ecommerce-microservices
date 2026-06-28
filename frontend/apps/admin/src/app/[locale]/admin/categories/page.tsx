"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { useTranslations } from "next-intl";
import { Plus, Edit, Trash2, Tag } from "lucide-react";
import { categoryApi } from "@ecommerce/lib/api";
import { useCategories } from "@/hooks";
import { Button, Input, Modal, ModalBody, ModalFooter } from "@ecommerce/ui";
import type { Category } from "@ecommerce/lib/types";
import { categoriesStyles as s } from "./categories.styles";

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
    <div className={s.root}>
      <div className={s.header}>
        <h1 className={s.title}>{t("title", { count: categories.length })}</h1>
        <Button onClick={openAdd}><Plus className="h-4 w-4" /> {t("addBtn")}</Button>
      </div>

      <div className={s.grid}>
        {isLoading
          ? [...Array(8)].map((_, i) => (
              <div key={i} className={s.skeleton} />
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
    <div className={s.card}>
      <div className={s.cardIcon}>
        <Tag className="h-5 w-5 text-primary-500" />
      </div>
      <div className={s.cardBody}>
        <p className={s.cardName}>{category.categoryTitle}</p>
        <p className={s.cardId}>ID: {category.categoryId}</p>
      </div>
      <div className={s.cardActions}>
        <button onClick={onEdit} className={s.editBtn}>
          <Edit className="h-3.5 w-3.5" />
        </button>
        <button onClick={() => { if (confirm(deleteConfirm)) onDelete(); }} className={s.deleteBtn}>
          <Trash2 className="h-3.5 w-3.5" />
        </button>
      </div>
    </div>
  );
}

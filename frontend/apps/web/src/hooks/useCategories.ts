import { useQuery } from "@tanstack/react-query";
import { useTranslations } from "next-intl";
import { categoryApi } from "@ecommerce/lib/api";
import type { ApiResponse, PaginatedResponse, Category } from "@ecommerce/lib/types";

export function useCategories(page = 0, size = 100) {
  return useQuery({
    queryKey: ["categories", { page, size }],
    queryFn: async () => {
      const res = await categoryApi.getAll({ page, size });
      return res.data as ApiResponse<PaginatedResponse<Category>>;
    },
  });
}

/**
 * 카테고리를 대분류(parentCategory 없음)와 소분류로 나눈다.
 * DB categories.parent_category_id 로 만든 2단계 구조를 그대로 쓴다.
 * 소분류가 하나도 없으면(대분류를 지운 경우) 모든 카테고리가 parents 에 들어가고 hasHierarchy 는 false 다.
 */
export function useCategoryTree() {
  const query = useCategories();
  const all = query.data?.data?.content ?? [];
  const parents = all.filter((c) => !c.parentCategory);
  const hasHierarchy = all.some((c) => c.parentCategory);
  const childrenOf = (parentId: number) => all.filter((c) => c.parentCategory?.categoryId === parentId);
  const findById = (id: number) => all.find((c) => c.categoryId === id);
  return { parents, hasHierarchy, childrenOf, findById, isLoading: query.isLoading };
}

export interface CategoryMenuItem {
  key: string;
  href: string;
  label: string;
}

/**
 * 헤더·홈 카테고리 메뉴 항목.
 * 대분류가 있으면 대분류마다 하나씩, 없으면 '의류' 하나(전체 상품)만 둔다.
 * 평평한 카테고리 전체는 상품 목록 사이드바에서 고른다.
 */
export function useCategoryMenu(): CategoryMenuItem[] {
  const t = useTranslations("Category");
  const { parents, hasHierarchy, isLoading } = useCategoryTree();
  if (isLoading) return [];
  if (!hasHierarchy) return [{ key: "all", href: "/products", label: t("clothing") }];
  return parents.map((cat) => ({
    key: String(cat.categoryId),
    href: `/products?categoryId=${cat.categoryId}`,
    label: cat.categoryTitle,
  }));
}

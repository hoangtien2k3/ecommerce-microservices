"use client";

import { useTranslations } from "next-intl";
import { useProducts, useCategories } from "@/hooks";
import CategorySection from "./CategorySection";

// 홈의 의류 섹션: 칩은 DB 카테고리(parent id 없는 것), 상품은 DB 실제 상품 최신 10개 (5개씩 2줄).
// mock 상품과 달리 눌렀을 때 실제 상품 상세로 간다.
export default function ClothingSection() {
  const t = useTranslations("Home");
  const { data: products } = useProducts({ sort: "productId,desc" });
  const { data: categoriesData } = useCategories();

  const chips = (categoriesData?.data?.content ?? [])
    .filter((c) => !c.parentCategory)
    .slice(0, 6)
    .map((c) => ({ id: c.categoryId, label: c.categoryTitle, href: `/products?categoryId=${c.categoryId}` }));

  return (
    <CategorySection
      title={t("catClothing")}
      icon="👕"
      viewAllLabel={t("viewAll")}
      bannerTitle={t("clothingBannerTitle")}
      bannerSub={t("clothingBannerSub")}
      bannerCta={t("heroCta")}
      bannerGradient="from-primary-600 to-primary-700"
      brands={chips}
      products={(products ?? []).slice(0, 10)}
    />
  );
}

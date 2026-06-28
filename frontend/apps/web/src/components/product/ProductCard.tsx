"use client";

import React, { useState } from "react";
import { useTranslations } from "next-intl";
import type { Product } from "@ecommerce/lib/types";
import { useCartStore } from "@ecommerce/lib/store";
import { cn } from "@ecommerce/lib/utils";
import { Link } from "@/i18n/navigation";
import {
  ProductImage, WishlistButton, AddToCartButton,
  ProductStars, ProductPrice, StockLabel, OutOfStockBadge,
} from "./ProductParts";
import { productStyles as s } from "./product.styles";

interface ProductCardProps {
  product: Product;
  className?: string;
}

export default function ProductCard({ product, className }: ProductCardProps) {
  const t = useTranslations("ProductCard");
  const { addItem } = useCartStore();
  const [wishlisted, setWishlisted] = useState(false);
  const [addedToCart, setAddedToCart] = useState(false);
  const inStock = (product.quantity ?? 0) > 0;

  const handleAddToCart = (e: React.MouseEvent) => {
    e.preventDefault();
    addItem(product);
    setAddedToCart(true);
    setTimeout(() => setAddedToCart(false), 1500);
  };

  const handleWishlist = (e: React.MouseEvent) => {
    e.preventDefault();
    setWishlisted(!wishlisted);
  };

  return (
    <Link href={`/products/${product.productId}`}>
      <div className={cn(s.card, className)}>
        <div className={s.imageWrap}>
          <ProductImage product={product} />
          <div className={s.badgeTopLeft}>
            {!inStock && <OutOfStockBadge />}
          </div>
          <div className={s.badgeTopRight}>
            <WishlistButton wishlisted={wishlisted} onClick={handleWishlist} />
          </div>
          <div className={s.addToCartSlot}>
            <AddToCartButton
              inStock={inStock}
              addedToCart={addedToCart}
              onClick={handleAddToCart}
            />
          </div>
        </div>

        <div className={s.body}>
          <p className={s.category}>
            {product.category?.categoryTitle ?? t("uncategorized")}
          </p>
          <h3 className={s.title}>
            {product.productTitle}
          </h3>
          <ProductStars rating={4} count={12} />
          <div className={s.priceFooter}>
            <div>
              <ProductPrice price={product.priceUnit} />
              <StockLabel quantity={product.quantity} />
            </div>
          </div>
        </div>
      </div>
    </Link>
  );
}

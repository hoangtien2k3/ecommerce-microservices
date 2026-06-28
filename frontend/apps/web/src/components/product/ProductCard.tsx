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
      <div className={cn(
        "group bg-white rounded-xl border border-gray-200 overflow-hidden",
        "hover:shadow-lg hover:border-orange-200 transition-all duration-300",
        className
      )}>
        <div className="relative aspect-square bg-gray-50 overflow-hidden">
          <ProductImage product={product} />
          <div className="absolute top-2 left-2 flex flex-col gap-1">
            {!inStock && <OutOfStockBadge />}
          </div>
          <div className="absolute top-2 right-2 flex flex-col gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
            <WishlistButton wishlisted={wishlisted} onClick={handleWishlist} />
          </div>
          <div className="absolute inset-x-0 bottom-0 translate-y-full group-hover:translate-y-0 transition-transform duration-300">
            <AddToCartButton
              inStock={inStock}
              addedToCart={addedToCart}
              onClick={handleAddToCart}
            />
          </div>
        </div>

        <div className="p-3">
          <p className="text-xs text-gray-500 mb-1">
            {product.category?.categoryTitle ?? t("uncategorized")}
          </p>
          <h3 className="text-sm font-medium text-gray-900 line-clamp-2 mb-2 min-h-10">
            {product.productTitle}
          </h3>
          <ProductStars rating={4} count={12} />
          <div className="flex items-center justify-between mt-2">
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

"use client";

import Link from "next/link";
import Image from "next/image";
import { Heart, ShoppingCart, Star } from "lucide-react";
import { useState } from "react";
import type { Product } from "@/types";
import { useCartStore } from "@/store/cartStore";
import { formatPrice } from "@/lib/utils";
import { cn } from "@/lib/utils";

interface ProductCardProps {
  product: Product;
  className?: string;
}

export default function ProductCard({ product, className }: ProductCardProps) {
  const { addItem } = useCartStore();
  const [wishlisted, setWishlisted] = useState(false);
  const [addedToCart, setAddedToCart] = useState(false);

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

  const inStock = (product.quantity ?? 0) > 0;

  return (
    <Link href={`/products/${product.productId}`}>
      <div
        className={cn(
          "group bg-white rounded-xl border border-gray-200 overflow-hidden",
          "hover:shadow-lg hover:border-orange-200 transition-all duration-300",
          className
        )}
      >
        {/* Image */}
        <div className="relative aspect-square bg-gray-50 overflow-hidden">
          {product.imageUrl ? (
            <Image
              src={product.imageUrl}
              alt={product.productTitle}
              fill
              className="object-cover group-hover:scale-105 transition-transform duration-300"
              sizes="(max-width: 768px) 50vw, (max-width: 1200px) 33vw, 25vw"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-orange-50 to-orange-100">
              <ShoppingCart className="h-12 w-12 text-orange-300" />
            </div>
          )}

          {/* Badges */}
          <div className="absolute top-2 left-2 flex flex-col gap-1">
            {!inStock && (
              <span className="bg-gray-800 text-white text-xs px-2 py-0.5 rounded-full">
                Hết hàng
              </span>
            )}
          </div>

          {/* Actions */}
          <div className="absolute top-2 right-2 flex flex-col gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
            <button
              onClick={handleWishlist}
              className="w-8 h-8 bg-white rounded-full flex items-center justify-center shadow-md hover:scale-110 transition-transform"
            >
              <Heart
                className={cn("h-4 w-4", wishlisted ? "fill-red-500 text-red-500" : "text-gray-500")}
              />
            </button>
          </div>

          {/* Add to cart overlay */}
          <div className="absolute inset-x-0 bottom-0 translate-y-full group-hover:translate-y-0 transition-transform duration-300">
            <button
              onClick={handleAddToCart}
              disabled={!inStock}
              className={cn(
                "w-full py-2.5 text-sm font-medium text-white transition-colors",
                addedToCart
                  ? "bg-green-500"
                  : inStock
                  ? "bg-orange-500 hover:bg-orange-600"
                  : "bg-gray-400 cursor-not-allowed"
              )}
            >
              {addedToCart ? "✓ Đã thêm vào giỏ" : inStock ? "Thêm vào giỏ hàng" : "Hết hàng"}
            </button>
          </div>
        </div>

        {/* Info */}
        <div className="p-3">
          <p className="text-xs text-gray-500 mb-1">
            {product.category?.categoryTitle ?? "Chưa phân loại"}
          </p>
          <h3 className="text-sm font-medium text-gray-900 line-clamp-2 mb-2 min-h-[2.5rem]">
            {product.productTitle}
          </h3>

          {/* Rating placeholder */}
          <div className="flex items-center gap-1 mb-2">
            {[...Array(5)].map((_, i) => (
              <Star key={i} className={cn("h-3 w-3", i < 4 ? "fill-yellow-400 text-yellow-400" : "text-gray-300")} />
            ))}
            <span className="text-xs text-gray-500">(12)</span>
          </div>

          <div className="flex items-center justify-between">
            <div>
              <p className="text-base font-bold text-orange-500">
                {formatPrice(product.priceUnit)}
              </p>
              {inStock ? (
                <p className="text-xs text-green-600">Còn {product.quantity} sản phẩm</p>
              ) : (
                <p className="text-xs text-red-500">Hết hàng</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </Link>
  );
}

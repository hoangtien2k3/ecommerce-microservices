"use client";

import { use } from "react";
import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import Image from "next/image";
import Link from "next/link";
import {
  ShoppingCart, Heart, Share2, Star, Truck, Shield, RefreshCw,
  ChevronRight, Minus, Plus, Package
} from "lucide-react";
import { productApi } from "@/lib/api";
import { useCartStore } from "@/store/cartStore";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { formatPrice } from "@/lib/utils";
import type { ApiResponse, Product } from "@/types";
import { useState } from "react";

export default function ProductDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const router = useRouter();
  const { addItem } = useCartStore();
  const [quantity, setQuantity] = useState(1);
  const [wishlisted, setWishlisted] = useState(false);
  const [addedToCart, setAddedToCart] = useState(false);

  const { data, isLoading, isError } = useQuery({
    queryKey: ["product", id],
    queryFn: async () => {
      const res = await productApi.getById(Number(id));
      return res.data as ApiResponse<Product>;
    },
    enabled: !!id,
  });

  const product = data?.data;

  const handleAddToCart = () => {
    if (!product) return;
    addItem(product, quantity);
    setAddedToCart(true);
    setTimeout(() => setAddedToCart(false), 2000);
  };

  const handleBuyNow = () => {
    if (!product) return;
    addItem(product, quantity);
    router.push("/cart");
  };

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 animate-pulse">
          <div className="aspect-square bg-gray-200 rounded-xl" />
          <div className="space-y-4">
            <div className="h-6 bg-gray-200 rounded w-3/4" />
            <div className="h-8 bg-gray-200 rounded w-1/2" />
            <div className="h-4 bg-gray-200 rounded" />
            <div className="h-4 bg-gray-200 rounded w-5/6" />
          </div>
        </div>
      </div>
    );
  }

  if (isError || !product) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-16 text-center">
        <Package className="h-16 w-16 text-gray-300 mx-auto mb-4" />
        <p className="text-lg font-medium text-gray-700">Không tìm thấy sản phẩm</p>
        <Link href="/products" className="mt-4 inline-block">
          <Button variant="outline">Quay lại danh sách</Button>
        </Link>
      </div>
    );
  }

  const inStock = product.quantity > 0;

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Breadcrumb */}
      <nav className="flex items-center gap-2 text-sm text-gray-500 mb-6">
        <Link href="/" className="hover:text-orange-500">Trang chủ</Link>
        <ChevronRight className="h-4 w-4" />
        <Link href="/products" className="hover:text-orange-500">Sản phẩm</Link>
        {product.category && (
          <>
            <ChevronRight className="h-4 w-4" />
            <Link href={`/products?categoryId=${product.category.categoryId}`} className="hover:text-orange-500">
              {product.category.categoryTitle}
            </Link>
          </>
        )}
        <ChevronRight className="h-4 w-4" />
        <span className="text-gray-900 font-medium truncate max-w-[200px]">{product.productTitle}</span>
      </nav>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
        {/* Image */}
        <div className="space-y-3">
          <div className="relative aspect-square bg-gray-50 rounded-2xl overflow-hidden border border-gray-200">
            {product.imageUrl ? (
              <Image
                src={product.imageUrl}
                alt={product.productTitle}
                fill
                className="object-cover"
                sizes="(max-width: 768px) 100vw, 50vw"
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-orange-50 to-orange-100">
                <ShoppingCart className="h-24 w-24 text-orange-200" />
              </div>
            )}
          </div>
        </div>

        {/* Info */}
        <div>
          {/* Category & SKU */}
          <div className="flex items-center gap-2 mb-2">
            {product.category && (
              <Badge variant="info">{product.category.categoryTitle}</Badge>
            )}
            {product.sku && (
              <span className="text-xs text-gray-400">SKU: {product.sku}</span>
            )}
          </div>

          <h1 className="text-2xl font-bold text-gray-900 mb-3">{product.productTitle}</h1>

          {/* Rating */}
          <div className="flex items-center gap-2 mb-4">
            <div className="flex items-center">
              {[...Array(5)].map((_, i) => (
                <Star key={i} className={`h-4 w-4 ${i < 4 ? "fill-yellow-400 text-yellow-400" : "text-gray-300"}`} />
              ))}
            </div>
            <span className="text-sm text-gray-600">4.0 (23 đánh giá)</span>
          </div>

          {/* Price */}
          <div className="bg-orange-50 rounded-xl p-4 mb-5">
            <p className="text-3xl font-bold text-orange-500 mb-1">
              {formatPrice(product.priceUnit)}
            </p>
            <p className="text-sm text-gray-500">Đã bao gồm VAT</p>
          </div>

          {/* Stock status */}
          <div className="flex items-center gap-2 mb-5">
            <div className={`w-2 h-2 rounded-full ${inStock ? "bg-green-500" : "bg-red-500"}`} />
            <span className={`text-sm font-medium ${inStock ? "text-green-600" : "text-red-600"}`}>
              {inStock ? `Còn ${product.quantity} sản phẩm` : "Hết hàng"}
            </span>
          </div>

          {/* Quantity */}
          {inStock && (
            <div className="flex items-center gap-3 mb-5">
              <span className="text-sm font-medium text-gray-700">Số lượng:</span>
              <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden">
                <button
                  onClick={() => setQuantity(Math.max(1, quantity - 1))}
                  className="px-3 py-2 hover:bg-gray-100 transition-colors"
                >
                  <Minus className="h-4 w-4" />
                </button>
                <span className="px-4 py-2 text-sm font-semibold border-x border-gray-300 min-w-[50px] text-center">
                  {quantity}
                </span>
                <button
                  onClick={() => setQuantity(Math.min(product.quantity, quantity + 1))}
                  className="px-3 py-2 hover:bg-gray-100 transition-colors"
                >
                  <Plus className="h-4 w-4" />
                </button>
              </div>
            </div>
          )}

          {/* Actions */}
          <div className="flex gap-3 mb-6">
            <Button
              onClick={handleAddToCart}
              variant="outline"
              size="lg"
              disabled={!inStock}
              className="flex-1"
            >
              {addedToCart ? (
                "✓ Đã thêm vào giỏ!"
              ) : (
                <>
                  <ShoppingCart className="h-5 w-5" />
                  Thêm vào giỏ
                </>
              )}
            </Button>
            <Button
              onClick={handleBuyNow}
              size="lg"
              disabled={!inStock}
              className="flex-1"
            >
              Mua ngay
            </Button>
            <button
              onClick={() => setWishlisted(!wishlisted)}
              className="p-3 border border-gray-300 rounded-lg hover:border-red-400 transition-colors"
            >
              <Heart className={`h-5 w-5 ${wishlisted ? "fill-red-500 text-red-500" : "text-gray-400"}`} />
            </button>
            <button className="p-3 border border-gray-300 rounded-lg hover:border-gray-400 transition-colors">
              <Share2 className="h-5 w-5 text-gray-400" />
            </button>
          </div>

          {/* Benefits */}
          <div className="space-y-2 border-t border-gray-200 pt-5">
            {[
              { icon: Truck, text: "Giao hàng miễn phí cho đơn trên 500.000đ" },
              { icon: Shield, text: "Bảo hành chính hãng 12 tháng" },
              { icon: RefreshCw, text: "Đổi trả miễn phí trong 30 ngày" },
            ].map(({ icon: Icon, text }) => (
              <div key={text} className="flex items-center gap-3 text-sm text-gray-600">
                <Icon className="h-4 w-4 text-orange-500 flex-shrink-0" />
                {text}
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Description */}
      {product.description && (
        <div className="mt-10 bg-white rounded-xl border border-gray-200 p-6">
          <h2 className="text-lg font-bold text-gray-900 mb-4">Mô tả sản phẩm</h2>
          <p className="text-gray-700 leading-relaxed">{product.description}</p>
        </div>
      )}
    </div>
  );
}

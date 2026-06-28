"use client";

import { use, useState } from "react";
import { useTranslations } from "next-intl";
import Image from "next/image";
import {
  ShoppingCart, Heart, Share2, Star, Truck, Shield, RefreshCw,
  ChevronRight, Package,
} from "lucide-react";
import { useProduct } from "@/hooks";
import { useCartStore } from "@ecommerce/lib/store";
import { Button, Badge, QuantitySelector } from "@ecommerce/ui";
import { formatPrice, cn } from "@ecommerce/lib/utils";
import { Link, useRouter } from "@/i18n/navigation";

export default function ProductDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const router = useRouter();
  const t = useTranslations("ProductDetail");
  const { addItem } = useCartStore();
  const [quantity, setQuantity] = useState(1);
  const [wishlisted, setWishlisted] = useState(false);
  const [addedToCart, setAddedToCart] = useState(false);

  const { data: product, isLoading, isError } = useProduct(id);

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
        <p className="text-lg font-medium text-gray-700">{t("notFound")}</p>
        <Link href="/products" className="mt-4 inline-block">
          <Button variant="outline">{t("backToList")}</Button>
        </Link>
      </div>
    );
  }

  const inStock = product.quantity > 0;

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <Breadcrumb product={product} />

      <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
        <ProductGallery product={product} />
        <ProductInfo
          product={product}
          quantity={quantity}
          onQuantityChange={setQuantity}
          wishlisted={wishlisted}
          addedToCart={addedToCart}
          onWishlist={() => setWishlisted(!wishlisted)}
          onAddToCart={handleAddToCart}
          onBuyNow={handleBuyNow}
        />
      </div>

      {product.description && (
        <div className="mt-10 bg-white rounded-xl border border-gray-200 p-6">
          <h2 className="text-lg font-bold text-gray-900 mb-4">{t("description")}</h2>
          <p className="text-gray-700 leading-relaxed">{product.description}</p>
        </div>
      )}
    </div>
  );
}

function Breadcrumb({ product }: { product: { productTitle: string; category?: { categoryId: number; categoryTitle: string } } }) {
  const t = useTranslations("ProductDetail");
  return (
    <nav className="flex items-center gap-2 text-sm text-gray-500 mb-6">
      <Link href="/" className="hover:text-orange-500">{t("breadcrumbHome")}</Link>
      <ChevronRight className="h-4 w-4" />
      <Link href="/products" className="hover:text-orange-500">{t("breadcrumbProducts")}</Link>
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
  );
}

function ProductGallery({ product }: { product: { imageUrl?: string; productTitle: string } }) {
  return (
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
  );
}

function ProductInfo({
  product, quantity, onQuantityChange,
  wishlisted, addedToCart, onWishlist, onAddToCart, onBuyNow,
}: {
  product: {
    category?: { categoryTitle: string }; sku?: string; productTitle: string;
    priceUnit: number; quantity: number;
  };
  quantity: number; onQuantityChange: (q: number) => void;
  wishlisted: boolean; addedToCart: boolean;
  onWishlist: () => void; onAddToCart: () => void; onBuyNow: () => void;
}) {
  const t = useTranslations("ProductDetail");
  const inStock = product.quantity > 0;

  return (
    <div>
      <div className="flex items-center gap-2 mb-2">
        {product.category && <Badge variant="info">{product.category.categoryTitle}</Badge>}
        {product.sku && <span className="text-xs text-gray-400">SKU: {product.sku}</span>}
      </div>

      <h1 className="text-2xl font-bold text-gray-900 mb-3">{product.productTitle}</h1>

      <div className="flex items-center gap-2 mb-4">
        <div className="flex items-center">
          {[...Array(5)].map((_, i) => (
            <Star key={i} className={`h-4 w-4 ${i < 4 ? "fill-yellow-400 text-yellow-400" : "text-gray-300"}`} />
          ))}
        </div>
        <span className="text-sm text-gray-600">4.0 (23 reviews)</span>
      </div>

      <div className="bg-orange-50 rounded-xl p-4 mb-5">
        <p className="text-3xl font-bold text-orange-500 mb-1">{formatPrice(product.priceUnit)}</p>
        <p className="text-sm text-gray-500">{t("vatIncluded")}</p>
      </div>

      <StockStatus inStock={inStock} quantity={product.quantity} />

      {inStock && (
        <div className="flex items-center gap-3 mb-5">
          <span className="text-sm font-medium text-gray-700">{t("quantityLabel")}</span>
          <QuantitySelector
            value={quantity}
            min={1}
            max={product.quantity}
            onChange={onQuantityChange}
          />
        </div>
      )}

      <div className="flex gap-3 mb-6">
        <Button onClick={onAddToCart} variant="outline" size="lg" disabled={!inStock} className="flex-1">
          {addedToCart ? t("added") : (
            <><ShoppingCart className="h-5 w-5" /> {t("addToCart")}</>
          )}
        </Button>
        <Button onClick={onBuyNow} size="lg" disabled={!inStock} className="flex-1">
          {t("buyNow")}
        </Button>
        <button onClick={onWishlist} className="p-3 border border-gray-300 rounded-lg hover:border-red-400 transition-colors">
          <Heart className={`h-5 w-5 ${wishlisted ? "fill-red-500 text-red-500" : "text-gray-400"}`} />
        </button>
        <button className="p-3 border border-gray-300 rounded-lg hover:border-gray-400 transition-colors">
          <Share2 className="h-5 w-5 text-gray-400" />
        </button>
      </div>

      <div className="space-y-2 border-t border-gray-200 pt-5">
        {[
          { icon: Truck, text: t("freeShipping") },
          { icon: Shield, text: t("warranty") },
          { icon: RefreshCw, text: t("freeReturn") },
        ].map(({ icon: Icon, text }) => (
          <div key={text} className="flex items-center gap-3 text-sm text-gray-600">
            <Icon className="h-4 w-4 text-orange-500 flex-shrink-0" />
            {text}
          </div>
        ))}
      </div>
    </div>
  );
}

function StockStatus({ inStock, quantity }: { inStock: boolean; quantity: number }) {
  return (
    <div className="flex items-center gap-2 mb-5">
      <div className={`w-2 h-2 rounded-full ${inStock ? "bg-green-500" : "bg-red-500"}`} />
      <span className={`text-sm font-medium ${inStock ? "text-green-600" : "text-red-600"}`}>
        {inStock ? `In stock (${quantity})` : "Out of stock"}
      </span>
    </div>
  );
}

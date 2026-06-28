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
import { formatPrice } from "@ecommerce/lib/utils";
import { Link, useRouter } from "@/i18n/navigation";
import { productDetailStyles as s } from "./productDetail.styles";

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
      <div className={s.page}>
        <div className={s.loadingGrid}>
          <div className={s.loadingImage} />
          <div className={s.loadingInfo}>
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
      <div className={s.errorPage}>
        <Package className="h-16 w-16 text-gray-300 mx-auto mb-4" />
        <p className={s.errorText}>{t("notFound")}</p>
        <Link href="/products" className="mt-4 inline-block">
          <Button variant="outline">{t("backToList")}</Button>
        </Link>
      </div>
    );
  }

  return (
    <div className={s.page}>
      <Breadcrumb product={product} />

      <div className={s.layout}>
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
        <div className={s.descBox}>
          <h2 className={s.descTitle}>{t("description")}</h2>
          <p className={s.descText}>{product.description}</p>
        </div>
      )}
    </div>
  );
}

function Breadcrumb({ product }: { product: { productTitle: string; category?: { categoryId: number; categoryTitle: string } } }) {
  const t = useTranslations("ProductDetail");
  return (
    <nav className={s.breadcrumb}>
      <Link href="/" className={s.breadcrumbLink}>{t("breadcrumbHome")}</Link>
      <ChevronRight className="h-4 w-4" />
      <Link href="/products" className={s.breadcrumbLink}>{t("breadcrumbProducts")}</Link>
      {product.category && (
        <>
          <ChevronRight className="h-4 w-4" />
          <Link href={`/products?categoryId=${product.category.categoryId}`} className={s.breadcrumbLink}>
            {product.category.categoryTitle}
          </Link>
        </>
      )}
      <ChevronRight className="h-4 w-4" />
      <span className={s.breadcrumbCurrent}>{product.productTitle}</span>
    </nav>
  );
}

function ProductGallery({ product }: { product: { imageUrl?: string; productTitle: string } }) {
  return (
    <div className={s.gallery}>
      {product.imageUrl ? (
        <Image
          src={product.imageUrl}
          alt={product.productTitle}
          fill
          className="object-cover"
          sizes="(max-width: 768px) 100vw, 50vw"
        />
      ) : (
        <div className={s.galleryPlaceholder}>
          <ShoppingCart className="h-24 w-24 text-primary-200" />
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
      <div className={s.infoTopRow}>
        {product.category && <Badge variant="info">{product.category.categoryTitle}</Badge>}
        {product.sku && <span className={s.sku}>SKU: {product.sku}</span>}
      </div>

      <h1 className={s.infoTitle}>{product.productTitle}</h1>

      <div className={s.ratingRow}>
        <div className={s.ratingStars}>
          {[...Array(5)].map((_, i) => (
            <Star key={i} className={`h-4 w-4 ${i < 4 ? "fill-yellow-400 text-yellow-400" : "text-gray-300"}`} />
          ))}
        </div>
        <span className={s.ratingText}>4.0 (23 reviews)</span>
      </div>

      <div className={s.priceBox}>
        <p className={s.priceValue}>{formatPrice(product.priceUnit)}</p>
        <p className={s.priceNote}>{t("vatIncluded")}</p>
      </div>

      <StockStatus inStock={inStock} quantity={product.quantity} />

      {inStock && (
        <div className={s.qtyRow}>
          <span className={s.qtyLabel}>{t("quantityLabel")}</span>
          <QuantitySelector
            value={quantity}
            min={1}
            max={product.quantity}
            onChange={onQuantityChange}
          />
        </div>
      )}

      <div className={s.actionRow}>
        <Button onClick={onAddToCart} variant="outline" size="lg" disabled={!inStock} className="flex-1">
          {addedToCart ? t("added") : (
            <><ShoppingCart className="h-5 w-5" /> {t("addToCart")}</>
          )}
        </Button>
        <Button onClick={onBuyNow} size="lg" disabled={!inStock} className="flex-1">
          {t("buyNow")}
        </Button>
        <button onClick={onWishlist} className={s.iconBtn}>
          <Heart className={`h-5 w-5 ${wishlisted ? "fill-red-500 text-red-500" : "text-gray-400"}`} />
        </button>
        <button className={s.iconBtn}>
          <Share2 className="h-5 w-5 text-gray-400" />
        </button>
      </div>

      <div className={s.perks}>
        {[
          { icon: Truck, text: t("freeShipping") },
          { icon: Shield, text: t("warranty") },
          { icon: RefreshCw, text: t("freeReturn") },
        ].map(({ icon: Icon, text }) => (
          <div key={text} className={s.perkItem}>
            <Icon className="h-4 w-4 text-primary-500 flex-shrink-0" />
            {text}
          </div>
        ))}
      </div>
    </div>
  );
}

function StockStatus({ inStock, quantity }: { inStock: boolean; quantity: number }) {
  return (
    <div className={s.stockRow}>
      <div className={`w-2 h-2 rounded-full ${inStock ? "bg-green-500" : "bg-red-500"}`} />
      <span className={`text-sm font-medium ${inStock ? "text-green-600" : "text-red-600"}`}>
        {inStock ? `In stock (${quantity})` : "Out of stock"}
      </span>
    </div>
  );
}

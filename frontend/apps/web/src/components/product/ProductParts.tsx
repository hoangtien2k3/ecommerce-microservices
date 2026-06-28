import Image from "next/image";
import { Heart, ShoppingCart, Star } from "lucide-react";
import { cn } from "@ecommerce/lib/utils";
import { formatPrice } from "@ecommerce/lib/utils";
import type { Product } from "@ecommerce/lib/types";
import { productStyles as s } from "./product.styles";

interface ProductCardProps {
  product: Product;
  wishlisted?: boolean;
  onWishlist?: (e: React.MouseEvent) => void;
  onAddToCart?: (e: React.MouseEvent) => void;
  addedToCart?: boolean;
  className?: string;
}

export function ProductImage({ product }: { product: Product }) {
  return product.imageUrl ? (
    <Image
      src={product.imageUrl}
      alt={product.productTitle}
      fill
      className="object-cover group-hover:scale-105 transition-transform duration-300"
      sizes="(max-width: 768px) 50vw, (max-width: 1200px) 33vw, 25vw"
    />
  ) : (
    <div className={s.imagePlaceholder}>
      <ShoppingCart className="h-12 w-12 text-primary-300" />
    </div>
  );
}

export function OutOfStockBadge() {
  return (
    <span className={s.outOfStockBadge}>
      Out of stock
    </span>
  );
}

export function WishlistButton({
  wishlisted, onClick,
}: {
  wishlisted?: boolean;
  onClick?: (e: React.MouseEvent) => void;
}) {
  return (
    <button
      onClick={onClick}
      className={s.wishlistBtn}
    >
      <Heart className={cn("h-4 w-4", wishlisted ? "fill-red-500 text-red-500" : "text-gray-500")} />
    </button>
  );
}

export function AddToCartButton({
  inStock, addedToCart, onClick,
}: {
  inStock: boolean;
  addedToCart?: boolean;
  onClick?: (e: React.MouseEvent) => void;
}) {
  return (
    <button
      onClick={onClick}
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
      {addedToCart ? "Added" : inStock ? "Add to cart" : "Out of stock"}
    </button>
  );
}

export function ProductStars({ rating = 4, count = 12 }: { rating?: number; count?: number }) {
  return (
    <div className={s.starsRow}>
      {[...Array(5)].map((_, i) => (
        <Star key={i} className={cn("h-3 w-3", i < rating ? "fill-yellow-400 text-yellow-400" : "text-gray-300")} />
      ))}
      <span className={s.starCount}>({count})</span>
    </div>
  );
}

export function ProductPrice({ price }: { price: number }) {
  return (
    <p className={s.price}>{formatPrice(price)}</p>
  );
}

export function StockLabel({ quantity }: { quantity: number }) {
  if (quantity <= 0) {
    return <p className="text-xs text-red-500">Out of stock</p>;
  }
  return <p className="text-xs text-green-600">{quantity} remaining</p>;
}

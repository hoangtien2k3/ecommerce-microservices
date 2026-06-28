"use client";

import Image from "next/image";
import { Trash2, Minus, Plus, ShoppingBag } from "lucide-react";
import { formatPrice } from "@ecommerce/lib/utils";
import { QuantitySelector } from "@ecommerce/ui";
import type { CartItem } from "@ecommerce/lib/types";
import { Link } from "@/i18n/navigation";
import { cartStyles as s } from "./cart.styles";

interface CartItemRowProps {
  item: CartItem;
  onUpdateQuantity: (productId: number, quantity: number) => void;
  onRemove: (productId: number) => void;
}

export default function CartItemRow({ item, onUpdateQuantity, onRemove }: CartItemRowProps) {
  const { product, quantity } = item;

  return (
    <div className={s.row}>
      <Link href={`/products/${product.productId}`} className="flex-shrink-0">
        <div className={s.thumbWrap}>
          {product.imageUrl ? (
            <Image src={product.imageUrl} alt={product.productTitle} fill className="object-cover" />
          ) : (
            <div className={s.thumbPlaceholder}>
              <ShoppingBag className="h-8 w-8 text-gray-300" />
            </div>
          )}
        </div>
      </Link>

      <div className={s.info}>
        <Link href={`/products/${product.productId}`}>
          <h3 className={s.title}>
            {product.productTitle}
          </h3>
        </Link>
        {product.category && (
          <p className={s.category}>{product.category.categoryTitle}</p>
        )}
        <p className={s.price}>{formatPrice(product.priceUnit)}</p>
      </div>

      <div className={s.actions}>
        <button onClick={() => onRemove(product.productId)} className={s.removeBtn}>
          <Trash2 className="h-4 w-4" />
        </button>
        <QuantitySelector
          value={quantity}
          min={0}
          onChange={(val) => onUpdateQuantity(product.productId, val)}
        />
        <p className={s.lineTotal}>{formatPrice(product.priceUnit * quantity)}</p>
      </div>
    </div>
  );
}

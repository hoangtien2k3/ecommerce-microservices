"use client";

import Image from "next/image";
import { Trash2, Minus, Plus, ShoppingBag } from "lucide-react";
import { formatPrice } from "@ecommerce/lib/utils";
import { QuantitySelector } from "@ecommerce/ui";
import type { CartItem } from "@ecommerce/lib/types";
import { Link } from "@/i18n/navigation";

interface CartItemRowProps {
  item: CartItem;
  onUpdateQuantity: (productId: number, quantity: number) => void;
  onRemove: (productId: number) => void;
}

export default function CartItemRow({ item, onUpdateQuantity, onRemove }: CartItemRowProps) {
  const { product, quantity } = item;

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-4 flex gap-4">
      <Link href={`/products/${product.productId}`} className="flex-shrink-0">
        <div className="w-20 h-20 relative rounded-lg overflow-hidden bg-gray-50">
          {product.imageUrl ? (
            <Image src={product.imageUrl} alt={product.productTitle} fill className="object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <ShoppingBag className="h-8 w-8 text-gray-300" />
            </div>
          )}
        </div>
      </Link>

      <div className="flex-1 min-w-0">
        <Link href={`/products/${product.productId}`}>
          <h3 className="text-sm font-medium text-gray-900 hover:text-orange-500 line-clamp-2">
            {product.productTitle}
          </h3>
        </Link>
        {product.category && (
          <p className="text-xs text-gray-500 mt-0.5">{product.category.categoryTitle}</p>
        )}
        <p className="text-base font-bold text-orange-500 mt-1">{formatPrice(product.priceUnit)}</p>
      </div>

      <div className="flex flex-col items-end gap-3">
        <button onClick={() => onRemove(product.productId)} className="text-gray-400 hover:text-red-500 transition-colors">
          <Trash2 className="h-4 w-4" />
        </button>
        <QuantitySelector
          value={quantity}
          min={0}
          onChange={(val) => onUpdateQuantity(product.productId, val)}
        />
        <p className="text-sm font-bold text-gray-900">{formatPrice(product.priceUnit * quantity)}</p>
      </div>
    </div>
  );
}

"use client";

import Image from "next/image";
import { ShoppingBag } from "lucide-react";
import { formatPrice } from "@ecommerce/lib/utils";
import { Badge } from "@ecommerce/ui";
import type { CartItem } from "@ecommerce/lib/types";

interface OrderSummaryProps {
  items: CartItem[];
  subtotal: number;
  shippingFee: number;
  total: number;
  showShippingPromo?: boolean;
  shippingPromoText?: string;
  showCoupon?: boolean;
  couponPlaceholder?: string;
  children?: React.ReactNode;
}

export default function OrderSummary({
  items, subtotal, shippingFee, total,
  showShippingPromo, shippingPromoText,
  showCoupon, couponPlaceholder,
  children,
}: OrderSummaryProps) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-5 sticky top-24">
      <h2 className="font-bold text-gray-900 mb-4">Order Summary</h2>

      {items.length > 0 && (
        <div className="space-y-3 max-h-60 overflow-y-auto mb-4">
          {items.map(({ product, quantity }) => (
            <div key={product.productId} className="flex gap-3">
              <div className="w-14 h-14 relative bg-gray-50 rounded-lg overflow-hidden flex-shrink-0">
                {product.imageUrl ? (
                  <Image src={product.imageUrl} alt={product.productTitle} fill className="object-cover" />
                ) : (
                  <div className="w-full h-full flex items-center justify-center">
                    <ShoppingBag className="h-6 w-6 text-gray-300" />
                  </div>
                )}
                <Badge className="absolute -top-1 -right-1 w-5 h-5 flex items-center justify-center p-0 text-xs">
                  {quantity}
                </Badge>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-xs font-medium text-gray-900 line-clamp-2">{product.productTitle}</p>
                <p className="text-xs text-orange-500 font-bold">{formatPrice(product.priceUnit * quantity)}</p>
              </div>
            </div>
          ))}
        </div>
      )}

      <div className="space-y-3 text-sm">
        <div className="flex justify-between text-gray-600">
          <span>Subtotal</span>
          <span>{formatPrice(subtotal)}</span>
        </div>
        <div className="flex justify-between text-gray-600">
          <span>Shipping</span>
          <span className={shippingFee === 0 ? "text-green-600 font-medium" : ""}>
            {shippingFee === 0 ? "Free" : formatPrice(shippingFee)}
          </span>
        </div>
        {showShippingPromo && shippingPromoText && (
          <p className="text-xs text-orange-500 bg-orange-50 rounded-lg p-2">{shippingPromoText}</p>
        )}
        <div className="border-t border-gray-200 pt-3 flex justify-between font-bold text-base">
          <span>Total</span>
          <span className="text-orange-500">{formatPrice(total)}</span>
        </div>
      </div>

      {showCoupon && (
        <div className="mt-4 flex gap-2">
          <input
            type="text"
            placeholder={couponPlaceholder ?? "Coupon code"}
            className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500"
          />
        </div>
      )}

      {children}
    </div>
  );
}

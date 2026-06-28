"use client";

import Image from "next/image";
import { ShoppingBag } from "lucide-react";
import { formatPrice } from "@ecommerce/lib/utils";
import { Badge } from "@ecommerce/ui";
import type { CartItem } from "@ecommerce/lib/types";
import { cartStyles as s } from "./cart.styles";

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
    <div className={s.summary}>
      <h2 className={s.summaryTitle}>Order Summary</h2>

      {items.length > 0 && (
        <div className={s.miniList}>
          {items.map(({ product, quantity }) => (
            <div key={product.productId} className={s.miniRow}>
              <div className={s.miniThumb}>
                {product.imageUrl ? (
                  <Image src={product.imageUrl} alt={product.productTitle} fill className="object-cover" />
                ) : (
                  <div className={s.miniThumbPlaceholder}>
                    <ShoppingBag className="h-6 w-6 text-gray-300" />
                  </div>
                )}
                <Badge className={s.miniBadge}>
                  {quantity}
                </Badge>
              </div>
              <div className={s.miniInfo}>
                <p className={s.miniName}>{product.productTitle}</p>
                <p className={s.miniPrice}>{formatPrice(product.priceUnit * quantity)}</p>
              </div>
            </div>
          ))}
        </div>
      )}

      <div className={s.totals}>
        <div className={s.totalRow}>
          <span>Subtotal</span>
          <span>{formatPrice(subtotal)}</span>
        </div>
        <div className={s.totalRow}>
          <span>Shipping</span>
          <span className={shippingFee === 0 ? "text-green-600 font-medium" : ""}>
            {shippingFee === 0 ? "Free" : formatPrice(shippingFee)}
          </span>
        </div>
        {showShippingPromo && shippingPromoText && (
          <p className={s.promo}>{shippingPromoText}</p>
        )}
        <div className={s.grandTotal}>
          <span>Total</span>
          <span className={s.grandTotalValue}>{formatPrice(total)}</span>
        </div>
      </div>

      {showCoupon && (
        <div className={s.couponWrap}>
          <input
            type="text"
            placeholder={couponPlaceholder ?? "Coupon code"}
            className={s.couponInput}
          />
        </div>
      )}

      {children}
    </div>
  );
}

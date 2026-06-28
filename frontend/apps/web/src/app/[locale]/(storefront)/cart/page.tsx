"use client";

import { useTranslations } from "next-intl";
import { ShoppingCart, Trash2, ArrowRight, ShoppingBag } from "lucide-react";
import { useCartStore } from "@ecommerce/lib/store";
import { useAuthStore } from "@ecommerce/lib/store";
import { Button, EmptyState } from "@ecommerce/ui";
import { formatPrice } from "@ecommerce/lib/utils";
import { Link } from "@/i18n/navigation";
import CartItemRow from "@/components/cart/CartItemRow";
import OrderSummary from "@/components/cart/OrderSummary";
import { useRouter } from "@/i18n/navigation";

export default function CartPage() {
  const router = useRouter();
  const t = useTranslations("Cart");
  const { items, removeItem, updateQuantity, clearCart, totalPrice, totalItems } = useCartStore();
  const { isAuthenticated } = useAuthStore();

  const handleCheckout = () => {
    if (!isAuthenticated) {
      router.push("/login?redirect=/checkout");
      return;
    }
    router.push("/checkout");
  };

  if (items.length === 0) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-16">
        <EmptyState
          icon={<ShoppingBag className="h-24 w-24 text-gray-200" />}
          title={t("emptyTitle")}
          description={t("emptyHint")}
          action={
            <Link href="/products">
              <Button size="lg">
                <ShoppingCart className="h-5 w-5" />
                {t("continueShopping")}
              </Button>
            </Link>
          }
        />
      </div>
    );
  }

  const subtotal = totalPrice();
  const shipping = subtotal >= 500000 ? 0 : 30000;
  const total = subtotal + shipping;

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="flex items-center gap-2 mb-6">
        <ShoppingCart className="h-6 w-6 text-orange-500" />
        <h1 className="text-2xl font-bold text-gray-900">
          {t("title", { count: totalItems() })}
        </h1>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-3">
          {items.map((item) => (
            <CartItemRow
              key={item.product.productId}
              item={item}
              onUpdateQuantity={updateQuantity}
              onRemove={removeItem}
            />
          ))}

          <div className="flex justify-between items-center pt-2">
            <Link href="/products">
              <Button variant="ghost" size="sm">&larr; {t("continueShopping")}</Button>
            </Link>
            <Button variant="ghost" size="sm" onClick={clearCart} className="text-red-500 hover:text-red-600">
              <Trash2 className="h-4 w-4" />
              {t("clearAll")}
            </Button>
          </div>
        </div>

        <div className="lg:col-span-1">
          <OrderSummary
            items={items}
            subtotal={subtotal}
            shippingFee={shipping}
            total={total}
            showShippingPromo={shipping > 0}
            shippingPromoText={t("shippingPromo", { amount: formatPrice(500000 - totalPrice()) })}
            showCoupon
            couponPlaceholder={t("couponPlaceholder")}
          >
            <Button onClick={handleCheckout} size="lg" className="w-full mt-5">
              {t("checkout")}
              <ArrowRight className="h-4 w-4" />
            </Button>
            <div className="mt-4 flex items-center justify-center gap-4 text-xs text-gray-400">
              <span>Secure checkout</span>
              <span>|</span>
              <span>Free returns</span>
            </div>
          </OrderSummary>
        </div>
      </div>
    </div>
  );
}

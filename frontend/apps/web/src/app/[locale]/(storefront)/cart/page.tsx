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
import { cartPageStyles as s } from "./cart.styles";

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
      <div className={s.emptyPage}>
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
    <div className={s.page}>
      <div className={s.heading}>
        <ShoppingCart className="h-6 w-6 text-primary-500" />
        <h1 className={s.title}>
          {t("title", { count: totalItems() })}
        </h1>
      </div>

      <div className={s.layout}>
        <div className={s.list}>
          {items.map((item) => (
            <CartItemRow
              key={item.product.productId}
              item={item}
              onUpdateQuantity={updateQuantity}
              onRemove={removeItem}
            />
          ))}

          <div className={s.listActions}>
            <Link href="/products">
              <Button variant="ghost" size="sm">&larr; {t("continueShopping")}</Button>
            </Link>
            <Button variant="ghost" size="sm" onClick={clearCart} className="text-red-500 hover:text-red-600">
              <Trash2 className="h-4 w-4" />
              {t("clearAll")}
            </Button>
          </div>
        </div>

        <div className={s.side}>
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
            <div className={s.trust}>
              <span>{t("securePayment")}</span>
              <span>|</span>
              <span>{t("returnPolicy")}</span>
            </div>
          </OrderSummary>
        </div>
      </div>
    </div>
  );
}

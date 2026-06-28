"use client";

import { useState } from "react";
import { useTranslations } from "next-intl";
import { CheckCircle, CreditCard, Truck, MapPin } from "lucide-react";
import { useCartStore } from "@ecommerce/lib/store";
import { useAuthStore } from "@ecommerce/lib/store";
import { Button, Input } from "@ecommerce/ui";
import { orderApi, paymentApi } from "@ecommerce/lib/api";
import { useRouter } from "@/i18n/navigation";
import StepIndicator from "@/components/checkout/StepIndicator";
import OrderSummary from "@/components/cart/OrderSummary";
import { checkoutPageStyles as s } from "./checkout.styles";

type Step = 1 | 2 | 3;

const STEPS = [
  { num: 1, label: "Shipping", icon: MapPin },
  { num: 2, label: "Payment", icon: CreditCard },
  { num: 3, label: "Confirmation", icon: CheckCircle },
];

const PAYMENT_OPTIONS: Array<{ value: "COD" | "BANK_TRANSFER" | "PAYPAL"; label: string; desc: string; emoji: string }> = [
  { value: "COD", label: "COD", desc: "Cash on delivery", emoji: "\uD83D\uDCB5" },
  { value: "BANK_TRANSFER", label: "Bank transfer", desc: "Direct bank transfer", emoji: "\uD83C\uDFE6" },
  { value: "PAYPAL", label: "PayPal", desc: "Pay with PayPal", emoji: "\uD83D\uDCB3" },
];

export default function CheckoutPage() {
  const router = useRouter();
  const t = useTranslations("Checkout");
  const { items, totalPrice, clearCart } = useCartStore();
  const { user, isAuthenticated } = useAuthStore();
  const [step, setStep] = useState<Step>(1);
  const [loading, setLoading] = useState(false);
  const [orderId, setOrderId] = useState<number | null>(null);

  const [shipping, setShipping] = useState({
    fullName: user?.fullName ?? "",
    phone: user?.phone ?? "",
    address: "",
    city: "",
    district: "",
    note: "",
  });

  const [paymentMethod, setPaymentMethod] = useState<"COD" | "BANK_TRANSFER" | "PAYPAL">("COD");

  if (!isAuthenticated) {
    router.replace("/login?redirect=/checkout");
    return null;
  }

  if (items.length === 0 && step !== 3) {
    router.replace("/cart");
    return null;
  }

  const shippingFee = totalPrice() >= 500000 ? 0 : 30000;
  const total = totalPrice() + shippingFee;

  const handlePlaceOrder = async () => {
    if (!shipping.fullName || !shipping.phone || !shipping.address || !shipping.city) {
      alert(t("validationErr"));
      return;
    }
    setLoading(true);
    try {
      const firstItem = items[0];
      const orderRes = await orderApi.create({
        orderDate: new Date().toISOString(),
        orderDesc: shipping.note || "EzBuy order",
        orderFee: total,
        productId: firstItem.product.productId,
      });
      const newOrderId = orderRes.data?.data?.orderId;
      await paymentApi.create({
        isPayed: paymentMethod !== "COD",
        paymentStatus: paymentMethod === "COD" ? "NOT_STARTED" : "IN_PROGRESS",
        orderId: newOrderId,
      });
      setOrderId(newOrderId);
      clearCart();
      setStep(3);
    } catch {
      alert(t("orderFailed"));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={s.page}>
      <h1 className={s.title}>{t("title")}</h1>
      <StepIndicator current={step} steps={STEPS} />

      <div className={s.layout}>
        <div className={s.main}>
          {step === 1 && (
            <ShippingStep shipping={shipping} onChange={setShipping} onNext={() => setStep(2)} />
          )}
          {step === 2 && (
            <PaymentStep
              paymentMethod={paymentMethod}
              onPaymentChange={setPaymentMethod}
              loading={loading}
              onBack={() => setStep(1)}
              onPlaceOrder={handlePlaceOrder}
            />
          )}
          {step === 3 && <ConfirmationStep orderId={orderId} />}
        </div>

        {step < 3 && (
          <div className={s.side}>
            <OrderSummary
              items={items}
              subtotal={totalPrice()}
              shippingFee={shippingFee}
              total={total}
            />
          </div>
        )}
      </div>
    </div>
  );
}

type ShippingData = {
  fullName: string; phone: string; address: string;
  city: string; district: string; note: string;
};

function ShippingStep({
  shipping, onChange, onNext,
}: {
  shipping: ShippingData;
  onChange: React.Dispatch<React.SetStateAction<ShippingData>>;
  onNext: () => void;
}) {
  const t = useTranslations("Checkout");
  const set = (key: string) => (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) =>
    onChange((prev: ShippingData) => ({ ...prev, [key]: e.target.value }));

  return (
    <div className={s.card}>
      <div className={s.cardHead}>
        <Truck className="h-5 w-5 text-primary-500" />
        <h2 className={s.cardTitle}>{t("stepShipping")}</h2>
      </div>
      <div className={s.fields}>
        <div className={s.grid2}>
          <Input label={t("fullName")} value={shipping.fullName} onChange={set("fullName")} />
          <Input label={t("phone")} value={shipping.phone} onChange={set("phone")} />
        </div>
        <Input label={t("address")} placeholder={t("addressPlaceholder")} value={shipping.address} onChange={set("address")} />
        <div className={s.grid2}>
          <div>
            <label className={s.fieldLabel}>{t("city")}</label>
            <select
              value={shipping.city}
              onChange={set("city")}
              className={s.select}
            >
              <option value="">{t("selectCity")}</option>
              {["TP. H\u1ED3 Ch\u00ED Minh", "H\u00E0 N\u1ED9i", "\u0110\u00E0 N\u1EB5ng", "C\u1EA7n Th\u01A1", "H\u1EA3i Ph\u00F2ng"].map(c => (
                <option key={c} value={c}>{c}</option>
              ))}
            </select>
          </div>
          <Input label={t("district")} value={shipping.district} onChange={set("district")} />
        </div>
        <div>
          <label className={s.fieldLabel}>{t("note")}</label>
          <textarea
            value={shipping.note}
            onChange={set("note")}
            placeholder={t("notePlaceholder")}
            rows={3}
            className={s.textarea}
          />
        </div>
        <Button onClick={onNext} className="w-full" size="lg">{t("continueToPayment")}</Button>
      </div>
    </div>
  );
}

function PaymentStep({
  paymentMethod, onPaymentChange, loading, onBack, onPlaceOrder,
}: {
  paymentMethod: string;
  onPaymentChange: (m: "COD" | "BANK_TRANSFER" | "PAYPAL") => void;
  loading: boolean;
  onBack: () => void;
  onPlaceOrder: () => void;
}) {
  const t = useTranslations("Checkout");
  return (
    <div className={s.card}>
      <div className={s.cardHead}>
        <CreditCard className="h-5 w-5 text-primary-500" />
        <h2 className={s.cardTitle}>{t("paymentMethod")}</h2>
      </div>
      <div className={s.payList}>
        {PAYMENT_OPTIONS.map(({ value, label, desc, emoji }) => (
          <label key={value} className={`flex items-start gap-3 p-4 rounded-xl border-2 cursor-pointer transition-all ${
            paymentMethod === value ? "border-primary-500 bg-primary-50" : "border-gray-200 hover:border-primary-200"
          }`}>
            <input
              type="radio" name="payment" value={value}
              checked={paymentMethod === value}
              onChange={() => onPaymentChange(value as "COD" | "BANK_TRANSFER" | "PAYPAL")}
              className="mt-0.5 accent-primary-500"
            />
            <span className={s.payEmoji}>{emoji}</span>
            <div>
              <p className={s.payName}>{label}</p>
              <p className={s.payDesc}>{desc}</p>
            </div>
          </label>
        ))}
      </div>
      <div className={s.payActions}>
        <Button variant="outline" onClick={onBack} className="flex-1">{t("back")}</Button>
        <Button onClick={onPlaceOrder} loading={loading} className="flex-1" size="lg">
          {t("placeOrder")}
        </Button>
      </div>
    </div>
  );
}

function ConfirmationStep({ orderId }: { orderId: number | null }) {
  const t = useTranslations("Checkout");
  const router = useRouter();
  return (
    <div className={s.confirm}>
      <div className={s.confirmIcon}>
        <CheckCircle className="h-10 w-10 text-green-500" />
      </div>
      <h2 className={s.confirmTitle}>{t("successTitle")}</h2>
      <p className={s.confirmCode}>
        {t("orderCode")} <strong className="text-primary-500">#{orderId}</strong>
      </p>
      <p className={s.confirmMsg}>{t("successMsg")}</p>
      <div className={s.confirmActions}>
        <Button variant="outline" onClick={() => router.push("/orders")}>{t("viewOrders")}</Button>
        <Button onClick={() => router.push("/products")}>{t("continueShopping")}</Button>
      </div>
    </div>
  );
}

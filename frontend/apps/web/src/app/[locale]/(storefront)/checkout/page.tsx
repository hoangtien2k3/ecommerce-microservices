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
    <div className="max-w-5xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">{t("title")}</h1>
      <StepIndicator current={step} steps={STEPS} />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
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
          <div className="lg:col-span-1">
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
    <div className="bg-white rounded-xl border border-gray-200 p-6">
      <div className="flex items-center gap-2 mb-4">
        <Truck className="h-5 w-5 text-orange-500" />
        <h2 className="text-lg font-bold">{t("stepShipping")}</h2>
      </div>
      <div className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <Input label={t("fullName")} value={shipping.fullName} onChange={set("fullName")} />
          <Input label={t("phone")} value={shipping.phone} onChange={set("phone")} />
        </div>
        <Input label={t("address")} placeholder={t("addressPlaceholder")} value={shipping.address} onChange={set("address")} />
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">{t("city")}</label>
            <select
              value={shipping.city}
              onChange={set("city")}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500"
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
          <label className="block text-sm font-medium text-gray-700 mb-1">{t("note")}</label>
          <textarea
            value={shipping.note}
            onChange={set("note")}
            placeholder={t("notePlaceholder")}
            rows={3}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500"
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
    <div className="bg-white rounded-xl border border-gray-200 p-6">
      <div className="flex items-center gap-2 mb-4">
        <CreditCard className="h-5 w-5 text-orange-500" />
        <h2 className="text-lg font-bold">{t("paymentMethod")}</h2>
      </div>
      <div className="space-y-3">
        {PAYMENT_OPTIONS.map(({ value, label, desc, emoji }) => (
          <label key={value} className={`flex items-start gap-3 p-4 rounded-xl border-2 cursor-pointer transition-all ${
            paymentMethod === value ? "border-orange-500 bg-orange-50" : "border-gray-200 hover:border-orange-200"
          }`}>
            <input
              type="radio" name="payment" value={value}
              checked={paymentMethod === value}
              onChange={() => onPaymentChange(value as "COD" | "BANK_TRANSFER" | "PAYPAL")}
              className="mt-0.5 accent-orange-500"
            />
            <span className="text-2xl">{emoji}</span>
            <div>
              <p className="font-medium text-gray-900 text-sm">{label}</p>
              <p className="text-xs text-gray-500">{desc}</p>
            </div>
          </label>
        ))}
      </div>
      <div className="flex gap-3 mt-6">
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
    <div className="bg-white rounded-xl border border-gray-200 p-8 text-center">
      <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
        <CheckCircle className="h-10 w-10 text-green-500" />
      </div>
      <h2 className="text-2xl font-bold text-gray-900 mb-2">{t("successTitle")}</h2>
      <p className="text-gray-500 mb-1">
        {t("orderCode")} <strong className="text-orange-500">#{orderId}</strong>
      </p>
      <p className="text-sm text-gray-500 mb-6">{t("successMsg")}</p>
      <div className="flex gap-3 justify-center">
        <Button variant="outline" onClick={() => router.push("/orders")}>{t("viewOrders")}</Button>
        <Button onClick={() => router.push("/products")}>{t("continueShopping")}</Button>
      </div>
    </div>
  );
}

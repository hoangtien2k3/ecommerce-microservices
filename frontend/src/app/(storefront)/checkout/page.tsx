"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Image from "next/image";
import { ShoppingBag, CheckCircle, CreditCard, Truck, MapPin } from "lucide-react";
import { useCartStore } from "@/store/cartStore";
import { useAuthStore } from "@/store/authStore";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";
import { formatPrice } from "@/lib/utils";
import { orderApi, paymentApi } from "@/lib/api";

type Step = 1 | 2 | 3;

export default function CheckoutPage() {
  const router = useRouter();
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
      alert("Vui lòng điền đầy đủ thông tin giao hàng");
      return;
    }

    setLoading(true);
    try {
      // Create order for each item (simplified — real app would batch)
      const firstItem = items[0];
      const orderRes = await orderApi.create({
        orderDate: new Date().toISOString(),
        orderDesc: shipping.note || "Đơn hàng từ EzBuy",
        orderFee: total,
        productId: firstItem.product.productId,
      });

      const newOrderId = orderRes.data?.data?.orderId;

      // Create payment record
      await paymentApi.create({
        isPayed: paymentMethod !== "COD",
        paymentStatus: paymentMethod === "COD" ? "NOT_STARTED" : "IN_PROGRESS",
        orderId: newOrderId,
      });

      setOrderId(newOrderId);
      clearCart();
      setStep(3);
    } catch {
      alert("Đặt hàng thất bại. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  const steps = [
    { num: 1, label: "Thông tin giao hàng", icon: MapPin },
    { num: 2, label: "Thanh toán", icon: CreditCard },
    { num: 3, label: "Xác nhận", icon: CheckCircle },
  ];

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Thanh toán</h1>

      {/* Step indicator */}
      <div className="flex items-center mb-8">
        {steps.map(({ num, label, icon: Icon }, i) => (
          <div key={num} className="flex items-center flex-1">
            <div className="flex flex-col items-center">
              <div className={`w-10 h-10 rounded-full flex items-center justify-center border-2 transition-colors ${
                step >= num ? "bg-orange-500 border-orange-500 text-white" : "border-gray-300 text-gray-400"
              }`}>
                {step > num ? <CheckCircle className="h-5 w-5" /> : <Icon className="h-5 w-5" />}
              </div>
              <span className={`text-xs mt-1 font-medium hidden md:block ${step >= num ? "text-orange-500" : "text-gray-400"}`}>
                {label}
              </span>
            </div>
            {i < steps.length - 1 && (
              <div className={`flex-1 h-0.5 mx-2 ${step > num ? "bg-orange-500" : "bg-gray-200"}`} />
            )}
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main content */}
        <div className="lg:col-span-2">
          {/* Step 1: Shipping */}
          {step === 1 && (
            <div className="bg-white rounded-xl border border-gray-200 p-6">
              <div className="flex items-center gap-2 mb-4">
                <Truck className="h-5 w-5 text-orange-500" />
                <h2 className="text-lg font-bold">Thông tin giao hàng</h2>
              </div>
              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <Input label="Họ và tên *" value={shipping.fullName} onChange={(e) => setShipping(p => ({...p, fullName: e.target.value}))} />
                  <Input label="Số điện thoại *" value={shipping.phone} onChange={(e) => setShipping(p => ({...p, phone: e.target.value}))} />
                </div>
                <Input label="Địa chỉ *" placeholder="Số nhà, tên đường" value={shipping.address} onChange={(e) => setShipping(p => ({...p, address: e.target.value}))} />
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Tỉnh/Thành phố *</label>
                    <select
                      value={shipping.city}
                      onChange={(e) => setShipping(p => ({...p, city: e.target.value}))}
                      className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500"
                    >
                      <option value="">Chọn tỉnh/thành</option>
                      {["TP. Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Cần Thơ", "Hải Phòng"].map(c => (
                        <option key={c} value={c}>{c}</option>
                      ))}
                    </select>
                  </div>
                  <Input label="Quận/Huyện" value={shipping.district} onChange={(e) => setShipping(p => ({...p, district: e.target.value}))} />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Ghi chú</label>
                  <textarea
                    value={shipping.note}
                    onChange={(e) => setShipping(p => ({...p, note: e.target.value}))}
                    placeholder="Ghi chú cho đơn hàng..."
                    rows={3}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500"
                  />
                </div>
                <Button onClick={() => setStep(2)} className="w-full" size="lg">
                  Tiếp tục → Thanh toán
                </Button>
              </div>
            </div>
          )}

          {/* Step 2: Payment */}
          {step === 2 && (
            <div className="bg-white rounded-xl border border-gray-200 p-6">
              <div className="flex items-center gap-2 mb-4">
                <CreditCard className="h-5 w-5 text-orange-500" />
                <h2 className="text-lg font-bold">Phương thức thanh toán</h2>
              </div>
              <div className="space-y-3">
                {[
                  { value: "COD", label: "Thanh toán khi nhận hàng (COD)", desc: "Trả tiền mặt khi nhận hàng", emoji: "💵" },
                  { value: "BANK_TRANSFER", label: "Chuyển khoản ngân hàng", desc: "VietcomBank, BIDV, Techcombank...", emoji: "🏦" },
                  { value: "PAYPAL", label: "PayPal", desc: "Thanh toán qua PayPal", emoji: "💳" },
                ].map(({ value, label, desc, emoji }) => (
                  <label key={value} className={`flex items-start gap-3 p-4 rounded-xl border-2 cursor-pointer transition-all ${
                    paymentMethod === value ? "border-orange-500 bg-orange-50" : "border-gray-200 hover:border-orange-200"
                  }`}>
                    <input
                      type="radio"
                      name="payment"
                      value={value}
                      checked={paymentMethod === value}
                      onChange={() => setPaymentMethod(value as typeof paymentMethod)}
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
                <Button variant="outline" onClick={() => setStep(1)} className="flex-1">← Quay lại</Button>
                <Button onClick={handlePlaceOrder} loading={loading} className="flex-1" size="lg">
                  Đặt hàng ngay
                </Button>
              </div>
            </div>
          )}

          {/* Step 3: Success */}
          {step === 3 && (
            <div className="bg-white rounded-xl border border-gray-200 p-8 text-center">
              <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <CheckCircle className="h-10 w-10 text-green-500" />
              </div>
              <h2 className="text-2xl font-bold text-gray-900 mb-2">Đặt hàng thành công!</h2>
              <p className="text-gray-500 mb-1">Mã đơn hàng: <strong className="text-orange-500">#{orderId}</strong></p>
              <p className="text-sm text-gray-500 mb-6">Chúng tôi sẽ liên hệ xác nhận đơn hàng trong thời gian sớm nhất.</p>
              <div className="flex gap-3 justify-center">
                <Button variant="outline" onClick={() => router.push("/orders")}>
                  Xem đơn hàng
                </Button>
                <Button onClick={() => router.push("/products")}>
                  Tiếp tục mua sắm
                </Button>
              </div>
            </div>
          )}
        </div>

        {/* Order summary */}
        {step < 3 && (
          <div className="lg:col-span-1">
            <div className="bg-white rounded-xl border border-gray-200 p-5 sticky top-24">
              <h2 className="font-bold text-gray-900 mb-4">Đơn hàng của bạn</h2>
              <div className="space-y-3 max-h-60 overflow-y-auto">
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
                      <Badge variant="default" className="absolute -top-1 -right-1 w-5 h-5 flex items-center justify-center p-0 text-xs">
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
              <div className="border-t border-gray-200 mt-4 pt-4 space-y-2 text-sm">
                <div className="flex justify-between text-gray-600">
                  <span>Tạm tính</span>
                  <span>{formatPrice(totalPrice())}</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Vận chuyển</span>
                  <span>{shippingFee === 0 ? <span className="text-green-600">Miễn phí</span> : formatPrice(shippingFee)}</span>
                </div>
                <div className="flex justify-between font-bold text-base pt-2 border-t">
                  <span>Tổng cộng</span>
                  <span className="text-orange-500">{formatPrice(total)}</span>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

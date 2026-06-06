"use client";

import Link from "next/link";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { ShoppingCart, Trash2, Plus, Minus, ArrowRight, ShoppingBag } from "lucide-react";
import { useCartStore } from "@/store/cartStore";
import { Button } from "@/components/ui/Button";
import { formatPrice } from "@/lib/utils";
import { useAuthStore } from "@/store/authStore";

export default function CartPage() {
  const router = useRouter();
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
      <div className="max-w-4xl mx-auto px-4 py-16 text-center">
        <ShoppingBag className="h-24 w-24 text-gray-200 mx-auto mb-6" />
        <h2 className="text-2xl font-bold text-gray-900 mb-2">Giỏ hàng trống</h2>
        <p className="text-gray-500 mb-8">Hãy thêm sản phẩm vào giỏ hàng của bạn</p>
        <Link href="/products">
          <Button size="lg">
            <ShoppingCart className="h-5 w-5" />
            Tiếp tục mua sắm
          </Button>
        </Link>
      </div>
    );
  }

  const shipping = totalPrice() >= 500000 ? 0 : 30000;
  const total = totalPrice() + shipping;

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="flex items-center gap-2 mb-6">
        <ShoppingCart className="h-6 w-6 text-orange-500" />
        <h1 className="text-2xl font-bold text-gray-900">
          Giỏ hàng ({totalItems()} sản phẩm)
        </h1>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Cart items */}
        <div className="lg:col-span-2 space-y-3">
          {items.map(({ product, quantity }) => (
            <div key={product.productId} className="bg-white rounded-xl border border-gray-200 p-4 flex gap-4">
              {/* Image */}
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

              {/* Info */}
              <div className="flex-1 min-w-0">
                <Link href={`/products/${product.productId}`}>
                  <h3 className="text-sm font-medium text-gray-900 hover:text-orange-500 line-clamp-2">
                    {product.productTitle}
                  </h3>
                </Link>
                {product.category && (
                  <p className="text-xs text-gray-500 mt-0.5">{product.category.categoryTitle}</p>
                )}
                <p className="text-base font-bold text-orange-500 mt-1">
                  {formatPrice(product.priceUnit)}
                </p>
              </div>

              {/* Quantity & Actions */}
              <div className="flex flex-col items-end gap-3">
                <button
                  onClick={() => removeItem(product.productId)}
                  className="text-gray-400 hover:text-red-500 transition-colors"
                >
                  <Trash2 className="h-4 w-4" />
                </button>

                <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden">
                  <button
                    onClick={() => updateQuantity(product.productId, quantity - 1)}
                    className="px-2 py-1.5 hover:bg-gray-100 transition-colors"
                  >
                    <Minus className="h-3.5 w-3.5" />
                  </button>
                  <span className="px-3 py-1.5 text-sm font-semibold border-x border-gray-300 min-w-[40px] text-center">
                    {quantity}
                  </span>
                  <button
                    onClick={() => updateQuantity(product.productId, quantity + 1)}
                    className="px-2 py-1.5 hover:bg-gray-100 transition-colors"
                  >
                    <Plus className="h-3.5 w-3.5" />
                  </button>
                </div>

                <p className="text-sm font-bold text-gray-900">
                  {formatPrice(product.priceUnit * quantity)}
                </p>
              </div>
            </div>
          ))}

          <div className="flex justify-between items-center pt-2">
            <Link href="/products">
              <Button variant="ghost" size="sm">
                ← Tiếp tục mua sắm
              </Button>
            </Link>
            <Button variant="ghost" size="sm" onClick={clearCart} className="text-red-500 hover:text-red-600">
              <Trash2 className="h-4 w-4" />
              Xóa tất cả
            </Button>
          </div>
        </div>

        {/* Order summary */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-xl border border-gray-200 p-5 sticky top-24">
            <h2 className="text-lg font-bold text-gray-900 mb-4">Tóm tắt đơn hàng</h2>

            <div className="space-y-3 text-sm">
              <div className="flex justify-between text-gray-600">
                <span>Tạm tính ({totalItems()} sp)</span>
                <span>{formatPrice(totalPrice())}</span>
              </div>
              <div className="flex justify-between text-gray-600">
                <span>Phí vận chuyển</span>
                <span className={shipping === 0 ? "text-green-600 font-medium" : ""}>
                  {shipping === 0 ? "Miễn phí" : formatPrice(shipping)}
                </span>
              </div>
              {shipping > 0 && (
                <p className="text-xs text-orange-500 bg-orange-50 rounded-lg p-2">
                  Mua thêm {formatPrice(500000 - totalPrice())} để được miễn phí vận chuyển
                </p>
              )}
              <div className="border-t border-gray-200 pt-3 flex justify-between font-bold text-base">
                <span>Tổng cộng</span>
                <span className="text-orange-500">{formatPrice(total)}</span>
              </div>
            </div>

            {/* Coupon input */}
            <div className="mt-4">
              <div className="flex gap-2">
                <input
                  type="text"
                  placeholder="Mã giảm giá"
                  className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500"
                />
                <Button variant="outline" size="sm">Áp dụng</Button>
              </div>
            </div>

            <Button onClick={handleCheckout} size="lg" className="w-full mt-5">
              Tiến hành thanh toán
              <ArrowRight className="h-4 w-4" />
            </Button>

            <div className="mt-4 flex items-center justify-center gap-4 text-xs text-gray-400">
              <span>🔒 Thanh toán an toàn</span>
              <span>|</span>
              <span>30 ngày đổi trả</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

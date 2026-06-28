import Link from "next/link";
import { Package, Mail, Phone, MapPin, Globe, MessageCircle, Send, Video } from "lucide-react";

export default function Footer() {
  return (
    <footer className="bg-gray-900 text-gray-400 mt-auto">
      <div className="max-w-7xl mx-auto px-4 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Brand */}
          <div>
            <Link href="/" className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 bg-orange-500 rounded-lg flex items-center justify-center">
                <Package className="h-5 w-5 text-white" />
              </div>
              <span className="text-xl font-bold text-white">
                Ez<span className="text-orange-500">Buy</span>
              </span>
            </Link>
            <p className="text-sm leading-relaxed mb-4">
              Nền tảng thương mại điện tử hàng đầu Việt Nam. Mua sắm thông minh, giao hàng nhanh chóng.
            </p>
            <div className="flex gap-3">
              {[Globe, MessageCircle, Send, Video].map((Icon, i) => (
                <a
                  key={i}
                  href="#"
                  className="w-8 h-8 bg-gray-800 rounded-lg flex items-center justify-center hover:bg-orange-500 transition-colors"
                >
                  <Icon className="h-4 w-4 text-white" />
                </a>
              ))}
            </div>
          </div>

          {/* Quick links */}
          <div>
            <h3 className="text-white font-semibold mb-4">Danh mục</h3>
            <ul className="space-y-2 text-sm">
              {["Điện tử", "Thời trang", "Nhà cửa & Đời sống", "Sức khỏe & Làm đẹp", "Thể thao", "Đồ chơi"].map(
                (cat) => (
                  <li key={cat}>
                    <Link href={`/products?category=${encodeURIComponent(cat)}`} className="hover:text-orange-500 transition-colors">
                      {cat}
                    </Link>
                  </li>
                )
              )}
            </ul>
          </div>

          {/* Help */}
          <div>
            <h3 className="text-white font-semibold mb-4">Hỗ trợ</h3>
            <ul className="space-y-2 text-sm">
              {[
                { href: "/help", label: "Trung tâm trợ giúp" },
                { href: "/orders", label: "Theo dõi đơn hàng" },
                { href: "/returns", label: "Chính sách đổi trả" },
                { href: "/shipping", label: "Thông tin vận chuyển" },
                { href: "/contact", label: "Liên hệ" },
                { href: "/about", label: "Về chúng tôi" },
              ].map(({ href, label }) => (
                <li key={href}>
                  <Link href={href} className="hover:text-orange-500 transition-colors">
                    {label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h3 className="text-white font-semibold mb-4">Liên hệ</h3>
            <ul className="space-y-3 text-sm">
              <li className="flex items-start gap-3">
                <MapPin className="h-4 w-4 text-orange-500 flex-shrink-0 mt-0.5" />
                <span>123 Đường ABC, Quận 1, TP. Hồ Chí Minh</span>
              </li>
              <li className="flex items-center gap-3">
                <Phone className="h-4 w-4 text-orange-500 flex-shrink-0" />
                <span>1800 6789</span>
              </li>
              <li className="flex items-center gap-3">
                <Mail className="h-4 w-4 text-orange-500 flex-shrink-0" />
                <span>support@ezbuy.vn</span>
              </li>
            </ul>
            <div className="mt-4">
              <p className="text-sm font-medium text-white mb-2">Nhận tin khuyến mãi</p>
              <div className="flex">
                <input
                  type="email"
                  placeholder="Email của bạn"
                  className="flex-1 bg-gray-800 text-white text-sm px-3 py-2 rounded-l-lg outline-none border border-gray-700 focus:border-orange-500"
                />
                <button className="bg-orange-500 text-white text-sm px-3 py-2 rounded-r-lg hover:bg-orange-600 transition-colors">
                  Đăng ký
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="border-t border-gray-800">
        <div className="max-w-7xl mx-auto px-4 py-4 flex flex-col md:flex-row justify-between items-center gap-2 text-xs">
          <span>© 2024 EzBuy. Tất cả quyền được bảo lưu.</span>
          <div className="flex gap-4">
            <Link href="/privacy" className="hover:text-orange-500">Chính sách bảo mật</Link>
            <Link href="/terms" className="hover:text-orange-500">Điều khoản sử dụng</Link>
          </div>
        </div>
      </div>
    </footer>
  );
}

// Mock data for the storefront homepage. The backend is not wired up yet, so the
// homepage renders from these fixtures to showcase the full CellphoneS-style UI.
// Swap each section over to its real `use*` hook once the corresponding service is
// available — the card/section components already accept the live `Product` shape.

export interface ShowcaseProduct {
  productId: number;
  productTitle: string;
  imageUrl?: string;
  priceUnit: number;
  oldPrice?: number;
  discountPercent?: number;
  rating?: number;
  sold?: number;
  stockTotal?: number; // for flash-sale progress bar
  memberExtra?: number; // "Smember giảm thêm" amount
  installment?: boolean;
  badge?: string;
  category?: { categoryId: number; categoryTitle: string };
}

export interface SidebarCategory {
  id: number;
  emoji: string;
  label: string;
  href: string;
}

export interface BrandChip {
  id: number;
  label: string;
  href: string;
}

// NOTE: label-bearing items carry a translation `key` (resolved against the
// next-intl messages) instead of hardcoded text. Only structural data lives here.
export interface HeroSlide {
  id: number;
  key: string; // -> Home.{key}Title / Home.{key}Sub
  href: string;
  gradient: string;
}

export interface CategoryIcon {
  id: number;
  key: string; // -> Category.{key}
  emoji: string;
  href: string;
}

export interface BrandLogo {
  id: number;
  name: string; // proper noun, not translated
  href: string;
}

export const heroSlides: HeroSlide[] = [
  { id: 1, key: "slide1", href: "/products?categoryId=1", gradient: "from-rose-600 via-primary-600 to-primary-700" },
  { id: 2, key: "slide2", href: "/products?categoryId=2", gradient: "from-indigo-600 via-blue-600 to-sky-600" },
  { id: 3, key: "slide3", href: "/products?categoryId=3", gradient: "from-emerald-600 via-teal-600 to-cyan-600" },
];

export const heroSideBanners = [
  { id: 1, key: "side1", gradient: "from-amber-500 to-orange-600", href: "/products" },
  { id: 2, key: "side2", gradient: "from-purple-600 to-fuchsia-600", href: "/products" },
];

export const quickDeals = [
  { id: 1, key: "deal1", emoji: "⚡", href: "/products?sort=priceUnit,asc" },
  { id: 2, key: "deal2", emoji: "🎓", href: "/products" },
  { id: 3, key: "deal3", emoji: "♻️", href: "/products" },
  { id: 4, key: "deal4", emoji: "🆕", href: "/products?sort=productId,desc" },
  { id: 5, key: "deal5", emoji: "🎟️", href: "/products" },
];

export const categoryIcons: CategoryIcon[] = [
  { id: 1, key: "c1", emoji: "📱", href: "/products?categoryId=1" },
  { id: 2, key: "c2", emoji: "💻", href: "/products?categoryId=2" },
  { id: 3, key: "c3", emoji: "📲", href: "/products?categoryId=3" },
  { id: 4, key: "c4", emoji: "🎧", href: "/products?categoryId=4" },
  { id: 5, key: "c5", emoji: "⌚", href: "/products?categoryId=5" },
  { id: 6, key: "c6", emoji: "📷", href: "/products?categoryId=6" },
  { id: 7, key: "c7", emoji: "🏠", href: "/products?categoryId=7" },
  { id: 8, key: "c8", emoji: "🖥️", href: "/products?categoryId=8" },
  { id: 9, key: "c9", emoji: "🔌", href: "/products?categoryId=9" },
  { id: 10, key: "c10", emoji: "📺", href: "/products?categoryId=10" },
];

export const brands: BrandLogo[] = [
  { id: 1, name: "Apple", href: "/products?brand=apple" },
  { id: 2, name: "Samsung", href: "/products?brand=samsung" },
  { id: 3, name: "Xiaomi", href: "/products?brand=xiaomi" },
  { id: 4, name: "OPPO", href: "/products?brand=oppo" },
  { id: 5, name: "ASUS", href: "/products?brand=asus" },
  { id: 6, name: "Dell", href: "/products?brand=dell" },
  { id: 7, name: "Sony", href: "/products?brand=sony" },
  { id: 8, name: "Lenovo", href: "/products?brand=lenovo" },
];

export const flashSaleProducts: ShowcaseProduct[] = [
  { productId: 101, productTitle: "Điện thoại Galaxy A55 5G 8GB/256GB", priceUnit: 8490000, oldPrice: 10990000, discountPercent: 23, rating: 5, sold: 120, stockTotal: 150, installment: true, category: { categoryId: 1, categoryTitle: "Điện thoại" } },
  { productId: 102, productTitle: "Tai nghe Bluetooth chống ồn Pro 2", priceUnit: 4290000, oldPrice: 6490000, discountPercent: 34, rating: 5, sold: 340, stockTotal: 400, installment: false, category: { categoryId: 4, categoryTitle: "Âm thanh" } },
  { productId: 103, productTitle: "Laptop UltraBook 14 inch i5/16GB/512GB", priceUnit: 15990000, oldPrice: 19990000, discountPercent: 20, rating: 4, sold: 56, stockTotal: 100, installment: true, category: { categoryId: 2, categoryTitle: "Laptop" } },
  { productId: 104, productTitle: "Đồng hồ thông minh Watch Active 3", priceUnit: 2990000, oldPrice: 4490000, discountPercent: 33, rating: 5, sold: 210, stockTotal: 250, installment: false, category: { categoryId: 5, categoryTitle: "Đồng hồ" } },
  { productId: 105, productTitle: "Máy tính bảng Tab S9 11 inch 128GB", priceUnit: 11490000, oldPrice: 14990000, discountPercent: 23, rating: 4, sold: 88, stockTotal: 120, installment: true, category: { categoryId: 3, categoryTitle: "Tablet" } },
  { productId: 106, productTitle: "Sạc dự phòng 20.000mAh PD 22.5W", priceUnit: 590000, oldPrice: 990000, discountPercent: 40, rating: 5, sold: 530, stockTotal: 600, installment: false, category: { categoryId: 9, categoryTitle: "Phụ kiện" } },
];

export const hotProducts: ShowcaseProduct[] = [
  { productId: 201, productTitle: "Điện thoại Galaxy S24 Ultra 12GB/256GB", priceUnit: 27990000, oldPrice: 33990000, discountPercent: 18, rating: 5, sold: 412, installment: true, badge: "Trả góp 0%", category: { categoryId: 1, categoryTitle: "Điện thoại" } },
  { productId: 202, productTitle: "Điện thoại Redmi Note 13 Pro 8GB/256GB", priceUnit: 6190000, oldPrice: 7490000, discountPercent: 17, rating: 5, sold: 980, installment: true, category: { categoryId: 1, categoryTitle: "Điện thoại" } },
  { productId: 203, productTitle: "Laptop Gaming RTX 4060 i7/16GB/1TB", priceUnit: 28990000, oldPrice: 32990000, discountPercent: 12, rating: 5, sold: 134, installment: true, category: { categoryId: 2, categoryTitle: "Laptop" } },
  { productId: 204, productTitle: "Tai nghe chụp tai gaming RGB 7.1", priceUnit: 890000, oldPrice: 1290000, discountPercent: 31, rating: 4, sold: 760, installment: false, category: { categoryId: 4, categoryTitle: "Âm thanh" } },
  { productId: 205, productTitle: "Điện thoại OPPO Reno11 F 5G 8GB/256GB", priceUnit: 8290000, oldPrice: 9990000, discountPercent: 17, rating: 5, sold: 256, installment: true, category: { categoryId: 1, categoryTitle: "Điện thoại" } },
  { productId: 206, productTitle: "Bàn phím cơ không dây hot-swap", priceUnit: 1190000, oldPrice: 1690000, discountPercent: 30, rating: 5, sold: 420, installment: false, category: { categoryId: 9, categoryTitle: "Phụ kiện" } },
  { productId: 207, productTitle: "Màn hình 27 inch 2K 165Hz IPS", priceUnit: 4990000, oldPrice: 6490000, discountPercent: 23, rating: 5, sold: 188, installment: true, category: { categoryId: 8, categoryTitle: "Màn hình" } },
  { productId: 208, productTitle: "Robot hút bụi lau nhà thông minh", priceUnit: 6990000, oldPrice: 9990000, discountPercent: 30, rating: 4, sold: 145, installment: true, category: { categoryId: 7, categoryTitle: "Gia dụng" } },
  { productId: 209, productTitle: "Củ sạc nhanh GaN 65W 3 cổng", priceUnit: 490000, oldPrice: 790000, discountPercent: 38, rating: 5, sold: 1120, installment: false, category: { categoryId: 9, categoryTitle: "Phụ kiện" } },
  { productId: 210, productTitle: "Tivi 4K 55 inch Google TV", priceUnit: 9990000, oldPrice: 13990000, discountPercent: 28, rating: 5, sold: 99, installment: true, category: { categoryId: 10, categoryTitle: "Tivi" } },
];

// Left vertical category list shown in the hero (CellphoneS signature sidebar).
export const heroSidebar: SidebarCategory[] = [
  { id: 1, emoji: "📱", label: "Điện thoại, Tablet", href: "/products?categoryId=1" },
  { id: 2, emoji: "💻", label: "Laptop", href: "/products?categoryId=2" },
  { id: 3, emoji: "🎧", label: "Âm thanh, Mic thu âm", href: "/products?categoryId=4" },
  { id: 4, emoji: "⌚", label: "Đồng hồ, Camera", href: "/products?categoryId=5" },
  { id: 5, emoji: "🏠", label: "Đồ gia dụng", href: "/products?categoryId=7" },
  { id: 6, emoji: "🔌", label: "Phụ kiện", href: "/products?categoryId=9" },
  { id: 7, emoji: "🖥️", label: "PC, Màn hình, Máy in", href: "/products?categoryId=8" },
  { id: 8, emoji: "📺", label: "Tivi", href: "/products?categoryId=10" },
  { id: 9, emoji: "🎮", label: "Tản nhiệt, Gaming Gear", href: "/products?categoryId=9" },
  { id: 10, emoji: "♻️", label: "Hàng cũ giá rẻ", href: "/products" },
];

// Brand filter chips per category section.
export const phoneBrands: BrandChip[] = [
  { id: 1, label: "Apple", href: "/products?brand=apple" },
  { id: 2, label: "Samsung", href: "/products?brand=samsung" },
  { id: 3, label: "Xiaomi", href: "/products?brand=xiaomi" },
  { id: 4, label: "OPPO", href: "/products?brand=oppo" },
  { id: 5, label: "realme", href: "/products?brand=realme" },
  { id: 6, label: "Nokia", href: "/products?brand=nokia" },
  { id: 7, label: "Sony", href: "/products?brand=sony" },
  { id: 8, label: "Honor", href: "/products?brand=honor" },
];

export const laptopBrands: BrandChip[] = [
  { id: 1, label: "MacBook", href: "/products?brand=macbook" },
  { id: 2, label: "ASUS", href: "/products?brand=asus" },
  { id: 3, label: "Lenovo", href: "/products?brand=lenovo" },
  { id: 4, label: "Acer", href: "/products?brand=acer" },
  { id: 5, label: "HP", href: "/products?brand=hp" },
  { id: 6, label: "Dell", href: "/products?brand=dell" },
  { id: 7, label: "MSI", href: "/products?brand=msi" },
  { id: 8, label: "Gigabyte", href: "/products?brand=gigabyte" },
];

export const phoneProducts: ShowcaseProduct[] = [
  { productId: 301, productTitle: "Điện thoại Galaxy A57 5G 8GB/256GB", priceUnit: 8490000, oldPrice: 9990000, discountPercent: 15, rating: 5, sold: 320, memberExtra: 300000, installment: true, category: { categoryId: 1, categoryTitle: "Điện thoại" } },
  { productId: 302, productTitle: "Điện thoại Galaxy S24 8GB/256GB", priceUnit: 19990000, oldPrice: 22990000, discountPercent: 13, rating: 5, sold: 210, memberExtra: 500000, installment: true, category: { categoryId: 1, categoryTitle: "Điện thoại" } },
  { productId: 303, productTitle: "Điện thoại OPPO Reno13 F 5G", priceUnit: 8990000, oldPrice: 10490000, discountPercent: 14, rating: 4, sold: 156, memberExtra: 200000, installment: true, category: { categoryId: 1, categoryTitle: "Điện thoại" } },
  { productId: 304, productTitle: "Điện thoại Xiaomi Redmi Note 14", priceUnit: 5290000, oldPrice: 6290000, discountPercent: 16, rating: 5, sold: 540, memberExtra: 150000, installment: true, category: { categoryId: 1, categoryTitle: "Điện thoại" } },
  { productId: 305, productTitle: "Điện thoại realme C75 6GB/128GB", priceUnit: 4490000, oldPrice: 5290000, discountPercent: 15, rating: 4, sold: 280, installment: true, category: { categoryId: 1, categoryTitle: "Điện thoại" } },
];

export const laptopProducts: ShowcaseProduct[] = [
  { productId: 401, productTitle: "Laptop MacBook Air M3 13 inch 16GB/256GB", priceUnit: 24990000, oldPrice: 27990000, discountPercent: 11, rating: 5, sold: 132, memberExtra: 500000, installment: true, category: { categoryId: 2, categoryTitle: "Laptop" } },
  { productId: 402, productTitle: "Laptop ASUS Vivobook 15 i5/16GB/512GB", priceUnit: 14990000, oldPrice: 17990000, discountPercent: 17, rating: 5, sold: 240, memberExtra: 300000, installment: true, category: { categoryId: 2, categoryTitle: "Laptop" } },
  { productId: 403, productTitle: "Laptop Gaming Lenovo LOQ RTX 4060", priceUnit: 25990000, oldPrice: 29990000, discountPercent: 13, rating: 5, sold: 98, memberExtra: 700000, installment: true, category: { categoryId: 2, categoryTitle: "Laptop" } },
  { productId: 404, productTitle: "Laptop Acer Aspire 5 i5/8GB/512GB", priceUnit: 12490000, oldPrice: 14990000, discountPercent: 16, rating: 4, sold: 310, installment: true, category: { categoryId: 2, categoryTitle: "Laptop" } },
  { productId: 405, productTitle: "Laptop HP Pavilion 14 i7/16GB/1TB", priceUnit: 18990000, oldPrice: 21990000, discountPercent: 13, rating: 5, sold: 76, memberExtra: 400000, installment: true, category: { categoryId: 2, categoryTitle: "Laptop" } },
];

// "Flash sale" resets at the top of the next hour.
export function nextFlashSaleDeadline(): number {
  const now = new Date();
  const next = new Date(now);
  next.setHours(now.getHours() + 1, 0, 0, 0);
  return next.getTime();
}

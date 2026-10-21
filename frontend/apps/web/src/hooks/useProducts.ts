import { useQuery } from "@tanstack/react-query";
import { productApi } from "@ecommerce/lib/api";
import type { ApiResponse, PaginatedResponse, Product } from "@ecommerce/lib/types";

// 백엔드 GET /api/products 는 파라미터 없이 전체 목록을 준다.
// 그래서 한 번만 받아 캐시해 두고, 필터·정렬은 화면 쪽(select)에서 한다.
interface ProductFilters {
  sort?: string; // "priceUnit,asc" 처럼 "필드,방향"
  categoryId?: number;
  search?: string;
  minPrice?: number;
  maxPrice?: number;
}

async function fetchAllProducts(): Promise<Product[]> {
  const res = await productApi.getAll();
  const raw = res.data;
  if (Array.isArray(raw) && Array.isArray(raw[0])) return raw[0] as Product[];
  if (Array.isArray(raw)) return raw as Product[];
  const wrapped = raw as ApiResponse<PaginatedResponse<Product>>;
  return wrapped?.data?.content ?? [];
}

function applyFilters(products: Product[], filters: ProductFilters): Product[] {
  const { sort, categoryId, search, minPrice, maxPrice } = filters;
  const keyword = search?.trim().toLowerCase();

  const result = products.filter(
    (p) =>
      (categoryId === undefined || p.category?.categoryId === categoryId) &&
      (minPrice === undefined || p.priceUnit >= minPrice) &&
      (maxPrice === undefined || p.priceUnit < maxPrice) &&
      (!keyword || p.productTitle.toLowerCase().includes(keyword))
  );

  if (!sort) return result;
  const [field, dir] = sort.split(",") as [keyof Product, string];
  const sign = dir === "desc" ? -1 : 1;
  return [...result].sort((a, b) => {
    const x = a[field];
    const y = b[field];
    if (typeof x === "number" && typeof y === "number") return (x - y) * sign;
    return String(x ?? "").localeCompare(String(y ?? "")) * sign;
  });
}

export function useProducts(filters: ProductFilters = {}) {
  return useQuery({
    queryKey: ["products"],
    queryFn: fetchAllProducts,
    select: (products) => applyFilters(products, filters),
  });
}

export function useProduct(id: string | number) {
  const productId = typeof id === "string" ? Number(id) : id;

  return useQuery({
    queryKey: ["product", productId],
    queryFn: async () => {
      const res = await productApi.getById(productId);
      return (res.data as ApiResponse<Product>).data ?? null;
    },
    enabled: !!productId,
  });
}

export function useAdminProducts(page = 0) {
  return useQuery({
    queryKey: ["admin-products-list", page],
    queryFn: async () => {
      const res = await productApi.getAll({ page, size: 10 });
      return res.data as ApiResponse<PaginatedResponse<Product>>;
    },
  });
}

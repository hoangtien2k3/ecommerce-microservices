export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message?: string;
  data?: T;
  errors?: string[];
  path?: string;
  traceId?: string;
  timestamp: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Auth
export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  username: string;
  email: string;
  password: string;
  gender: string;
  phone?: string;
}

export interface TokenResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  refresh_expires_in: number;
  token_type: string;
  scope: string;
}

export interface UserResponse {
  id: number;
  fullName: string;
  username: string;
  email: string;
  gender: string;
  phone?: string;
  avatar?: string;
  roles: Role[];
}

export interface Role {
  id: number;
  name: string;
}

// Product
export interface Category {
  categoryId: number;
  categoryTitle: string;
  imageUrl?: string;
  parentCategory?: Category;
  subCategories?: Category[];
  products?: Product[];
}

export interface Product {
  productId: number;
  productTitle: string;
  imageUrl?: string;
  sku?: string;
  priceUnit: number;
  quantity: number;
  category?: Category;
  description?: string;
}

// Cart & Order
export interface Cart {
  cartId: number;
  userId: number;
  orderItems?: OrderItem[];
}

export interface OrderItem {
  orderId: number;
  productId: number;
  quantity: number;
  unitPrice: number;
  product?: Product;
}

export interface Order {
  orderId: number;
  orderDate: string;
  orderDesc?: string;
  orderFee: number;
  productId: number;
  cart?: Cart;
}

// Payment
export interface Payment {
  paymentId: number;
  isPayed: boolean;
  paymentStatus: PaymentStatus;
  orderId: number;
}

export type PaymentStatus = 'IN_PROGRESS' | 'COMPLETED' | 'NOT_STARTED' | 'FAILED' | 'CANCELLED';

// Inventory
export interface InventoryItem {
  id: number;
  skuCode: string;
  quantity: number;
}

// Favourite
export interface Favourite {
  userId: number;
  productId: number;
  product?: Product;
}

// Rating
export interface Rating {
  id: number;
  productId: number;
  userId: string;
  ratingValue: number;
  comment?: string;
  createdAt: string;
}

// Notification
export interface Notification {
  id: number;
  userId: number;
  message: string;
  isRead: boolean;
  createdAt: string;
}

// Cart store types
export interface CartItem {
  product: Product;
  quantity: number;
}

export interface CartState {
  items: CartItem[];
  addItem: (product: Product, quantity?: number) => void;
  removeItem: (productId: number) => void;
  updateQuantity: (productId: number, quantity: number) => void;
  clearCart: () => void;
  totalItems: () => number;
  totalPrice: () => number;
}

// Auth store types
export interface AuthState {
  user: UserResponse | null;
  token: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  setAuth: (user: UserResponse, token: string, refreshToken: string) => void;
  logout: () => void;
}

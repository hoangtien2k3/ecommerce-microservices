// Types for the first-come coupon page, plus the fixtures it used before the
// storefront list API existed. hooks/useCoupons.ts now calls the real API
// (GET /promotion/storefront/promotions, POST .../{id}/issue); the mock
// functions below are kept for local work without a backend.

export interface CouponEvent {
  promotionId: number;
  name: string;
  description?: string;
  discountAmount: number; // fixed discount in VND
  minimumOrderPurchaseAmount?: number;
  issueLimit: number; // total first-come quantity
  issuedCount: number; // quantity issued so far
  startDate: string; // ISO 8601, issuing opens
  endDate: string; // ISO 8601, issuing closes
  claimed: boolean; // whether the current user already has this coupon
}

// Outcome of pressing the issue button, mapped from the backend response:
//   201                                     -> success
//   409 promotion.coupon.already.claimed    -> alreadyClaimed
//   409 promotion.coupon.sold.out           -> soldOut
//   400 promotion.coupon.not.open           -> notOpen
//   anything else (network error, 5xx)      -> error
export type IssueResult = "success" | "alreadyClaimed" | "soldOut" | "notOpen" | "error";

// What the card shows, derived from a coupon and the current time.
export type CouponStatus = "upcoming" | "open" | "claimed" | "soldOut" | "ended";

export function getCouponStatus(coupon: CouponEvent, now: number): CouponStatus {
  if (coupon.claimed) return "claimed";
  if (now > Date.parse(coupon.endDate)) return "ended";
  if (coupon.issuedCount >= coupon.issueLimit) return "soldOut";
  if (now < Date.parse(coupon.startDate)) return "upcoming";
  return "open";
}

const MINUTE = 60_000;
const HOUR = 60 * MINUTE;

// Times are relative to the first load so every status shows up on the page:
// open, almost sold out, opening soon, sold out and ended.
function buildMockCoupons(now: number): CouponEvent[] {
  const iso = (offset: number) => new Date(now + offset).toISOString();
  return [
    {
      promotionId: 101,
      name: "Giảm 10.000₫ cho mọi đơn",
      description: "Fixed discount on any product",
      discountAmount: 10_000,
      minimumOrderPurchaseAmount: 100_000,
      issueLimit: 100,
      issuedCount: 63,
      startDate: iso(-1 * HOUR),
      endDate: iso(5 * HOUR),
      claimed: false,
    },
    {
      promotionId: 102,
      name: "Giảm 50.000₫ đơn từ 500K",
      description: "For orders of 500,000₫ or more",
      discountAmount: 50_000,
      minimumOrderPurchaseAmount: 500_000,
      issueLimit: 30,
      issuedCount: 28,
      startDate: iso(-30 * MINUTE),
      endDate: iso(3 * HOUR),
      claimed: false,
    },
    {
      promotionId: 103,
      name: "Flash coupon 100.000₫",
      description: "Opens soon and sells out fast",
      discountAmount: 100_000,
      minimumOrderPurchaseAmount: 1_000_000,
      issueLimit: 10,
      issuedCount: 0,
      startDate: iso(2 * MINUTE),
      endDate: iso(2 * HOUR),
      claimed: false,
    },
    {
      promotionId: 104,
      name: "Giảm 20.000₫ cuối tuần",
      discountAmount: 20_000,
      minimumOrderPurchaseAmount: 200_000,
      issueLimit: 50,
      issuedCount: 50,
      startDate: iso(-2 * HOUR),
      endDate: iso(4 * HOUR),
      claimed: false,
    },
    {
      promotionId: 105,
      name: "Giảm 30.000₫ khai trương",
      discountAmount: 30_000,
      issueLimit: 200,
      issuedCount: 140,
      startDate: iso(-26 * HOUR),
      endDate: iso(-2 * HOUR),
      claimed: false,
    },
  ];
}

// In-memory "server" state so issuing changes the counts until the page reloads.
let store: CouponEvent[] | null = null;
let failNextIssueOf102 = true;

const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

export async function mockFetchCoupons(): Promise<CouponEvent[]> {
  await delay(300);
  store ??= buildMockCoupons(Date.now());
  return store.map((c) => ({ ...c }));
}

export async function mockIssueCoupon(promotionId: number): Promise<IssueResult> {
  await delay(600);
  const coupon = store?.find((c) => c.promotionId === promotionId);
  if (!coupon) return "error";
  const now = Date.now();
  if (coupon.claimed) return "alreadyClaimed";
  if (now < Date.parse(coupon.startDate) || now > Date.parse(coupon.endDate)) return "notOpen";
  if (coupon.issuedCount >= coupon.issueLimit) return "soldOut";
  // Fail the first attempt on coupon 102 so the error state and retry can be seen.
  if (promotionId === 102 && failNextIssueOf102) {
    failNextIssueOf102 = false;
    return "error";
  }
  coupon.issuedCount += 1;
  coupon.claimed = true;
  return "success";
}

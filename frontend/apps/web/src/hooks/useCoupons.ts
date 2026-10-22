import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { couponApi } from "@ecommerce/lib/api";
import type { ApiResponse } from "@ecommerce/lib/types";
import type { CouponEvent, IssueResult } from "@/data/couponMock";

// 실제 API 사용 중. true 로 바꾸면 비로그인 사용자도 발급 버튼을 누를 수 있다 (mock 시절 동작).
export const COUPON_MOCK_MODE = false;

// GET /promotion/storefront/promotions 응답 항목 (promotion-service StorefrontCouponVm)
interface StorefrontCoupon {
  promotionId: number;
  name: string;
  description?: string;
  discountType: "FIXED" | "PERCENTAGE";
  discountAmount?: number;
  discountPercentage?: number;
  minimumOrderPurchaseAmount?: number;
  issueLimit: number;
  issuedCount: number;
  startDate: string;
  endDate: string;
  claimed: boolean;
}

async function fetchCoupons(): Promise<CouponEvent[]> {
  const res = await couponApi.getAll();
  const list = (res.data as ApiResponse<StorefrontCoupon[]>).data ?? [];
  return list.map((c) => ({
    promotionId: c.promotionId,
    name: c.name,
    description: c.description,
    // 화면은 정액 할인만 표시한다. 정률 쿠폰은 금액 0 으로 보인다.
    discountAmount: c.discountAmount ?? 0,
    discountType: c.discountType,
    discountPercentage: c.discountPercentage,
    minimumOrderPurchaseAmount: c.minimumOrderPurchaseAmount,
    issueLimit: c.issueLimit,
    issuedCount: c.issuedCount,
    startDate: c.startDate,
    endDate: c.endDate,
    claimed: c.claimed,
  }));
}

// 백엔드 ErrorCode (common-lib) 의 code / message 키를 화면 결과로 바꾼다.
const ISSUE_ERRORS: Record<string, IssueResult> = {
  "PRM-6001": "notOpen",
  "promotion.coupon.not.open": "notOpen",
  "PRM-6002": "soldOut",
  "promotion.coupon.sold.out": "soldOut",
  "PRM-6003": "alreadyClaimed",
  "promotion.coupon.already.claimed": "alreadyClaimed",
};

async function issueCoupon(promotionId: number): Promise<IssueResult> {
  try {
    await couponApi.issue(promotionId);
    return "success";
  } catch (e) {
    // axios 오류면 response.data 에 ApiResponse 오류 본문이 있다.
    const body = (e as { response?: { data?: { code?: string; message?: string } } }).response?.data;
    return ISSUE_ERRORS[body?.code ?? ""] ?? ISSUE_ERRORS[body?.message ?? ""] ?? "error";
  }
}

export const COUPONS_KEY = ["coupons"] as const;

export function useCoupons() {
  return useQuery({
    queryKey: COUPONS_KEY,
    queryFn: fetchCoupons,
  });
}

// 체크아웃에서 고를 수 있는 쿠폰: 내가 발급받았고 지금 사용 기간 안에 있는 것.
// 목록 API는 이미 사용한 쿠폰도 claimed 로 주므로, 사용한 쿠폰은 주문할 때 백엔드가 거부한다.
export function isUsableCoupon(coupon: CouponEvent, now: number): boolean {
  return coupon.claimed && now >= Date.parse(coupon.startDate) && now <= Date.parse(coupon.endDate);
}

// order-service 의 할인 계산과 같게 맞춘다 (정률은 주문 금액의 %, 정액은 그 금액, 주문 금액보다 크지 않게).
export function couponDiscount(coupon: CouponEvent, amount: number): number {
  const discount = coupon.discountType === "PERCENTAGE"
    ? Math.round((amount * (coupon.discountPercentage ?? 0)) / 100)
    : coupon.discountAmount;
  return Math.min(discount, amount);
}

export function useIssueCoupon() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (promotionId: number) => issueCoupon(promotionId),
    // Every outcome can change the remaining count (someone else may have
    // taken the last one), so always refetch the list.
    onSettled: () => queryClient.invalidateQueries({ queryKey: COUPONS_KEY }),
  });
}

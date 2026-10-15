import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { mockFetchCoupons, mockIssueCoupon } from "@/data/couponMock";

// The two functions below are the only place that touches the coupon data
// source. Swap them for real API calls (lib/api.ts) once the list API exists,
// and set COUPON_MOCK_MODE to false so the page asks guests to log in first.
export const COUPON_MOCK_MODE = true;

const fetchCoupons = mockFetchCoupons;
const issueCoupon = mockIssueCoupon;

const COUPONS_KEY = ["coupons"] as const;

export function useCoupons() {
  return useQuery({
    queryKey: COUPONS_KEY,
    queryFn: fetchCoupons,
  });
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

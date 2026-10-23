"use client";

import { useEffect, useState } from "react";
import { useTranslations } from "next-intl";
import { TicketX } from "lucide-react";
import { EmptyState, LoadingSkeleton } from "@ecommerce/ui";
import { useAuthStore } from "@ecommerce/lib/store";
import { useRouter } from "@/i18n/navigation";
import { COUPON_MOCK_MODE, useCoupons, useIssueCoupon } from "@/hooks";
import { getCouponStatus, type CouponEvent, type IssueResult } from "@/data/couponMock";
import CouponHero from "./CouponHero";
import CouponCard from "./CouponCard";
import IssueNotice from "./IssueNotice";
import { couponStyles as s } from "./coupon.styles";

interface Notice {
  result: IssueResult;
  coupon: CouponEvent;
}

export default function CouponEventList() {
  const t = useTranslations("Coupons");
  const router = useRouter();
  const { isAuthenticated } = useAuthStore();
  const { data: coupons, isLoading, isError } = useCoupons();
  const issue = useIssueCoupon();
  const [notice, setNotice] = useState<Notice | null>(null);

  // Ticking clock for countdowns and open/close transitions. It starts as null
  // and is set after mount so server and client render the same markup.
  const [now, setNow] = useState<number | null>(null);
  useEffect(() => {
    const tick = () => setNow(Date.now());
    tick();
    const timer = setInterval(tick, 1000);
    return () => clearInterval(timer);
  }, []);

  const handleIssue = (coupon: CouponEvent) => {
    if (!COUPON_MOCK_MODE && !isAuthenticated) {
      router.push("/login?redirect=/coupons");
      return;
    }
    setNotice(null);
    issue.mutate(coupon.promotionId, {
      onSuccess: (result) => setNotice({ result, coupon }),
      onError: () => setNotice({ result: "error", coupon }),
    });
  };

  const nextOpenAt =
    now === null || !coupons
      ? null
      : coupons
          .map((c) => Date.parse(c.startDate))
          .filter((start) => start > now)
          .sort((a, b) => a - b)[0] ?? null;

  return (
    <div className="max-w-5xl mx-auto px-4 py-6 space-y-4">
      <CouponHero nextOpenAt={nextOpenAt} now={now} />

      {notice && (
        <IssueNotice
          result={notice.result}
          couponName={notice.coupon.name}
          onRetry={() => handleIssue(notice.coupon)}
          onDismiss={() => setNotice(null)}
        />
      )}

      {isLoading || now === null ? (
        <LoadingSkeleton rows={4} />
      ) : isError ? (
        <EmptyState icon={<TicketX className="h-20 w-20 text-gray-200" />} title={t("loadError")} />
      ) : !coupons || coupons.length === 0 ? (
        <EmptyState
          icon={<TicketX className="h-20 w-20 text-gray-200" />}
          title={t("emptyTitle")}
          description={t("emptyHint")}
        />
      ) : (
        <div className={coupons.length === 1 ? s.gridSingle : s.grid}>
          {coupons.map((coupon) => (
            <CouponCard
              key={coupon.promotionId}
              coupon={coupon}
              status={getCouponStatus(coupon, now)}
              now={now}
              issuing={issue.isPending && issue.variables === coupon.promotionId}
              onIssue={handleIssue}
              featured={coupons.length === 1}
            />
          ))}
        </div>
      )}
    </div>
  );
}

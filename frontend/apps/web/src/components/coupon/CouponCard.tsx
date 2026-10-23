"use client";

import { useTranslations } from "next-intl";
import { Check, Clock } from "lucide-react";
import { Badge, Button } from "@ecommerce/ui";
import { cn, formatPrice } from "@ecommerce/lib/utils";
import type { CouponEvent, CouponStatus } from "@/data/couponMock";
import { formatCountdown, formatShortTime } from "./countdown";
import { couponStyles as s } from "./coupon.styles";

const BADGE: Record<CouponStatus, { variant: "default" | "success" | "danger" | "info"; key: string }> = {
  open: { variant: "danger", key: "statusOpen" },
  upcoming: { variant: "info", key: "statusUpcoming" },
  claimed: { variant: "success", key: "statusClaimed" },
  soldOut: { variant: "default", key: "statusSoldOut" },
  ended: { variant: "default", key: "statusEnded" },
};

interface CouponCardProps {
  coupon: CouponEvent;
  status: CouponStatus;
  now: number;
  issuing: boolean;
  onIssue: (coupon: CouponEvent) => void;
  // 쿠폰이 하나뿐일 때: 가로 전체 폭에 크게 보여 준다
  featured?: boolean;
}

export default function CouponCard({ coupon, status, now, issuing, onIssue, featured = false }: CouponCardProps) {
  const t = useTranslations("Coupons");
  const left = Math.max(0, coupon.issueLimit - coupon.issuedCount);
  const ratio = coupon.issueLimit > 0 ? Math.round((left / coupon.issueLimit) * 100) : 0;
  const inactive = status === "soldOut" || status === "ended";
  const badge = BADGE[status];

  return (
    <div className={cn(s.card, inactive && s.cardInactive)}>
      <div className={cn(s.stub, featured && s.stubFeatured, inactive && s.stubMuted)}>
        <span className={cn(s.stubAmount, featured && s.stubAmountFeatured)}>{formatPrice(coupon.discountAmount)}</span>
        <span className={cn(s.stubLabel, featured && s.stubLabelFeatured)}>{t("discountLabel")}</span>
      </div>

      <div className={cn(s.body, featured && s.bodyFeatured)}>
        <div className={s.bodyHeader}>
          <h3 className={cn(s.name, featured && s.nameFeatured)}>{coupon.name}</h3>
          <Badge variant={badge.variant} className="shrink-0">{t(badge.key)}</Badge>
        </div>
        {coupon.description && <p className={cn(s.desc, featured && s.descFeatured)}>{coupon.description}</p>}
        <p className={cn(s.minOrder, featured && s.minOrderFeatured)}>
          {coupon.minimumOrderPurchaseAmount
            ? t("minOrder", { amount: formatPrice(coupon.minimumOrderPurchaseAmount) })
            : t("noMinOrder")}
        </p>

        {/* Remaining quantity */}
        <div className={cn(s.bar, featured && s.barFeatured)}>
          <div className={s.barFill} style={{ width: `${ratio}%` }} />
          <span className={cn(s.barLabel, featured && s.barLabelFeatured)}>{t("remaining", { left, total: coupon.issueLimit })}</span>
        </div>

        <div className={cn(s.footer, featured && s.footerFeatured)}>
          <span className={cn(s.time, featured && s.timeFeatured)}>
            <Clock className="h-3.5 w-3.5" />
            {status === "upcoming"
              ? t("opensAt", { time: formatShortTime(coupon.startDate, now) })
              : status === "ended"
                ? t("endedAt", { time: formatShortTime(coupon.endDate, now) })
                : t("endsAt", { time: formatShortTime(coupon.endDate, now) })}
          </span>
          <CouponAction coupon={coupon} status={status} now={now} issuing={issuing} onIssue={onIssue} featured={featured} />
        </div>
      </div>
    </div>
  );
}

function CouponAction({ coupon, status, now, issuing, onIssue, featured = false }: CouponCardProps) {
  const t = useTranslations("Coupons");
  const size = featured ? "lg" : "sm";
  const action = cn(s.action, featured && s.actionFeatured);

  switch (status) {
    case "open":
      return (
        <Button size={size} className={action} loading={issuing} onClick={() => onIssue(coupon)}>
          {t("actionIssue")}
        </Button>
      );
    case "upcoming":
      return (
        <Button size={size} variant="outline" className={action} disabled>
          {t("actionUpcoming", { countdown: formatCountdown(Date.parse(coupon.startDate) - now) })}
        </Button>
      );
    case "claimed":
      return (
        <Button size={size} variant="ghost" className={cn(action, "text-green-700")} disabled>
          <Check className="h-4 w-4" /> {t("statusClaimed")}
        </Button>
      );
    case "soldOut":
      return (
        <Button size={size} variant="secondary" className={action} disabled>
          {t("statusSoldOut")}
        </Button>
      );
    case "ended":
      return (
        <Button size={size} variant="secondary" className={action} disabled>
          {t("statusEnded")}
        </Button>
      );
  }
}

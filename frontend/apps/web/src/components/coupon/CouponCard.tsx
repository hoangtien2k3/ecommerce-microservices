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
}

export default function CouponCard({ coupon, status, now, issuing, onIssue }: CouponCardProps) {
  const t = useTranslations("Coupons");
  const left = Math.max(0, coupon.issueLimit - coupon.issuedCount);
  const ratio = coupon.issueLimit > 0 ? Math.round((left / coupon.issueLimit) * 100) : 0;
  const inactive = status === "soldOut" || status === "ended";
  const badge = BADGE[status];

  return (
    <div className={cn(s.card, inactive && s.cardInactive)}>
      <div className={cn(s.stub, inactive && s.stubMuted)}>
        <span className={s.stubAmount}>{formatPrice(coupon.discountAmount)}</span>
        <span className={s.stubLabel}>{t("discountLabel")}</span>
      </div>

      <div className={s.body}>
        <div className={s.bodyHeader}>
          <h3 className={s.name}>{coupon.name}</h3>
          <Badge variant={badge.variant} className="shrink-0">{t(badge.key)}</Badge>
        </div>
        {coupon.description && <p className={s.desc}>{coupon.description}</p>}
        <p className={s.minOrder}>
          {coupon.minimumOrderPurchaseAmount
            ? t("minOrder", { amount: formatPrice(coupon.minimumOrderPurchaseAmount) })
            : t("noMinOrder")}
        </p>

        {/* Remaining quantity */}
        <div className={s.bar}>
          <div className={s.barFill} style={{ width: `${ratio}%` }} />
          <span className={s.barLabel}>{t("remaining", { left, total: coupon.issueLimit })}</span>
        </div>

        <div className={s.footer}>
          <span className={s.time}>
            <Clock className="h-3.5 w-3.5" />
            {status === "upcoming"
              ? t("opensAt", { time: formatShortTime(coupon.startDate, now) })
              : status === "ended"
                ? t("endedAt", { time: formatShortTime(coupon.endDate, now) })
                : t("endsAt", { time: formatShortTime(coupon.endDate, now) })}
          </span>
          <CouponAction coupon={coupon} status={status} now={now} issuing={issuing} onIssue={onIssue} />
        </div>
      </div>
    </div>
  );
}

function CouponAction({ coupon, status, now, issuing, onIssue }: CouponCardProps) {
  const t = useTranslations("Coupons");

  switch (status) {
    case "open":
      return (
        <Button size="sm" className={s.action} loading={issuing} onClick={() => onIssue(coupon)}>
          {t("actionIssue")}
        </Button>
      );
    case "upcoming":
      return (
        <Button size="sm" variant="outline" className={s.action} disabled>
          {t("actionUpcoming", { countdown: formatCountdown(Date.parse(coupon.startDate) - now) })}
        </Button>
      );
    case "claimed":
      return (
        <Button size="sm" variant="ghost" className={cn(s.action, "text-green-700")} disabled>
          <Check className="h-4 w-4" /> {t("statusClaimed")}
        </Button>
      );
    case "soldOut":
      return (
        <Button size="sm" variant="secondary" className={s.action} disabled>
          {t("statusSoldOut")}
        </Button>
      );
    case "ended":
      return (
        <Button size="sm" variant="secondary" className={s.action} disabled>
          {t("statusEnded")}
        </Button>
      );
  }
}

"use client";

import { useTranslations } from "next-intl";
import { TicketPercent } from "lucide-react";
import { countdownParts } from "./countdown";
import { couponStyles as s } from "./coupon.styles";

interface CouponHeroProps {
  // Start time (epoch ms) of the next coupon that has not opened yet, if any.
  nextOpenAt: number | null;
  // Current time, or null before the page has mounted (avoids hydration mismatch).
  now: number | null;
}

export default function CouponHero({ nextOpenAt, now }: CouponHeroProps) {
  const t = useTranslations("Coupons");
  const showCountdown = nextOpenAt !== null && now !== null && nextOpenAt > now;
  const [hh, mm, ss] = showCountdown ? countdownParts(nextOpenAt - now) : ["--", "--", "--"];

  return (
    <section className={s.hero}>
      <div>
        <h1 className={s.heroTitle}>
          <TicketPercent className="h-7 w-7 text-amber-300" />
          {t("title")}
        </h1>
        <p className={s.heroSub}>{t("subtitle")}</p>
      </div>

      {showCountdown && (
        <div className={s.heroCountWrap}>
          <span className={s.heroCountLabel}>{t("nextOpenIn")}</span>
          <span className={s.heroCountBox}>{hh}</span>
          <span className="text-white font-bold">:</span>
          <span className={s.heroCountBox}>{mm}</span>
          <span className="text-white font-bold">:</span>
          <span className={s.heroCountBox}>{ss}</span>
        </div>
      )}
    </section>
  );
}

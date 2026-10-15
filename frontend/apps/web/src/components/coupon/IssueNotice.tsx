"use client";

import { useTranslations } from "next-intl";
import { AlertCircle, CheckCircle2, Info, X } from "lucide-react";
import { Button } from "@ecommerce/ui";
import { cn } from "@ecommerce/lib/utils";
import type { IssueResult } from "@/data/couponMock";
import { couponStyles as s, noticeTone } from "./coupon.styles";

const MESSAGE_KEY: Record<IssueResult, string> = {
  success: "resultSuccess",
  alreadyClaimed: "resultAlreadyClaimed",
  soldOut: "resultSoldOut",
  notOpen: "resultNotOpen",
  error: "resultError",
};

interface IssueNoticeProps {
  result: IssueResult;
  couponName: string;
  onRetry?: () => void;
  onDismiss: () => void;
}

// Banner shown above the coupon grid after the issue button is pressed.
export default function IssueNotice({ result, couponName, onRetry, onDismiss }: IssueNoticeProps) {
  const t = useTranslations("Coupons");
  const Icon = result === "success" ? CheckCircle2 : result === "error" ? AlertCircle : Info;

  return (
    <div role="status" aria-live="polite" className={cn(s.notice, noticeTone[result])}>
      <Icon className="h-5 w-5 shrink-0" />
      <p className={s.noticeText}>{t(MESSAGE_KEY[result], { name: couponName })}</p>
      {result === "error" && onRetry && (
        <Button size="sm" variant="danger" onClick={onRetry}>
          {t("retry")}
        </Button>
      )}
      <button type="button" aria-label={t("dismiss")} className={s.noticeClose} onClick={onDismiss}>
        <X className="h-4 w-4" />
      </button>
    </div>
  );
}

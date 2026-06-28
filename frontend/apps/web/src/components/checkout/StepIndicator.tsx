"use client";

import { CheckCircle } from "lucide-react";
import { cn } from "@ecommerce/lib/utils";
import { checkoutStyles as s } from "./checkout.styles";

interface StepIndicatorProps {
  current: number;
  steps: { num: number; label: string; icon: React.ElementType }[];
}

export default function StepIndicator({ current, steps }: StepIndicatorProps) {
  return (
    <div className={s.stepBar}>
      {steps.map(({ num, label, icon: Icon }, i) => (
        <div key={num} className={s.stepItem}>
          <div className={cn(s.stepCircleBase, current >= num ? s.stepCircleActive : s.stepCircleIdle)}>
            {current > num ? <CheckCircle className="h-5 w-5" /> : <Icon className="h-5 w-5" />}
          </div>
          <span className={cn(s.stepLabelBase, current >= num ? s.stepLabelActive : s.stepLabelIdle)}>
            {label}
          </span>
          {i < steps.length - 1 && (
            <div className={cn(s.stepLineBase, current > num ? s.stepLineActive : s.stepLineIdle)} />
          )}
        </div>
      ))}
    </div>
  );
}

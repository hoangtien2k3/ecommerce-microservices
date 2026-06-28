"use client";

import { CheckCircle, MapPin, CreditCard } from "lucide-react";
import { cn } from "@ecommerce/lib/utils";

interface StepIndicatorProps {
  current: number;
  steps: { num: number; label: string; icon: React.ElementType }[];
}

export default function StepIndicator({ current, steps }: StepIndicatorProps) {
  return (
    <div className="flex items-center mb-8">
      {steps.map(({ num, label, icon: Icon }, i) => (
        <div key={num} className="flex items-center flex-1">
          <div className={cn(
            "w-10 h-10 rounded-full flex items-center justify-center border-2 transition-colors",
            current >= num
              ? "bg-orange-500 border-orange-500 text-white"
              : "border-gray-300 text-gray-400"
          )}>
            {current > num ? <CheckCircle className="h-5 w-5" /> : <Icon className="h-5 w-5" />}
          </div>
          <span className={cn(
            "text-xs mt-1 font-medium hidden md:block",
            current >= num ? "text-orange-500" : "text-gray-400"
          )}>
            {label}
          </span>
          {i < steps.length - 1 && (
            <div className={cn("flex-1 h-0.5 mx-2", current > num ? "bg-orange-500" : "bg-gray-200")} />
          )}
        </div>
      ))}
    </div>
  );
}

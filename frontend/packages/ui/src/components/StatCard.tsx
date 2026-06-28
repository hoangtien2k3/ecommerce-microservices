"use client";

import { ArrowUpRight, ArrowDownRight } from "lucide-react";
import { cn } from "@ecommerce/lib/utils";

interface StatCardProps {
  label: string;
  value: string;
  icon: React.ElementType;
  color?: string;
  change?: string;
  up?: boolean;
  className?: string;
}

export function StatCard({ label, value, icon: Icon, color = "bg-gray-100 text-gray-600", change, up, className }: StatCardProps) {
  return (
    <div className={cn("bg-white rounded-xl border border-gray-200 p-5", className)}>
      <div className="flex items-center justify-between mb-3">
        <div className={cn("w-10 h-10 rounded-xl flex items-center justify-center", color)}>
          <Icon className="h-5 w-5" />
        </div>
        {change && (
          <span className={cn("flex items-center gap-1 text-xs font-medium", up ? "text-green-500" : "text-red-500")}>
            {up ? <ArrowUpRight className="h-3.5 w-3.5" /> : <ArrowDownRight className="h-3.5 w-3.5" />}
            {change}
          </span>
        )}
      </div>
      <p className="text-2xl font-bold text-gray-900">{value}</p>
      <p className="text-sm text-gray-500 mt-0.5">{label}</p>
    </div>
  );
}

interface StatsGridProps {
  stats: StatCardProps[];
  columns?: number;
}

export function StatsGrid({ stats, columns = 4 }: StatsGridProps) {
  const colClass = {
    1: "grid-cols-1",
    2: "grid-cols-1 sm:grid-cols-2",
    3: "grid-cols-1 sm:grid-cols-2 xl:grid-cols-3",
    4: "grid-cols-1 sm:grid-cols-2 xl:grid-cols-4",
  }[columns] ?? "grid-cols-1 sm:grid-cols-2 xl:grid-cols-4";

  return (
    <div className={cn("grid gap-4", colClass)}>
      {stats.map((s) => <StatCard key={s.label} {...s} />)}
    </div>
  );
}

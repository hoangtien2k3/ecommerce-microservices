"use client";
import { BarChart3 } from "lucide-react";
export default function AdminInventoryPage() {
  return (
    <div className="space-y-5">
      <h1 className="text-2xl font-bold text-gray-900">Kho hàng</h1>
      <div className="bg-white rounded-xl border border-gray-200 p-12 text-center">
        <BarChart3 className="h-16 w-16 text-gray-300 mx-auto mb-4" />
        <p className="text-gray-500">Tính năng đang phát triển</p>
      </div>
    </div>
  );
}

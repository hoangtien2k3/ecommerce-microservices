"use client";
import { Truck } from "lucide-react";
export default function AdminShippingPage() {
  return (
    <div className="space-y-5">
      <h1 className="text-2xl font-bold text-gray-900">Vận chuyển</h1>
      <div className="bg-white rounded-xl border border-gray-200 p-12 text-center">
        <Truck className="h-16 w-16 text-gray-300 mx-auto mb-4" />
        <p className="text-gray-500">Tính năng đang phát triển</p>
      </div>
    </div>
  );
}

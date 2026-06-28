"use client";
import { Users } from "lucide-react";
export default function AdminUsersPage() {
  return (
    <div className="space-y-5">
      <h1 className="text-2xl font-bold text-gray-900">Người dùng</h1>
      <div className="bg-white rounded-xl border border-gray-200 p-12 text-center">
        <Users className="h-16 w-16 text-gray-300 mx-auto mb-4" />
        <p className="text-gray-500">Tính năng đang phát triển</p>
      </div>
    </div>
  );
}

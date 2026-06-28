import type { Metadata } from "next";
import LoginForm from "@/components/auth/LoginForm";

export const metadata: Metadata = { title: "Đăng nhập" };

export default function LoginPage() {
  return (
    <div className="w-full max-w-md">
      <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Chào mừng trở lại!</h1>
          <p className="text-gray-500 text-sm mt-1">Đăng nhập để tiếp tục mua sắm</p>
        </div>
        <LoginForm />
      </div>
    </div>
  );
}

import type { Metadata } from "next";
import RegisterForm from "@/components/auth/RegisterForm";

export const metadata: Metadata = { title: "Đăng ký" };

export default function RegisterPage() {
  return (
    <div className="w-full max-w-lg">
      <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Tạo tài khoản mới</h1>
          <p className="text-gray-500 text-sm mt-1">Tham gia EzBuy để nhận ưu đãi tốt nhất</p>
        </div>
        <RegisterForm />
      </div>
    </div>
  );
}

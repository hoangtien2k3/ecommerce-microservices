"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Eye, EyeOff, User, Mail, Lock, Phone } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { authApi } from "@/lib/api";

export default function RegisterForm() {
  const router = useRouter();
  const [form, setForm] = useState({
    fullName: "",
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
    gender: "MALE",
    phone: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const validate = () => {
    if (!form.fullName || !form.username || !form.email || !form.password) {
      return "Vui lòng điền đầy đủ thông tin bắt buộc";
    }
    if (form.password.length < 8) {
      return "Mật khẩu phải có ít nhất 8 ký tự";
    }
    if (form.password !== form.confirmPassword) {
      return "Mật khẩu xác nhận không khớp";
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      return "Email không hợp lệ";
    }
    return null;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    setError("");

    try {
      await authApi.register({
        fullName: form.fullName,
        username: form.username,
        email: form.email,
        password: form.password,
        gender: form.gender,
        phone: form.phone || undefined,
      });

      setSuccess(true);
      setTimeout(() => router.push("/login"), 2000);
    } catch (err: unknown) {
      const data = (err as { response?: { data?: { message?: string } } })?.response?.data;
      setError(data?.message ?? "Đăng ký thất bại. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="text-center py-8">
        <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <span className="text-3xl">✓</span>
        </div>
        <h3 className="text-lg font-semibold text-gray-900 mb-2">Đăng ký thành công!</h3>
        <p className="text-gray-600 text-sm">Đang chuyển hướng đến trang đăng nhập...</p>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-600 rounded-lg px-4 py-3 text-sm">
          {error}
        </div>
      )}

      <div className="grid grid-cols-2 gap-3">
        <Input
          name="fullName"
          label="Họ và tên *"
          placeholder="Nguyễn Văn A"
          value={form.fullName}
          onChange={handleChange}
          leftIcon={<User className="h-4 w-4" />}
          required
        />
        <Input
          name="username"
          label="Tên đăng nhập *"
          placeholder="nguyenvana"
          value={form.username}
          onChange={handleChange}
          required
        />
      </div>

      <Input
        name="email"
        type="email"
        label="Email *"
        placeholder="example@email.com"
        value={form.email}
        onChange={handleChange}
        leftIcon={<Mail className="h-4 w-4" />}
        autoComplete="email"
        required
      />

      <Input
        name="phone"
        type="tel"
        label="Số điện thoại"
        placeholder="0912345678"
        value={form.phone}
        onChange={handleChange}
        leftIcon={<Phone className="h-4 w-4" />}
      />

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Giới tính *</label>
        <select
          name="gender"
          value={form.gender}
          onChange={handleChange}
          className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-orange-500"
        >
          <option value="MALE">Nam</option>
          <option value="FEMALE">Nữ</option>
          <option value="OTHER">Khác</option>
        </select>
      </div>

      <div className="relative">
        <Input
          name="password"
          type={showPassword ? "text" : "password"}
          label="Mật khẩu *"
          placeholder="Ít nhất 8 ký tự"
          value={form.password}
          onChange={handleChange}
          leftIcon={<Lock className="h-4 w-4" />}
          required
        />
        <button
          type="button"
          onClick={() => setShowPassword(!showPassword)}
          className="absolute right-3 top-8 text-gray-400 hover:text-gray-600"
        >
          {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
        </button>
      </div>

      <Input
        name="confirmPassword"
        type="password"
        label="Xác nhận mật khẩu *"
        placeholder="Nhập lại mật khẩu"
        value={form.confirmPassword}
        onChange={handleChange}
        leftIcon={<Lock className="h-4 w-4" />}
        required
      />

      <Button type="submit" loading={loading} className="w-full" size="lg">
        Đăng ký
      </Button>

      <p className="text-center text-sm text-gray-600">
        Đã có tài khoản?{" "}
        <Link href="/login" className="text-orange-500 font-medium hover:text-orange-600">
          Đăng nhập
        </Link>
      </p>
    </form>
  );
}

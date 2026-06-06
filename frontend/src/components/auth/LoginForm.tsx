"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Eye, EyeOff, Mail, Lock } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { authApi } from "@/lib/api";
import { useAuthStore } from "@/store/authStore";

export default function LoginForm() {
  const router = useRouter();
  const { setAuth } = useAuthStore();
  const [form, setForm] = useState({ username: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.username || !form.password) {
      setError("Vui lòng điền đầy đủ thông tin");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const res = await authApi.login(form.username, form.password);
      const tokenData = res.data?.data;

      if (tokenData?.access_token) {
        // Get user profile
        localStorage.setItem("access_token", tokenData.access_token);
        try {
          const profileRes = await authApi.getProfile();
          const user = profileRes.data?.data;
          if (user) {
            setAuth(user, tokenData.access_token, tokenData.refresh_token);
          }
        } catch {
          // If profile fetch fails, still log in with minimal info
          setAuth(
            { id: 0, fullName: form.username, username: form.username, email: "", gender: "", roles: [] },
            tokenData.access_token,
            tokenData.refresh_token
          );
        }
        router.push("/");
        router.refresh();
      }
    } catch (err: unknown) {
      const status = (err as { response?: { status: number } })?.response?.status;
      if (status === 401 || status === 400) {
        setError("Tên đăng nhập hoặc mật khẩu không đúng");
      } else {
        setError("Có lỗi xảy ra. Vui lòng thử lại sau.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-600 rounded-lg px-4 py-3 text-sm">
          {error}
        </div>
      )}

      <Input
        name="username"
        label="Tên đăng nhập"
        placeholder="Nhập tên đăng nhập"
        value={form.username}
        onChange={handleChange}
        autoComplete="username"
        leftIcon={<Mail className="h-4 w-4" />}
        required
      />

      <div className="relative">
        <Input
          name="password"
          type={showPassword ? "text" : "password"}
          label="Mật khẩu"
          placeholder="Nhập mật khẩu"
          value={form.password}
          onChange={handleChange}
          autoComplete="current-password"
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

      <div className="flex items-center justify-between text-sm">
        <label className="flex items-center gap-2 cursor-pointer">
          <input type="checkbox" className="rounded border-gray-300 text-orange-500" />
          <span className="text-gray-600">Ghi nhớ đăng nhập</span>
        </label>
        <Link href="/forgot-password" className="text-orange-500 hover:text-orange-600">
          Quên mật khẩu?
        </Link>
      </div>

      <Button type="submit" loading={loading} className="w-full" size="lg">
        Đăng nhập
      </Button>

      <p className="text-center text-sm text-gray-600">
        Chưa có tài khoản?{" "}
        <Link href="/register" className="text-orange-500 font-medium hover:text-orange-600">
          Đăng ký ngay
        </Link>
      </p>
    </form>
  );
}

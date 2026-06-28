"use client";

import React, { useState } from "react";
import { Eye, EyeOff, Mail, Lock } from "lucide-react";
import { useTranslations } from "next-intl";
import { Button, Input } from "@ecommerce/ui";
import { useLogin } from "@/hooks";
import { Link } from "@/i18n/navigation";

export default function LoginForm() {
  const t = useTranslations("Auth");
  const loginMutation = useLogin();
  const [form, setForm] = useState({ username: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);

  const error = loginMutation.isError
    ? (() => {
        const status = (loginMutation.error as { response?: { status: number } })?.response?.status;
        if (status === 401 || status === 400) return t("errCredentials");
        return t("errServer");
      })()
    : "";

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.username || !form.password) return;
    loginMutation.mutate(form);
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
        label={t("usernameLabel")}
        placeholder={t("usernamePlaceholder")}
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
          label={t("passwordLabel")}
          placeholder={t("passwordPlaceholder")}
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
          <span className="text-gray-600">{t("rememberMe")}</span>
        </label>
        <Link href="/forgot-password" className="text-orange-500 hover:text-orange-600">
          {t("forgotPassword")}
        </Link>
      </div>

      <Button type="submit" loading={loginMutation.isPending} className="w-full" size="lg">
        {t("loginBtn")}
      </Button>

      <p className="text-center text-sm text-gray-600">
        {t("noAccount")}{" "}
        <Link href="/register" className="text-orange-500 font-medium hover:text-orange-600">
          {t("registerNow")}
        </Link>
      </p>
    </form>
  );
}

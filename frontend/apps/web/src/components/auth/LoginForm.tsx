"use client";

import React, { useState } from "react";
import { Eye, EyeOff, Mail, Lock } from "lucide-react";
import { useTranslations } from "next-intl";
import { Button, Input } from "@ecommerce/ui";
import { useLogin } from "@/hooks";
import { Link } from "@/i18n/navigation";
import { authStyles as s } from "./auth.styles";

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
    <form onSubmit={handleSubmit} className={s.form}>
      {error && (
        <div className={s.errorBox}>
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

      <div className={s.passwordWrap}>
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
          className={s.passwordToggle}
        >
          {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
        </button>
      </div>

      <div className={s.optionsRow}>
        <label className={s.rememberLabel}>
          <input type="checkbox" className={s.rememberCheckbox} />
          <span className={s.rememberText}>{t("rememberMe")}</span>
        </label>
        <Link href="/forgot-password" className={s.link}>
          {t("forgotPassword")}
        </Link>
      </div>

      <Button type="submit" loading={loginMutation.isPending} className="w-full" size="lg">
        {t("loginBtn")}
      </Button>

      <p className={s.switchText}>
        {t("noAccount")}{" "}
        <Link href="/register" className={s.switchLink}>
          {t("registerNow")}
        </Link>
      </p>
    </form>
  );
}

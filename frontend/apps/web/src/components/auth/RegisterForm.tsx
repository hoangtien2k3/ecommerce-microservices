"use client";

import React, { useState } from "react";
import { Eye, EyeOff, User, Mail, Lock, Phone } from "lucide-react";
import { useTranslations } from "next-intl";
import { Button, Input } from "@ecommerce/ui";
import { useRegister } from "@/hooks";
import { Link, useRouter } from "@/i18n/navigation";

export default function RegisterForm() {
  const router = useRouter();
  const t = useTranslations("Auth");
  const registerMutation = useRegister();
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
  const [validationError, setValidationError] = useState("");
  const [success, setSuccess] = useState(false);

  const error = validationError || (
    registerMutation.isError
      ? (registerMutation.error as { response?: { data?: { message?: string } } })?.response?.data?.message ?? t("errRegister")
      : ""
  );

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setValidationError("");
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.fullName || !form.username || !form.email || !form.password) {
      setValidationError(t("errRequired"));
      return;
    }
    if (form.password.length < 8) { setValidationError(t("errPasswordLen")); return; }
    if (form.password !== form.confirmPassword) { setValidationError(t("errPasswordMatch")); return; }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) { setValidationError(t("errEmail")); return; }

    registerMutation.mutate(
      {
        fullName: form.fullName,
        username: form.username,
        email: form.email,
        password: form.password,
        gender: form.gender,
        phone: form.phone || undefined,
      },
      {
        onSuccess: () => {
          setSuccess(true);
          setTimeout(() => router.push("/login"), 2000);
        },
      }
    );
  };

  if (success) {
    return (
      <div className="text-center py-8">
        <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <span className="text-3xl">&#10003;</span>
        </div>
        <h3 className="text-lg font-semibold text-gray-900 mb-2">{t("successTitle")}</h3>
        <p className="text-gray-600 text-sm">{t("successRedirect")}</p>
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
          label={t("fullNameLabel")}
          placeholder={t("fullNamePlaceholder")}
          value={form.fullName}
          onChange={handleChange}
          leftIcon={<User className="h-4 w-4" />}
          required
        />
        <Input
          name="username"
          label={t("usernameRegLabel")}
          placeholder={t("usernameRegPlaceholder")}
          value={form.username}
          onChange={handleChange}
          required
        />
      </div>

      <Input
        name="email"
        type="email"
        label={t("emailLabel")}
        placeholder={t("emailPlaceholder")}
        value={form.email}
        onChange={handleChange}
        leftIcon={<Mail className="h-4 w-4" />}
        autoComplete="email"
        required
      />

      <Input
        name="phone"
        type="tel"
        label={t("phoneLabel")}
        placeholder={t("phonePlaceholder")}
        value={form.phone}
        onChange={handleChange}
        leftIcon={<Phone className="h-4 w-4" />}
      />

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">{t("genderLabel")}</label>
        <select
          name="gender"
          value={form.gender}
          onChange={handleChange}
          className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-orange-500"
        >
          <option value="MALE">{t("genderMale")}</option>
          <option value="FEMALE">{t("genderFemale")}</option>
          <option value="OTHER">{t("genderOther")}</option>
        </select>
      </div>

      <div className="relative">
        <Input
          name="password"
          type={showPassword ? "text" : "password"}
          label={t("passwordRegLabel")}
          placeholder={t("passwordHint")}
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
        label={t("confirmPasswordLabel")}
        placeholder={t("confirmPasswordPlaceholder")}
        value={form.confirmPassword}
        onChange={handleChange}
        leftIcon={<Lock className="h-4 w-4" />}
        required
      />

      <Button type="submit" loading={registerMutation.isPending} className="w-full" size="lg">
        {t("registerBtn")}
      </Button>

      <p className="text-center text-sm text-gray-600">
        {t("hasAccount")}{" "}
        <Link href="/login" className="text-orange-500 font-medium hover:text-orange-600">
          {t("loginLink")}
        </Link>
      </p>
    </form>
  );
}

"use client";

import React, { useState } from "react";
import { Eye, EyeOff, User, Mail, Lock, Phone } from "lucide-react";
import { useTranslations } from "next-intl";
import { Button, Input } from "@ecommerce/ui";
import { useRegister } from "@/hooks";
import { Link, useRouter } from "@/i18n/navigation";
import { authStyles as s } from "./auth.styles";

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
      <div className={s.successWrap}>
        <div className={s.successIcon}>
          <span className={s.successIconText}>&#10003;</span>
        </div>
        <h3 className={s.successTitle}>{t("successTitle")}</h3>
        <p className={s.successText}>{t("successRedirect")}</p>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className={s.form}>
      {error && (
        <div className={s.errorBox}>
          {error}
        </div>
      )}

      <div className={s.grid2}>
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
        <label className={s.fieldLabel}>{t("genderLabel")}</label>
        <select
          name="gender"
          value={form.gender}
          onChange={handleChange}
          className={s.select}
        >
          <option value="MALE">{t("genderMale")}</option>
          <option value="FEMALE">{t("genderFemale")}</option>
          <option value="OTHER">{t("genderOther")}</option>
        </select>
      </div>

      <div className={s.passwordWrap}>
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
          className={s.passwordToggle}
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

      <p className={s.switchText}>
        {t("hasAccount")}{" "}
        <Link href="/login" className={s.switchLink}>
          {t("loginLink")}
        </Link>
      </p>
    </form>
  );
}

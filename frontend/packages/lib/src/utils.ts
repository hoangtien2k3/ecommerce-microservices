import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatPrice(price: number, currency = "VND"): string {
  if (currency === "VND") {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  }
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(price);
}

// order-service 의 LocalDateTime 형식 (AppConstant.LOCAL_DATE_TIME_FORMAT): dd-MM-yyyy__HH:mm:ss:SSSSSS
const BACKEND_DATE_TIME = /^(\d{2})-(\d{2})-(\d{4})__(\d{2}):(\d{2}):(\d{2}):(\d{1,6})$/;

/** 백엔드 형식이면 직접 풀고, 아니면 ISO 문자열로 보고 Date 로 만든다. */
export function parseDateTime(date: string): Date {
  const m = BACKEND_DATE_TIME.exec(date);
  if (!m) return new Date(date);
  const [, dd, MM, yyyy, HH, mm, ss, frac] = m;
  return new Date(+yyyy, +MM - 1, +dd, +HH, +mm, +ss, Math.floor(+frac.padEnd(6, "0") / 1000));
}

/** Date 를 order-service 가 받는 형식(dd-MM-yyyy__HH:mm:ss:SSSSSS, 브라우저 현지 시각)으로 바꾼다. */
export function toBackendDateTime(date: Date): string {
  const p = (n: number, len = 2) => String(n).padStart(len, "0");
  return (
    `${p(date.getDate())}-${p(date.getMonth() + 1)}-${date.getFullYear()}__` +
    `${p(date.getHours())}:${p(date.getMinutes())}:${p(date.getSeconds())}:${p(date.getMilliseconds() * 1000, 6)}`
  );
}

export function formatDate(date: string): string {
  const parsed = parseDateTime(date);
  if (Number.isNaN(parsed.getTime())) return date; // 모르는 형식이면 그대로 보여 준다 (Intl 은 Invalid Date 에서 예외를 던짐)
  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(parsed);
}

export function truncateText(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text;
  return text.slice(0, maxLength) + "...";
}

export function getInitials(name: string): string {
  return name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);
}

// Splits a duration in ms into zero-padded HH, MM, SS parts for countdowns.
export function countdownParts(ms: number): [string, string, string] {
  const total = Math.max(0, ms);
  const pad = (n: number) => n.toString().padStart(2, "0");
  return [
    pad(Math.floor(total / 3_600_000)),
    pad(Math.floor((total % 3_600_000) / 60_000)),
    pad(Math.floor((total % 60_000) / 1000)),
  ];
}

export function formatCountdown(ms: number): string {
  return countdownParts(ms).join(":");
}

// Short local time, e.g. "14:30" today or "16/10 14:30" on another day.
export function formatShortTime(iso: string, now: number): string {
  const date = new Date(iso);
  const time = date.toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" });
  if (date.toDateString() === new Date(now).toDateString()) return time;
  const day = date.toLocaleDateString("vi-VN", { day: "2-digit", month: "2-digit" });
  return `${day} ${time}`;
}

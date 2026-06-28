import { getTranslations } from "next-intl/server";
import { Link } from "@/i18n/navigation";
import { quickDeals } from "@/data/homeMock";
import { homeStyles as s } from "./home.styles";

export default async function QuickDeals() {
  const t = await getTranslations("Home");

  return (
    <div className={s.quickGrid}>
      {quickDeals.map((deal) => (
        <Link key={deal.id} href={deal.href} className={s.quickCard}>
          <span className={s.quickIcon}>{deal.emoji}</span>
          <span className={s.quickLabel}>{t(deal.key)}</span>
        </Link>
      ))}
    </div>
  );
}

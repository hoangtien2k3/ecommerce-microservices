import { getTranslations } from "next-intl/server";
import { Link } from "@/i18n/navigation";
import { categoryIcons } from "@/data/homeMock";
import { homeStyles as s } from "./home.styles";

export default async function CategoryBar() {
  const t = await getTranslations("Category");

  return (
    <div className={s.section}>
      <div className={s.catGrid}>
        {categoryIcons.map((cat) => (
          <Link key={cat.id} href={cat.href} className={s.catItem}>
            <div className={s.catIcon}>{cat.emoji}</div>
            <span className={s.catLabel}>{t(cat.key)}</span>
          </Link>
        ))}
      </div>
    </div>
  );
}

"use client";

import { useState, useEffect, useCallback } from "react";
import { useTranslations } from "next-intl";
import { ChevronLeft, ChevronRight, ArrowRight } from "lucide-react";
import { cn } from "@ecommerce/lib/utils";
import { Link } from "@/i18n/navigation";
import { heroSlides, heroSideBanners } from "@/data/homeMock";
import { homeStyles as s } from "./home.styles";

export default function HeroSlider() {
  const t = useTranslations("Home");
  const [active, setActive] = useState(0);
  const count = heroSlides.length;

  const go = useCallback((i: number) => setActive((i + count) % count), [count]);

  useEffect(() => {
    const timer = setInterval(() => setActive((a) => (a + 1) % count), 4500);
    return () => clearInterval(timer);
  }, [count]);

  return (
    <div className={s.heroGrid}>
      {/* Main slider */}
      <div className={s.heroMain}>
        {heroSlides.map((slide, i) => (
          <Link
            key={slide.id}
            href={slide.href}
            className={cn(
              "absolute inset-0 flex flex-col justify-center px-8 md:px-12 text-white bg-gradient-to-r transition-opacity duration-700",
              slide.gradient,
              i === active ? "opacity-100" : "opacity-0 pointer-events-none"
            )}
          >
            <span className={s.heroBadge}>{t("heroBadge")}</span>
            <h2 className={s.heroTitle}>{t(`${slide.key}Title`)}</h2>
            <p className={s.heroSub}>{t(`${slide.key}Sub`)}</p>
            <span className={s.heroCta}>
              {t("heroCta")} <ArrowRight className="h-4 w-4" />
            </span>
          </Link>
        ))}

        {/* Arrows */}
        <button
          onClick={(e) => { e.preventDefault(); go(active - 1); }}
          className={cn(s.heroArrow, "left-3")}
          aria-label="Previous"
        >
          <ChevronLeft className="h-5 w-5" />
        </button>
        <button
          onClick={(e) => { e.preventDefault(); go(active + 1); }}
          className={cn(s.heroArrow, "right-3")}
          aria-label="Next"
        >
          <ChevronRight className="h-5 w-5" />
        </button>

        {/* Dots */}
        <div className={s.heroDots}>
          {heroSlides.map((_, i) => (
            <button
              key={i}
              onClick={(e) => { e.preventDefault(); go(i); }}
              className={cn(
                "h-2 rounded-full transition-all",
                i === active ? "w-6 bg-white" : "w-2 bg-white/50 hover:bg-white/80"
              )}
              aria-label={`Slide ${i + 1}`}
            />
          ))}
        </div>
      </div>

      {/* Side banners */}
      <div className={s.heroSide}>
        {heroSideBanners.map((b) => (
          <Link key={b.id} href={b.href} className={cn(s.heroSideCard, b.gradient)}>
            <p className={s.heroSideTitle}>{t(`${b.key}Title`)}</p>
            <p className={s.heroSideSub}>{t(`${b.key}Sub`)}</p>
          </Link>
        ))}
      </div>
    </div>
  );
}

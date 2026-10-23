import { getTranslations } from "next-intl/server";
import CouponEventList from "@/components/coupon/CouponEventList";

export async function generateMetadata() {
  const t = await getTranslations("Coupons");
  return { title: t("metaTitle") };
}

export default function CouponsPage() {
  return <CouponEventList />;
}

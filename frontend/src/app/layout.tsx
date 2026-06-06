import type { Metadata } from "next";
import { Geist } from "next/font/google";
import "./globals.css";
import Providers from "@/components/Providers";

const geist = Geist({ subsets: ["latin"], variable: "--font-geist" });

export const metadata: Metadata = {
  title: {
    default: "EzBuy - Mua sắm thông minh",
    template: "%s | EzBuy",
  },
  description: "Nền tảng thương mại điện tử hàng đầu Việt Nam",
  keywords: ["mua sắm online", "thương mại điện tử", "ecommerce"],
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="vi" className={geist.variable}>
      <body className="min-h-screen bg-gray-50 antialiased">
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}

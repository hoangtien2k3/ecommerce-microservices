import type { Metadata } from "next";
import { Geist } from "next/font/google";
import "./globals.css";
import Providers from "@/components/Providers";

const geist = Geist({ subsets: ["latin"], variable: "--font-geist" });

export const metadata: Metadata = {
  title: {
    default: "EzBuy Admin",
    template: "%s | EzBuy Admin",
  },
  description: "Bảng điều khiển quản trị EzBuy",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="vi" className={geist.variable}>
      <body className="min-h-screen bg-gray-100 antialiased">
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}

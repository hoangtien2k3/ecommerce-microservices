import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone",
  images: {
    remotePatterns: [
      { protocol: "https", hostname: "**" },
      { protocol: "http", hostname: "**" },
    ],
  },
  async rewrites() {
    const apiBase = process.env.INTERNAL_API_URL || "http://api-gateway:8888";
    return [
      { source: "/api/:path*",        destination: `${apiBase}/api/:path*` },
      { source: "/storefront/:path*", destination: `${apiBase}/storefront/:path*` },
    ];
  },
};

export default nextConfig;

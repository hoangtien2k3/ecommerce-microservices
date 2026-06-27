import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone",
  images: {
    remotePatterns: [
      { protocol: "https", hostname: "**" },
      { protocol: "http", hostname: "**" },
    ],
  },
  // No API rewrites: the browser calls Apache APISIX directly (NEXT_PUBLIC_API_URL).
  // Next.js only serves the UI — it is no longer a proxy for API traffic.
};

export default nextConfig;

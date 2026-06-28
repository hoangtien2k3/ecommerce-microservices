import path from "node:path";
import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone",
  // Monorepo: trace files from the repo root so the standalone bundle includes
  // workspace packages (@ecommerce/lib, @ecommerce/ui) that live outside this app.
  outputFileTracingRoot: path.join(__dirname, "../../"),
  // Transpile the workspace packages — they ship raw TypeScript source (no build step).
  transpilePackages: ["@ecommerce/lib", "@ecommerce/ui"],
  images: {
    remotePatterns: [
      { protocol: "https", hostname: "**" },
      { protocol: "http", hostname: "**" },
    ],
  },
};

export default nextConfig;

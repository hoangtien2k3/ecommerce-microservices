# Monorepo Dockerfile — build context MUST be the frontend/ root:
#   docker build -f apps/admin/Dockerfile -t ecommerce-admin .
FROM node:24-alpine AS base
RUN npm install -g pnpm@10.10.0
WORKDIR /app

# 1. Prune the monorepo down to just what @ecommerce/admin needs.
FROM base AS pruner
COPY . .
RUN pnpm dlx turbo@^2.3.3 prune @ecommerce/admin --docker

# 2. Install deps (cached on the pruned lockfile) then build.
FROM base AS builder
COPY --from=pruner /app/out/json/ .
RUN pnpm install --frozen-lockfile
COPY --from=pruner /app/out/full/ .
ARG NEXT_PUBLIC_API_URL=
ENV NEXT_PUBLIC_API_URL=${NEXT_PUBLIC_API_URL}
RUN pnpm --filter @ecommerce/admin build

# 3. Minimal runtime from the Next.js standalone output.
FROM base AS runner
ENV NODE_ENV=production
RUN addgroup --system --gid 1001 nodejs && \
    adduser --system --uid 1001 nextjs

COPY --from=builder --chown=nextjs:nodejs /app/apps/admin/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/apps/admin/.next/static ./apps/admin/.next/static
COPY --from=builder --chown=nextjs:nodejs /app/apps/admin/public ./apps/admin/public

USER nextjs
EXPOSE 3001
ENV PORT=3001
ENV HOSTNAME="0.0.0.0"

CMD ["node", "apps/admin/server.js"]

# EzBuy Frontend — Monorepo

Turborepo + pnpm workspaces. Two Next.js apps share UI and data-access code
through internal packages (shipped as raw TypeScript, transpiled by Next via
`transpilePackages` — no build step for packages).

```
frontend/
├── apps/
│   ├── web/      @ecommerce/web    — storefront + auth (port 3000)
│   └── admin/    @ecommerce/admin  — admin dashboard, mounted at /admin/* (port 3001)
└── packages/
    ├── lib/      @ecommerce/lib    — api client, types, utils, zustand stores
    ├── ui/       @ecommerce/ui     — shared UI primitives (Button, Input, Badge)
    └── config/   @ecommerce/config — shared tsconfig presets
```

## Dependency graph

```
apps/web ──┬─► @ecommerce/ui ──► @ecommerce/lib
apps/admin ┘─► @ecommerce/lib
```

## Getting started

```bash
pnpm install          # install all workspaces
pnpm dev              # run every app (turbo)
pnpm dev:web          # storefront only      → http://localhost:3000
pnpm dev:admin        # admin only           → http://localhost:3001/admin/dashboard
pnpm build            # build everything
pnpm lint             # lint everything
pnpm typecheck        # type-check everything
```

## Import conventions

| What | Import from |
| --- | --- |
| API client (`authApi`, `productApi`, …) | `@ecommerce/lib/api` |
| Domain types (`Product`, `ApiResponse`, …) | `@ecommerce/lib/types` |
| Helpers (`cn`, `formatPrice`, …) | `@ecommerce/lib/utils` |
| Stores (`useAuthStore`, `useCartStore`) | `@ecommerce/lib/store` |
| UI primitives (`Button`, `Input`, `Badge`) | `@ecommerce/ui` |
| App-local code | `@/...` (resolves to that app's `src/`) |

## Docker

Each app builds from the **frontend root** as context (uses `turbo prune`):

```bash
docker build -f apps/web/Dockerfile   -t ecommerce-web   .
docker build -f apps/admin/Dockerfile -t ecommerce-admin .
```

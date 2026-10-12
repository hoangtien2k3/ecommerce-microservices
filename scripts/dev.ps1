<#
.SYNOPSIS
    Windows task runner for ecommerce-microservices — mirrors the root Makefile.

.DESCRIPTION
    Use this when GNU make is not installed. Task names are identical to the
    Make targets:

        make test                           ==  .\scripts\dev.ps1 test
        make go-run SVC=product-service     ==  .\scripts\dev.ps1 go-run product-service
        make logs SVC=product-service       ==  .\scripts\dev.ps1 logs product-service

.EXAMPLE
    .\scripts\dev.ps1 help
    .\scripts\dev.ps1 build
    .\scripts\dev.ps1 test
    .\scripts\dev.ps1 go-run product-service
    .\scripts\dev.ps1 web-dev
#>
[CmdletBinding()]
param(
    [Parameter(Position = 0)]
    [string]$Task = "help",

    [Parameter(Position = 1)]
    [string]$Service
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repoRoot

$goModules = @(
    "services/shared",
    "services/auth-service",
    "services/product-service",
    "services/order-service",
    "services/payment-service",
    "services/inventory-service",
    "services/shipping-service",
    "services/search-service"
)

$bunServices = @(
    "services/promotion-service",
    "services/rating-service",
    "services/media-service",
    "services/notification-service"
)

$infraServices = @(
    "postgres",
    "redis",
    "kafka",
    "opensearch",
    "rustfs",
    "keycloak",
    "apisix"
)

function Fail([string]$Message) {
    Write-Host "x $Message" -ForegroundColor Red
    exit 1
}

function Assert-LastExit([string]$What) {
    if ($LASTEXITCODE -ne 0) {
        Fail "$What failed (exit $LASTEXITCODE)"
    }
}

function Get-GoPatterns {
    $patterns = @()
    foreach ($module in $goModules) {
        $patterns += "./$module/..."
    }
    return $patterns
}

function Show-Help {
    Write-Host "ecommerce-microservices - available tasks"
    Write-Host ""
    Write-Host "  Go workspace"
    Write-Host "    build                  Build all Go modules"
    Write-Host "    test                   Run Go + Bun test suites"
    Write-Host "    check                  gofmt check + go vet + all tests"
    Write-Host "    go-vet                 Run go vet on all modules"
    Write-Host "    go-fmt                 Format all Go code (gofmt -w)"
    Write-Host "    go-tidy                go mod tidy in every module"
    Write-Host "    go-run <module>        Run one Go service locally"
    Write-Host ""
    Write-Host "  Bun services"
    Write-Host "    bun-install            Install dependencies (4 services)"
    Write-Host "    bun-test               Run Bun tests (4 services)"
    Write-Host "    bun-dev <service>      Run one Bun service in watch mode"
    Write-Host ""
    Write-Host "  Frontend (Bun workspace - SvelteKit 5)"
    Write-Host "    web-install            Install frontend workspace dependencies (Bun)"
    Write-Host "    web-dev                Start both SvelteKit apps (web :3000, admin :3001)"
    Write-Host "    web-build              Production build of both apps"
    Write-Host "    web-check              Type-check both apps and packages"
    Write-Host "    web-lint               Prettier + ESLint across the workspace"
    Write-Host "    web-test               Run frontend unit tests (bun test)"
    Write-Host ""
    Write-Host "  Docker Compose"
    Write-Host "    up                     Build & start the full stack"
    Write-Host "    infra-up               Start only infra containers"
    Write-Host "    down                   Stop & remove containers"
    Write-Host "    ps                     Show container status"
    Write-Host "    logs [service]         Follow logs (all or one service)"
    Write-Host "    restart <service>      Restart one container"
    Write-Host "    images                 Build all Docker images without starting"
    Write-Host ""
    Write-Host "  Kubernetes (k3d - legacy path)"
    Write-Host "    cluster-up             One-shot k3d deploy (k3d-setup.ps1, Docker or Podman)"
    Write-Host "    cluster-status         kubectl get pods -n ecommerce -o wide"
    Write-Host "    cluster-down           Delete the k3d cluster"
    Write-Host ""
    Write-Host "  Windows examples"
    Write-Host "    .\scripts\dev.ps1 test"
    Write-Host "    .\scripts\dev.ps1 go-run product-service"
}

function Invoke-GoBuild {
    $patterns = Get-GoPatterns
    go build $patterns
    Assert-LastExit "go build"
}

function Invoke-GoTest {
    $patterns = Get-GoPatterns
    go test $patterns
    Assert-LastExit "go test"
}

function Invoke-GoVet {
    $patterns = Get-GoPatterns
    go vet $patterns
    Assert-LastExit "go vet"
}

function Invoke-GoFmt {
    gofmt -w $goModules
    Assert-LastExit "gofmt -w"
}

function Invoke-GoFmtCheck {
    $unformatted = gofmt -l $goModules
    if ($LASTEXITCODE -ne 0) {
        Fail "gofmt -l"
    }
    if ($unformatted) {
        Write-Host "Unformatted Go files:" -ForegroundColor Yellow
        $unformatted | ForEach-Object { Write-Host "  $_" }
        exit 1
    }
    Write-Host "All Go files are gofmt-clean."
}

function Invoke-GoTidy {
    foreach ($module in $goModules) {
        Write-Host "==> go mod tidy: $module"
        Push-Location $module
        go mod tidy
        $code = $LASTEXITCODE
        Pop-Location
        if ($code -ne 0) {
            Fail "go mod tidy failed in $module"
        }
    }
}

function Invoke-BunInstall {
    foreach ($svc in $bunServices) {
        Write-Host "==> bun install: $svc"
        Push-Location $svc
        bun install
        $code = $LASTEXITCODE
        Pop-Location
        if ($code -ne 0) {
            Fail "bun install failed in $svc"
        }
    }
}

function Invoke-BunTest {
    Invoke-BunInstall
    $failed = @()
    foreach ($svc in $bunServices) {
        Write-Host "==> bun test: $svc"
        Push-Location $svc
        bun test
        $code = $LASTEXITCODE
        Pop-Location
        if ($code -ne 0) {
            $failed += $svc
        }
    }
    if ($failed.Count -gt 0) {
        Fail "bun test failed: $($failed -join ', ')"
    }
}

function Invoke-GoRun {
    if (-not $Service) {
        Fail "Usage: .\scripts\dev.ps1 go-run <module>   (e.g. product-service)"
    }
    Push-Location "services/$Service"
    go run .
    $code = $LASTEXITCODE
    Pop-Location
    exit $code
}

function Invoke-BunDev {
    if (-not $Service) {
        Fail "Usage: .\scripts\dev.ps1 bun-dev <service>   (e.g. rating-service)"
    }
    Push-Location "services/$Service"
    bun install
    if ($LASTEXITCODE -eq 0) {
        bun run dev
    }
    $code = $LASTEXITCODE
    Pop-Location
    exit $code
}

switch ($Task.ToLowerInvariant()) {
    "help"           { Show-Help }
    "build"          { Invoke-GoBuild }
    "go-build"       { Invoke-GoBuild }
    "test"           { Invoke-GoTest; Invoke-BunTest }
    "check"          { Invoke-GoFmtCheck; Invoke-GoVet; Invoke-GoTest; Invoke-BunTest }
    "go-test"        { Invoke-GoTest }
    "go-vet"         { Invoke-GoVet }
    "go-fmt"         { Invoke-GoFmt }
    "go-fmt-check"   { Invoke-GoFmtCheck }
    "go-tidy"        { Invoke-GoTidy }
    "go-run"         { Invoke-GoRun }
    "bun-install"    { Invoke-BunInstall }
    "bun-test"       { Invoke-BunTest }
    "bun-dev"        { Invoke-BunDev }
    "web-install"    { Push-Location frontend; bun install;      $code = $LASTEXITCODE; Pop-Location; exit $code }
    "web-dev"        { Push-Location frontend; bun run dev;      $code = $LASTEXITCODE; Pop-Location; exit $code }
    "web-build"      { Push-Location frontend; bun run build;    $code = $LASTEXITCODE; Pop-Location; exit $code }
    "web-check"      { Push-Location frontend; bun run check;    $code = $LASTEXITCODE; Pop-Location; exit $code }
    "web-lint"       { Push-Location frontend; bun run lint;     $code = $LASTEXITCODE; Pop-Location; exit $code }
    "web-test"       { Push-Location frontend; bun test;         $code = $LASTEXITCODE; Pop-Location; exit $code }
    "up"             { docker compose up -d --build; Assert-LastExit "docker compose up" }
    "infra-up"       { docker compose up -d $infraServices; Assert-LastExit "docker compose up (infra)" }
    "down"           { docker compose down; Assert-LastExit "docker compose down" }
    "ps"             { docker compose ps; Assert-LastExit "docker compose ps" }
    "logs"           {
        if ($Service) { docker compose logs -f $Service } else { docker compose logs -f }
    }
    "restart"        {
        if (-not $Service) { Fail "Usage: .\scripts\dev.ps1 restart <service>" }
        docker compose restart $Service
        Assert-LastExit "docker compose restart"
    }
    "images"         { docker compose build; Assert-LastExit "docker compose build" }
    "cluster-up"     {
        $setupScript = Join-Path $repoRoot "scripts\k3d-setup.ps1"
        if (-not (Test-Path $setupScript)) { Fail "scripts\k3d-setup.ps1 not found" }
        if (Get-Command pwsh -ErrorAction SilentlyContinue) {
            pwsh -NoProfile -ExecutionPolicy Bypass -File $setupScript
        } else {
            powershell -NoProfile -ExecutionPolicy Bypass -File $setupScript
        }
        Assert-LastExit "k3d-setup.ps1"
    }
    "cluster-status" { kubectl get pods -n ecommerce -o wide; Assert-LastExit "kubectl get pods" }
    "cluster-down"   { k3d cluster delete ecommerce; Assert-LastExit "k3d cluster delete" }
    default {
        Write-Host "Unknown task: $Task" -ForegroundColor Yellow
        Write-Host ""
        Show-Help
        exit 1
    }
}

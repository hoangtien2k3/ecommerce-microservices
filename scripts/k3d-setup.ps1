#Requires -Version 5.1
<#
.SYNOPSIS
    k3d-setup.ps1 — One-shot local Kubernetes deployment on Windows (Docker or Podman).

.DESCRIPTION
    Sets up the full Ecommerce Microservices stack on Windows:

      1. Detects the Windows environment & CPU architecture
      2. Detects the container runtime — Docker Desktop or Podman (auto-detected;
         force one with -Runtime docker|podman). Podman machines are configured
         rootful with the API socket exposed for k3d.
      3. Installs k3d automatically if missing
      4. Installs kubectl automatically if missing
      5. Creates/starts the k3d cluster (k3d-config.yaml)
      6. Installs NGINX Ingress Controller
      7. Applies Namespace, Secrets (from .env or defaults) & ConfigMaps
      8. Deploys Infrastructure (PostgreSQL, Redis, Kafka, OpenSearch, RustFS, Keycloak)
      9. Deploys APISIX API Gateway, 11 backend microservices & Frontend/Admin webapps
     10. Updates the Windows hosts file (or prints the elevated command)

    For Linux / macOS / WSL / Git Bash use k3d-setup.sh instead — it speaks both
    runtimes too.

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File .\scripts\k3d-setup.ps1
    powershell -ExecutionPolicy Bypass -File .\scripts\k3d-setup.ps1 -Runtime podman
#>

[CmdletBinding()]
param(
    [ValidateSet("auto", "docker", "podman")]
    [string]$Runtime = "auto",

    [string]$ClusterName = "ecommerce",
    [string]$Namespace   = "ecommerce",
    [string]$K3dVersion  = "v5.8.3"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Continue"

# Ensure UTF-8 console output so box-drawing and status symbols render properly
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding           = [System.Text.Encoding]::UTF8

# Always run from the repository root (this script lives in scripts/)
if ($PSScriptRoot) {
    Set-Location -Path (Split-Path -Parent $PSScriptRoot)
}

# =============================================================================
# UI & Logging Helpers
# =============================================================================
$script:TotalSteps       = 10
$script:CurrentStep      = 0
$script:StepStart        = [System.Diagnostics.Stopwatch]::StartNew()
$script:GlobalTimer      = [System.Diagnostics.Stopwatch]::StartNew()
$script:ContainerRuntime = "auto"

function Write-Hr {
    Write-Host "──────────────────────────────────────────────────────────────" -ForegroundColor DarkGray
}

function Write-Info([string]$Message) {
    Write-Host "  →  " -NoNewline -ForegroundColor Cyan
    Write-Host $Message
}

function Write-Success([string]$Message) {
    Write-Host "  ✓  " -NoNewline -ForegroundColor Green
    Write-Host $Message
}

function Write-Warn([string]$Message) {
    Write-Host "  !  " -NoNewline -ForegroundColor Yellow
    Write-Host $Message
}

function Stop-WithError([string]$Message) {
    Write-Host ""
    Write-Host "  ✗  ERROR: " -NoNewline -ForegroundColor Red
    Write-Host $Message
    Write-Host ""
    exit 1
}

function Start-Step([string]$Title) {
    $script:CurrentStep++
    $script:StepStart = [System.Diagnostics.Stopwatch]::StartNew()
    Write-Host ""
    Write-Host ("  [{0}/{1}]  {2}" -f $script:CurrentStep, $script:TotalSteps, $Title) -ForegroundColor White
}

function Complete-Step {
    $elapsed = [int][Math]::Floor($script:StepStart.Elapsed.TotalSeconds)
    Write-Host ("  └─ done in {0}s" -f $elapsed) -ForegroundColor DarkGray
}

function Format-Elapsed([int]$TotalSeconds) {
    if ($TotalSeconds -ge 60) {
        return ("{0}m {1}s" -f [int][Math]::Floor($TotalSeconds / 60), ($TotalSeconds % 60))
    }
    return ("{0}s" -f $TotalSeconds)
}

# =============================================================================
# Configuration Constants
# =============================================================================
$HostsEntry = "127.0.0.1 ecommerce.local admin.ecommerce.local api.ecommerce.local keycloak.ecommerce.local rustfs.ecommerce.local"
$LocalBin   = Join-Path $HOME ".local\bin"

if (-not (Test-Path $LocalBin)) {
    New-Item -ItemType Directory -Path $LocalBin -Force | Out-Null
}
if (($env:PATH -split ';') -notcontains $LocalBin) {
    $env:PATH = "$LocalBin;$env:PATH"
}

# =============================================================================
# Helper Functions
# =============================================================================
function Get-SystemArch {
    $rawArch = $env:PROCESSOR_ARCHITECTURE
    if ($env:PROCESSOR_ARCHITEW6432) {
        $rawArch = $env:PROCESSOR_ARCHITEW6432
    }
    switch -Regex ($rawArch) {
        "AMD64|x86_64" { return "amd64" }
        "ARM64|aarch64" { return "arm64" }
        default { Stop-WithError "Unsupported CPU architecture: $rawArch" }
    }
}

function Install-Binary([string]$Name, [string]$Url) {
    $dest = Join-Path $LocalBin "$Name.exe"
    Write-Info "Downloading $Name.exe to $LocalBin..."
    try {
        $ProgressPreference = 'SilentlyContinue'
        Invoke-WebRequest -Uri $Url -OutFile $dest -UseBasicParsing -ErrorAction Stop
    } catch {
        Stop-WithError "Failed to download $Name from $Url`n     $($_.Exception.Message)"
    }

    # Persist ~/.local/bin in User PATH if not already present
    $userPath = [Environment]::GetEnvironmentVariable("Path", "User")
    if ($userPath -and (($userPath -split ';') -notcontains $LocalBin)) {
        [Environment]::SetEnvironmentVariable("Path", "$userPath;$LocalBin", "User")
    }
    Write-Success "$Name installed → $dest"
}

function Install-K3d([string]$Arch) {
    $url = "https://github.com/k3d-io/k3d/releases/download/$K3dVersion/k3d-windows-$Arch.exe"
    Install-Binary -Name "k3d" -Url $url
}

function Install-Kubectl([string]$Arch) {
    try {
        $ProgressPreference = 'SilentlyContinue'
        $ver = (Invoke-RestMethod -Uri "https://dl.k8s.io/release/stable.txt" -UseBasicParsing -ErrorAction Stop).Trim()
    } catch {
        $ver = "v1.31.0"
    }
    $url = "https://dl.k8s.io/release/$ver/bin/windows/$Arch/kubectl.exe"
    Install-Binary -Name "kubectl" -Url $url
}

function Invoke-KubectlPipeApply([string]$CreateArgs) {
    # Use cmd.exe byte pipe so PowerShell 5.1 does not re-encode UTF-8 payloads (e.g. realm JSON)
    $cmdLine = "kubectl $CreateArgs --dry-run=client -o yaml | kubectl apply -f - >nul"
    cmd.exe /c $cmdLine
    if ($LASTEXITCODE -ne 0) {
        Stop-WithError "Failed to apply resource via: kubectl $CreateArgs"
    }
}

function Read-DotEnv([string]$FilePath) {
    $map = @{}
    if (-not (Test-Path $FilePath)) {
        return $map
    }
    foreach ($line in Get-Content -Path $FilePath -Encoding UTF8) {
        $trimmed = $line.Trim()
        if ([string]::IsNullOrWhiteSpace($trimmed) -or $trimmed.StartsWith("#")) {
            continue
        }
        $idx = $trimmed.IndexOf('=')
        if ($idx -lt 1) {
            continue
        }
        $key = $trimmed.Substring(0, $idx).Trim()
        $val = $trimmed.Substring($idx + 1).Trim()
        if (($val.StartsWith('"') -and $val.EndsWith('"')) -or ($val.StartsWith("'") -and $val.EndsWith("'"))) {
            if ($val.Length -ge 2) {
                $val = $val.Substring(1, $val.Length - 2)
            }
        }
        $map[$key] = $val
    }
    return $map
}

function Get-EnvOrDefault([hashtable]$EnvMap, [string]$Key, [string]$DefaultValue) {
    if ($EnvMap.ContainsKey($Key) -and -not [string]::IsNullOrEmpty($EnvMap[$Key])) {
        return $EnvMap[$Key]
    }
    return $DefaultValue
}

function Update-WindowsHosts([string]$Entry) {
    $hostsPath = "$env:SystemRoot\System32\drivers\etc\hosts"
    $isAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole(
        [Security.Principal.WindowsBuiltInRole]::Administrator
    )

    $currentContent = ""
    if (Test-Path $hostsPath) {
        $currentContent = Get-Content -Path $hostsPath -Raw -ErrorAction SilentlyContinue
    }

    if ($currentContent -and $currentContent.Contains($Entry)) {
        Write-Warn "Hosts file already up to date — skipping"
        return
    }

    if ($isAdmin) {
        try {
            $lines = @()
            if (Test-Path $hostsPath) {
                $lines = @(Get-Content -Path $hostsPath | Where-Object { $_ -notmatch '\becommerce\.local\b' })
            }
            $lines += $Entry
            Set-Content -Path $hostsPath -Value $lines -Encoding ASCII -Force
            Write-Success "Windows hosts file updated ($hostsPath)"
        } catch {
            Write-Warn "Could not write to hosts file automatically: $($_.Exception.Message)"
            Write-Warn "Run this in an Administrator PowerShell prompt:"
            Write-Host "`n     Add-Content -Path '$hostsPath' -Value '$Entry'`n"
        }
    } else {
        if ($currentContent -and ($currentContent -match '\becommerce\.local\b')) {
            Write-Warn "Hosts file contains ecommerce.local (run as Administrator if you need to refresh hostnames)"
        } else {
            Write-Warn "Not running as Administrator — run this in an elevated PowerShell prompt:"
            Write-Host "`n     Add-Content -Path '$hostsPath' -Value '$Entry'`n" -ForegroundColor Yellow
        }
    }
}

# =============================================================================
# Container Runtime — Docker preferred, Podman supported
# =============================================================================
function Test-DockerReady {
    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) { return $false }
    docker info *> $null
    return ($LASTEXITCODE -eq 0)
}

function Initialize-DockerRuntime {
    $version = (docker version --format '{{.Server.Version}}' 2>$null)
    if (-not $version) { $version = "unknown" }
    Write-Success "Docker is running  (version: $version)"
}

function Initialize-PodmanRuntime {
    if (-not (Get-Command podman -ErrorAction SilentlyContinue)) {
        Stop-WithError "Podman not found.`n     Install Podman Desktop (https://podman-desktop.io) or run: winget install RedHat.Podman"
    }

    # List existing Podman machines
    $machinesJson = podman machine list --format json 2>$null
    $machines = @()
    if ($machinesJson) {
        try {
            $machines = @($machinesJson | ConvertFrom-Json)
        } catch {
            $machines = @()
        }
    }

    if ($machines.Count -eq 0) {
        Write-Warn "No Podman machine found — initializing a rootful Podman machine..."
        podman machine init --rootful
        if ($LASTEXITCODE -ne 0) {
            Stop-WithError "Failed to initialize Podman machine. Try running: podman machine init --rootful"
        }
        $machinesJson = podman machine list --format json 2>$null
        $machines = @($machinesJson | ConvertFrom-Json)
    }

    # Pick default machine (or first available)
    $targetMachine = $machines | Where-Object { $_.Default -eq $true } | Select-Object -First 1
    if (-not $targetMachine) {
        $targetMachine = $machines | Select-Object -First 1
    }
    $machineName = ($targetMachine.Name -replace '\*$', '').Trim()

    # Inspect machine for Rootful status and State
    $inspectRaw = podman machine inspect $machineName 2>$null
    if (-not $inspectRaw) {
        Stop-WithError "Unable to inspect Podman machine '$machineName'."
    }
    $machineInfo = ($inspectRaw | ConvertFrom-Json)[0]

    if (-not $machineInfo.Rootful) {
        Write-Warn "Podman machine '$machineName' is rootless. k3d requires rootful mode — reconfiguring..."
        if ($machineInfo.State -eq "running") {
            Write-Info "Stopping Podman machine '$machineName'..."
            podman machine stop $machineName | Out-Null
        }
        Write-Info "Enabling rootful mode on '$machineName'..."
        podman machine set --rootful $machineName | Out-Null
        if ($LASTEXITCODE -ne 0) {
            Stop-WithError "Failed to set rootful mode on Podman machine '$machineName'.`n     Run manually: podman machine stop $machineName && podman machine set --rootful $machineName"
        }
        $inspectRaw  = podman machine inspect $machineName 2>$null
        $machineInfo = ($inspectRaw | ConvertFrom-Json)[0]
    }

    if ($machineInfo.State -ne "running") {
        Write-Info "Starting Podman machine '$machineName'..."
        podman machine start $machineName
        if ($LASTEXITCODE -ne 0) {
            Stop-WithError "Failed to start Podman machine '$machineName'."
        }
        $inspectRaw  = podman machine inspect $machineName 2>$null
        $machineInfo = ($inspectRaw | ConvertFrom-Json)[0]
    }

    # Determine Windows Named Pipe for DOCKER_HOST
    $pipePath = $null
    if ($machineInfo.ConnectionInfo -and $machineInfo.ConnectionInfo.PodmanPipe -and $machineInfo.ConnectionInfo.PodmanPipe.Path) {
        $pipePath = $machineInfo.ConnectionInfo.PodmanPipe.Path
    }
    if ([string]::IsNullOrWhiteSpace($pipePath)) {
        $pipePath = "\\.\pipe\$machineName"
    }

    $dockerHostUri = "npipe://" + ($pipePath -replace '\\', '/')
    $env:DOCKER_HOST = $dockerHostUri

    # Prepare inside-VM settings required by k3d and OpenSearch:
    #   - service_timeout=0 so Podman socket does not close mid-cluster creation
    #   - /var/run/docker.sock symlink to /run/podman/podman.sock
    #   - vm.max_map_count=262144 for OpenSearch 3
    Write-Info "Configuring Podman VM socket & kernel parameters..."
    podman machine ssh $machineName "sudo mkdir -p /etc/containers/containers.conf.d && printf '[engine]\nservice_timeout=0\n' | sudo tee /etc/containers/containers.conf.d/timeout.conf >/dev/null && sudo systemctl enable --now podman.socket >/dev/null 2>&1 || true; sudo ln -sf /run/podman/podman.sock /var/run/docker.sock 2>/dev/null || true; sudo sysctl -w vm.max_map_count=262144 >/dev/null 2>&1 || true" 2>$null

    $podmanVer = (podman version --format "{{.Server.Version}}" 2>$null)
    if (-not $podmanVer) {
        $podmanVer = (podman --version 2>$null)
    }
    Write-Success "Podman machine '$machineName' is running (rootful, version: $podmanVer)"
    Write-Success "DOCKER_HOST=$env:DOCKER_HOST"
}

# =============================================================================
# Banner
# =============================================================================
$Arch = Get-SystemArch

Write-Host ""
Write-Hr
Write-Host " Ecommerce Microservices · K8s Setup (Windows) " -ForegroundColor Cyan
Write-Host ("  Platform    windows · {0}" -f $Arch) -ForegroundColor DarkGray
Write-Host ("  Runtime     {0} (auto = Docker preferred, Podman fallback)" -f $Runtime) -ForegroundColor DarkGray
Write-Host ("  Cluster     {0}" -f $ClusterName) -ForegroundColor DarkGray
Write-Host ("  Started     {0}" -f (Get-Date -Format "HH:mm:ss")) -ForegroundColor DarkGray
Write-Hr

# =============================================================================
# 1. Detect environment
# =============================================================================
Start-Step "Detect environment"
Write-Success "OS=windows  ARCH=$Arch"
Complete-Step

# =============================================================================
# 2. Detect & prepare the container runtime
# =============================================================================
Start-Step "Check container runtime"

switch ($Runtime) {
    "docker" {
        if (-not (Test-DockerReady)) {
            Stop-WithError "Docker is installed but its daemon is not reachable.`n     Start Docker Desktop and try again, or use: -Runtime podman"
        }
        Initialize-DockerRuntime
        $script:ContainerRuntime = "docker"
    }
    "podman" {
        Initialize-PodmanRuntime
        $script:ContainerRuntime = "podman"
    }
    default {
        if (Test-DockerReady) {
            Initialize-DockerRuntime
            $script:ContainerRuntime = "docker"
        } elseif (Get-Command podman -ErrorAction SilentlyContinue) {
            Initialize-PodmanRuntime
            $script:ContainerRuntime = "podman"
        } else {
            Stop-WithError "No container runtime found.`n     Install Docker Desktop (https://www.docker.com/get-started) or Podman Desktop (https://podman-desktop.io)."
        }
    }
}

Write-Success "Container runtime: $script:ContainerRuntime"
Complete-Step

# =============================================================================
# 3. Check k3d
# =============================================================================
Start-Step "Check k3d"
if (Get-Command k3d -ErrorAction SilentlyContinue) {
    $k3dVerLine = (k3d version 2>$null | Select-Object -First 1)
    Write-Success "$k3dVerLine"
} else {
    Write-Warn "k3d not found — installing automatically..."
    Install-K3d -Arch $Arch
}
Complete-Step

# =============================================================================
# 4. Check kubectl
# =============================================================================
Start-Step "Check kubectl"
if (Get-Command kubectl -ErrorAction SilentlyContinue) {
    $kubectlVerLine = (kubectl version --client 2>$null | Select-Object -First 1)
    Write-Success "$kubectlVerLine"
} else {
    Write-Warn "kubectl not found — installing automatically..."
    Install-Kubectl -Arch $Arch
}
Complete-Step

# =============================================================================
# 5. Set up k3d cluster
# =============================================================================
Start-Step "Set up k3d cluster (via $script:ContainerRuntime)"

$clusterExists  = $false
$clusterRunning = $false

$clusterListJson = k3d cluster list -o json 2>$null
if ($LASTEXITCODE -eq 0 -and $clusterListJson) {
    try {
        $clusters = @($clusterListJson | ConvertFrom-Json)
        $existing = $clusters | Where-Object { $_.name -eq $ClusterName } | Select-Object -First 1
        if ($existing) {
            $clusterExists = $true
            if ($existing.serversRunning -ge 1) {
                $clusterRunning = $true
            }
        }
    } catch {
        $clusterExists = $false
    }
}

if ($clusterExists) {
    if ($clusterRunning) {
        Write-Warn "Cluster '$ClusterName' already running — skipping creation"
    } else {
        Write-Info "Cluster '$ClusterName' is stopped — restarting..."
        k3d cluster start $ClusterName | Out-Null
        if ($LASTEXITCODE -ne 0) {
            Stop-WithError "Failed to start existing k3d cluster '$ClusterName'."
        }
        Write-Success "Cluster '$ClusterName' started"
    }
} else {
    if ($script:ContainerRuntime -eq "podman") {
        # Ensure Podman has the default 'podman' bridge network ready before k3d creates the cluster network
        podman network inspect podman *> $null
        if ($LASTEXITCODE -ne 0) {
            podman network create podman *> $null
        }
    }

    Write-Info "Creating k3d cluster '$ClusterName' from k3d-config.yaml..."
    k3d cluster create --config scripts/k3d-config.yaml
    if ($LASTEXITCODE -ne 0) {
        Stop-WithError "k3d cluster creation failed.`n     Verify the $script:ContainerRuntime runtime is running (Docker Desktop, or a rootful Podman machine) and ports 80 / 9443 are free."
    }
    Write-Success "Cluster '$ClusterName' created"
}

k3d kubeconfig merge $ClusterName --kubeconfig-merge-default --kubeconfig-switch-context *> $null
kubectl config use-context "k3d-$ClusterName" *> $null

Write-Info "Waiting for Kubernetes API server..."
$apiReady = $false
for ($i = 0; $i -lt 60; $i++) {
    kubectl get nodes *> $null
    if ($LASTEXITCODE -eq 0) {
        $apiReady = $true
        break
    }
    Start-Sleep -Seconds 2
}
if (-not $apiReady) {
    Stop-WithError "Timed out waiting for Kubernetes API server."
}
Write-Success "API server is ready"
Complete-Step

# =============================================================================
# 6. NGINX Ingress Controller
# =============================================================================
Start-Step "Install NGINX Ingress Controller"

kubectl get ns ingress-nginx *> $null
if ($LASTEXITCODE -eq 0) {
    Write-Warn "ingress-nginx already installed — skipping manifest apply"
} else {
    Write-Info "Applying NGINX Ingress manifests..."
    kubectl apply --validate=false `
        -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml | Out-Null
    if ($LASTEXITCODE -ne 0) {
        Stop-WithError "Failed to apply NGINX Ingress Controller manifest."
    }
}

Write-Info "Waiting for NGINX Ingress Controller pod to become ready..."
$ingressReady = $false
for ($attempt = 1; $attempt -le 3; $attempt++) {
    # Give the controller pod a moment to be scheduled before waiting
    Start-Sleep -Seconds 3
    kubectl wait --namespace ingress-nginx `
        --for=condition=ready pod `
        --selector=app.kubernetes.io/component=controller `
        --timeout=120s *> $null
    if ($LASTEXITCODE -eq 0) {
        $ingressReady = $true
        break
    }
    Write-Warn "ingress-nginx: wait attempt $attempt/3 timed out — retrying..."
}
if (-not $ingressReady) {
    Stop-WithError "NGINX Ingress Controller failed to become ready.`n     Check: kubectl get pods -n ingress-nginx"
}
Write-Success "NGINX Ingress Controller is ready"
Complete-Step

# =============================================================================
# 7. Namespace + Secrets + ConfigMaps
# =============================================================================
Start-Step "Apply Namespace, Secrets & ConfigMaps"

kubectl apply -f deploy/k8s/namespace.yaml | Out-Null
Write-Success "Namespace '$Namespace'"

# Remove legacy workloads if re-running on an older cluster
kubectl delete deploy,svc,statefulset api-gateway minio zipkin `
    -n $Namespace --ignore-not-found *> $null

if (Test-Path ".env") {
    Write-Info "Loading secrets from .env..."
    $envMap = Read-DotEnv ".env"

    $pgUser      = Get-EnvOrDefault $envMap "POSTGRES_USER"          "admin"
    $pgPass      = Get-EnvOrDefault $envMap "POSTGRES_PASSWORD"      "admin"
    $kcAdmin     = Get-EnvOrDefault $envMap "KEYCLOAK_ADMIN"         "admin"
    $kcPass      = Get-EnvOrDefault $envMap "KEYCLOAK_ADMIN_PASSWORD" "admin"
    $redisPass   = Get-EnvOrDefault $envMap "REDIS_PASSWORD"         "admin"
    $storageKey  = Get-EnvOrDefault $envMap "STORAGE_ACCESS_KEY"     "admin"
    $storageSec  = Get-EnvOrDefault $envMap "STORAGE_SECRET_KEY"     "admin"
    $opensearchPass = Get-EnvOrDefault $envMap "OPENSEARCH_PASSWORD" (Get-EnvOrDefault $envMap "ELASTIC_PASSWORD" "admin")
    $mailUser       = Get-EnvOrDefault $envMap "MAIL_USERNAME"       ""
    $mailPass       = Get-EnvOrDefault $envMap "MAIL_PASSWORD"       ""

    Invoke-KubectlPipeApply "create secret generic postgres-secret -n $Namespace --from-literal=POSTGRES_USER=""$pgUser"" --from-literal=POSTGRES_PASSWORD=""$pgPass"""
    Invoke-KubectlPipeApply "create secret generic keycloak-secret -n $Namespace --from-literal=KEYCLOAK_ADMIN=""$kcAdmin"" --from-literal=KEYCLOAK_ADMIN_PASSWORD=""$kcPass"""
    Invoke-KubectlPipeApply "create secret generic redis-secret -n $Namespace --from-literal=REDIS_PASSWORD=""$redisPass"""
    Invoke-KubectlPipeApply "create secret generic storage-secret -n $Namespace --from-literal=STORAGE_ACCESS_KEY=""$storageKey"" --from-literal=STORAGE_SECRET_KEY=""$storageSec"""
    Invoke-KubectlPipeApply "create secret generic opensearch-secret -n $Namespace --from-literal=OPENSEARCH_INITIAL_ADMIN_PASSWORD=""$opensearchPass"""
    Invoke-KubectlPipeApply "create secret generic mail-secret -n $Namespace --from-literal=MAIL_USERNAME=""$mailUser"" --from-literal=MAIL_PASSWORD=""$mailPass"""
} else {
    Write-Info "No .env found — applying deploy/k8s/secrets.yaml (default dev values)"
    kubectl apply -f deploy/k8s/secrets.yaml | Out-Null
}
Write-Success "Secrets applied"

kubectl apply -f deploy/k8s/configmap.yaml | Out-Null
Invoke-KubectlPipeApply "create configmap keycloak-realm -n $Namespace --from-file=ecommerce-realm.json=deploy/keycloak/import/ecommerce-realm.json"
Invoke-KubectlPipeApply "create configmap postgres-init-scripts -n $Namespace --from-file=create-all-databases.sql=deploy/postgres/init/create-all-databases.sql"
Invoke-KubectlPipeApply "create configmap apisix-config -n $Namespace --from-file=config.yaml=deploy/apisix/config.yaml"
Invoke-KubectlPipeApply "create configmap apisix-routes -n $Namespace --from-file=apisix.yaml=deploy/apisix/apisix.yaml"
Write-Success "ConfigMaps applied"
Complete-Step

# =============================================================================
# 8. Deploy Infrastructure
# =============================================================================
Start-Step "Deploy infrastructure"

$infraManifests = @("postgres", "redis", "kafka", "opensearch", "rustfs", "keycloak")
foreach ($infra in $infraManifests) {
    kubectl apply -f "deploy/k8s/infra/$infra.yaml" | Out-Null
}
Write-Success "Infrastructure manifests applied (pods start in the background)"
Complete-Step

# =============================================================================
# 9. Deploy API Gateway, Backend Services & Frontend Webapps
# =============================================================================
Start-Step "Deploy API gateway, backend services & frontend webapps"

Get-ChildItem -Path "deploy/k8s/backend/*.yaml" | ForEach-Object {
    kubectl apply -f $_.FullName | Out-Null
}

# Reconcile shared ingress before APISIX ingress
kubectl apply -f deploy/k8s/ingress/ingress.yaml   | Out-Null
kubectl apply -f deploy/k8s/infra/apisix.yaml      | Out-Null
kubectl apply -f deploy/k8s/frontend/frontend.yaml | Out-Null
if (Test-Path "deploy/k8s/frontend/admin.yaml") {
    kubectl apply -f deploy/k8s/frontend/admin.yaml | Out-Null
}

Write-Success "API gateway + 11 backend services + storefront & admin webapps applied"
Write-Info    "Pods pull images & start in the background — nothing to wait for here"
Complete-Step

# =============================================================================
# 10. Update Windows hosts file
# =============================================================================
Start-Step "Update hosts file"
Update-WindowsHosts -Entry $HostsEntry
Complete-Step

# =============================================================================
# Summary
# =============================================================================
$totalElapsed = Format-Elapsed ([int][Math]::Floor($script:GlobalTimer.Elapsed.TotalSeconds))

Write-Host ""
Write-Hr
Write-Host ("  ✓ All manifests applied!  Total time: {0}" -f $totalElapsed) -ForegroundColor Green
Write-Host "  Kubernetes now pulls images & starts pods in the background — they come up on their own." -ForegroundColor DarkGray
Write-Host ""
Write-Host ("  {0,-16}  {1}" -f "Runtime", $script:ContainerRuntime) -ForegroundColor DarkGray
Write-Host ("  {0,-16}  {1}" -f "Service", "URL") -ForegroundColor White
Write-Host ("  {0,-16}  {1}" -f "───────────────", "───────────────────────────────────────────") -ForegroundColor DarkGray
Write-Host ("  {0,-16}  " -f "Frontend") -NoNewline -ForegroundColor White; Write-Host "http://ecommerce.local" -ForegroundColor Cyan
Write-Host ("  {0,-16}  " -f "Admin") -NoNewline -ForegroundColor White; Write-Host "http://admin.ecommerce.local" -ForegroundColor Cyan
Write-Host ("  {0,-16}  " -f "API Gateway") -NoNewline -ForegroundColor White; Write-Host "http://api.ecommerce.local" -ForegroundColor Cyan
Write-Host ("  {0,-16}  " -f "Keycloak") -NoNewline -ForegroundColor White; Write-Host "http://keycloak.ecommerce.local" -ForegroundColor Cyan
Write-Host ("  {0,-16}  " -f "RustFS Console") -NoNewline -ForegroundColor White; Write-Host "http://rustfs.ecommerce.local/rustfs/console/" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Pods are still pulling/starting — give them a few minutes." -ForegroundColor DarkGray
Write-Host ("  Watch:        kubectl get pods -n {0} -w" -f $Namespace) -ForegroundColor DarkGray
Write-Host ("  Wait-all:     kubectl wait --for=condition=Ready pod --all -n {0} --timeout=600s" -f $Namespace) -ForegroundColor DarkGray
if ($script:ContainerRuntime -eq "podman") {
    Write-Host ("  Podman pipe:  `$env:DOCKER_HOST = '{0}'" -f $env:DOCKER_HOST) -ForegroundColor DarkGray
}
Write-Hr

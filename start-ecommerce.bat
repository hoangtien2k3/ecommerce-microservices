@echo off
:: =============================================================================
:: start-ecommerce.bat — One-shot local deployment for Windows
::
:: Requires: Docker Desktop + Git Bash OR WSL
::
:: Usage: Double-click this file, or run in CMD/PowerShell:
::   .\start-ecommerce.bat
:: =============================================================================

title Ecommerce Microservices — K8s Setup

echo.
echo  ╔══════════════════════════════════════════════════════════╗
echo  ║     Ecommerce Microservices ^· Local K8s Setup           ║
echo  ║     k3d  +  k3s  +  NGINX Ingress                        ║
echo  ╚══════════════════════════════════════════════════════════╝
echo.

:: ── Check Docker ─────────────────────────────────────────────────────────────
where docker >nul 2>&1
if %errorlevel% neq 0 (
    echo  [ERROR] Docker not found.
    echo          Install Docker Desktop: https://www.docker.com/get-started
    pause
    exit /b 1
)

docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo  [ERROR] Docker is not running.
    echo          Please start Docker Desktop and try again.
    pause
    exit /b 1
)
echo  [OK]  Docker is running

:: ── Try Git Bash first ────────────────────────────────────────────────────────
set GITBASH=
for %%p in (
    "C:\Program Files\Git\bin\bash.exe"
    "C:\Program Files\Git\usr\bin\bash.exe"
    "%LOCALAPPDATA%\Programs\Git\bin\bash.exe"
) do (
    if exist %%p (
        set GITBASH=%%p
        goto :found_gitbash
    )
)

:: ── Try WSL ──────────────────────────────────────────────────────────────────
where wsl >nul 2>&1
if %errorlevel% equ 0 (
    echo  [OK]  Using WSL to run setup...
    echo.
    wsl bash k3d-setup.sh
    goto :done
)

echo  [ERROR] Neither Git Bash nor WSL found.
echo          Please install one of:
echo          - Git for Windows: https://git-scm.com/download/win
echo          - WSL:  wsl --install  (in PowerShell as Administrator)
pause
exit /b 1

:found_gitbash
echo  [OK]  Using Git Bash: %GITBASH%
echo.
%GITBASH% -c "bash k3d-setup.sh"
goto :done

:done
echo.
pause

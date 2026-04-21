#!/usr/bin/env bash
# Build a runnable fat JAR of FiveM Booster. Works on Linux, macOS and Windows
# (via git-bash / WSL). Produces dist/FiveMBooster.jar that you can launch with
#   java -jar dist/FiveMBooster.jar
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

rm -rf out dist
mkdir -p out dist

echo "[1/3] Compiling..."
javac -d out $(find src -name '*.java')

echo "[2/3] Packaging JAR..."
jar --create --file dist/FiveMBooster.jar --manifest build/manifest.txt -C out .

echo "[3/3] Done."
echo "  Runnable JAR: dist/FiveMBooster.jar"
echo "  Run with:     java -jar dist/FiveMBooster.jar"

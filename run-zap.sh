#!/usr/bin/env bash
# ============================================================
# run-zap.sh – DAST: OWASP ZAP Full Scan tegen lokale OpenMRS
# ============================================================
# Vereisten:
#   - Docker draait
#   - OpenMRS draait via: docker-compose up -d
#     en is bereikbaar op http://localhost:8080/openmrs/
#
# Gebruik:
#   chmod +x run-zap.sh
#   ./run-zap.sh
#
# Output: docs/dast/zap-report.html  (HTML rapport)
#         docs/dast/zap-report.json  (JSON audit trail)
#         docs/dast/zap-report.xml   (XML export)
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUTPUT_DIR="$SCRIPT_DIR/docs/dast"
mkdir -p "$OUTPUT_DIR"

# Op Windows/macOS met Docker Desktop: gebruik host.docker.internal
# Op Linux: gebruik het bridge-gateway IP of --network host
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
  TARGET="http://172.17.0.1:8080/openmrs"
  NETWORK_FLAG="--network host"
  TARGET="http://localhost:8080/openmrs"
else
  TARGET="http://host.docker.internal:8080/openmrs"
  NETWORK_FLAG=""
fi

echo "============================================"
echo " DAST: OWASP ZAP Full Scan"
echo "--------------------------------------------"
echo " Target : $TARGET"
echo " Output : $OUTPUT_DIR"
echo "============================================"
echo ""

# Controleer of OpenMRS bereikbaar is
echo "Controleren of OpenMRS bereikbaar is..."
if ! curl -sf --max-time 5 "$TARGET" > /dev/null 2>&1; then
  echo ""
  echo "❌  OpenMRS is niet bereikbaar op $TARGET"
  echo "    Start eerst OpenMRS met: docker-compose up -d"
  echo "    En wacht tot http://localhost:8080/openmrs/ laadt."
  exit 1
fi
echo "✅  OpenMRS is bereikbaar."
echo ""

# Pull de nieuwste ZAP image
echo "ZAP image ophalen..."
docker pull ghcr.io/zaproxy/zaproxy:stable

echo ""
echo "ZAP Full Scan starten (dit duurt enkele minuten)..."
echo ""

# Voer ZAP uit
# -t : target URL
# -r : HTML rapport
# -J : JSON rapport
# -x : XML rapport
# -I : negeer regels die de scan laten falen (exit code 0 ook bij findings)
# -a : include alpha passieve scanregels
docker run --rm \
  $NETWORK_FLAG \
  -v "$OUTPUT_DIR":/zap/wrk/:rw \
  ghcr.io/zaproxy/zaproxy:stable \
  zap-full-scan.py \
  -t "$TARGET" \
  -r zap-report.html \
  -J zap-report.json \
  -x zap-report.xml \
  -I \
  -a

echo ""
echo "============================================"
echo " ✅ ZAP scan voltooid!"
echo "--------------------------------------------"
echo " Rapporten opgeslagen in: docs/dast/"
echo "   - zap-report.html  (open in browser)"
echo "   - zap-report.json  (audit trail)"
echo "   - zap-report.xml   (XML export)"
echo "============================================"
echo ""
echo "Commit de rapporten naar de repo als bewijslast:"
echo "  git add docs/dast/"
echo "  git commit -m 'dast: voeg OWASP ZAP rapport toe (NEN-7510 8.29)'"

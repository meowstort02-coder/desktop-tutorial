#!/bin/bash
# TRIGGER CODEMAGIC BUILD (FROM COLIN)

URL="https://api.codemagic.io/hooks/69a0680414a23bbf5a69ae15"

echo "Triggering build for branch: main..."
curl -X POST "$URL" \
     -H "Content-Type: application/json" \
     -d '{"branch": "main"}'

if [ $? -eq 0 ]; then
    echo "SUCCESS: Build signal sent to CodeMagic."
else
    echo "ERROR: Failed to contact build server."
fi

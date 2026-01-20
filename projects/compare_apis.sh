#!/bin/bash

BASE_BRANCH="main"
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

echo "=== API Changes Summary: $CURRENT_BRANCH vs $BASE_BRANCH ==="
echo ""

for file in $(git diff --name-only $BASE_BRANCH...HEAD | grep '\.api$'); do
    echo "=========================================="
    echo "FILE: $file"
    echo "=========================================="

    if git show $BASE_BRANCH:"$file" > /tmp/main.api 2>/dev/null; then
        git show HEAD:"$file" > /tmp/current.api 2>/dev/null
        echo "[MODIFIED]"
        diff -u /tmp/main.api /tmp/current.api | grep -E '^\+|^\-|^@@' | head -40
    else
        echo "[NEW FILE in $CURRENT_BRANCH]"
        git show HEAD:"$file" | head -20
    fi
    echo ""
done

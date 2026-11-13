#!/usr/bin/env python3
# SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
# SPDX-License-Identifier: Apache-2.0

"""Show where an app's shared files and toolchain versions differ from this template.

Usage: drift.py <app dir>   (exit status 1 when anything differs)
"""

import re
import subprocess
import sys
from pathlib import Path

TEMPLATE = Path(__file__).resolve().parent.parent
SHARED = (
    "build-logic/",
    "gradle/wrapper/",
    "gradlew",
    "gradlew.bat",
    ".editorconfig",
    ".pre-commit-config.yaml",
    ".github/workflows/",
)
TOOLCHAIN = ("agp", "kotlin", "ksp", "hilt", "composeBom", "pvot")


def shared_files():
    out = subprocess.run(
        ["git", "-C", str(TEMPLATE), "ls-files"], capture_output=True, text=True, check=True
    ).stdout
    return [p for p in out.splitlines() if p.startswith(SHARED)]


def versions(catalog):
    if not catalog.is_file():
        return {}
    section = catalog.read_text().split("[versions]", 1)[-1].split("\n[", 1)[0]
    return dict(re.findall(r'^\s*([\w-]+)\s*=\s*"([^"]+)"', section, re.M))


def main():
    if len(sys.argv) != 2:
        sys.exit(__doc__.strip().splitlines()[-1])
    app = Path(sys.argv[1]).expanduser().resolve()
    drift = []
    for rel in shared_files():
        theirs = app / rel
        if not theirs.exists():
            drift.append(f"missing   {rel}")
        elif theirs.read_bytes() != (TEMPLATE / rel).read_bytes():
            drift.append(f"differs   {rel}")
    ours = versions(TEMPLATE / "gradle/libs.versions.toml")
    theirs = versions(app / "gradle/libs.versions.toml")
    for key in TOOLCHAIN:
        if key in theirs and theirs[key] != ours[key]:
            drift.append(f"version   {key}: {theirs[key]} (template {ours[key]})")
        elif key in ("agp", "kotlin") and key not in theirs:
            drift.append(f"version   {key}: not in the app's catalog (template {ours[key]})")
    print("\n".join(drift) if drift else f"{app.name} matches the template")
    sys.exit(1 if drift else 0)


if __name__ == "__main__":
    main()

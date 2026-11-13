#!/usr/bin/env python3
# SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
# SPDX-License-Identifier: Apache-2.0

"""Create a new PVOT-OSS app from this template.

Usage: new-app.py --name Notes --package org.prauga.notes --repo PvotNotes --dest ~/apps/Notes
"""

import argparse
import re
import shutil
import subprocess
import sys
from pathlib import Path

TEMPLATE = Path(__file__).resolve().parent.parent
PACKAGE = "org.prauga.template"
REPO = "pvot-app-template"
SKIP = ("tools/", "README.md")
BINARY = {".jar", ".png", ".webp", ".jpg"}


def tracked_files():
    out = subprocess.run(
        ["git", "-C", str(TEMPLATE), "ls-files", "-z"], capture_output=True, check=True
    ).stdout
    return [p for p in out.decode().split("\0") if p and not p.startswith(SKIP)]


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--name", required=True, help="app name shown to users, e.g. Notes")
    parser.add_argument("--package", required=True, help="Kotlin package, e.g. org.prauga.notes")
    parser.add_argument("--app-id", help="applicationId; defaults to --package")
    parser.add_argument("--repo", required=True, help="GitHub repository under PVOT-OSS, e.g. PvotNotes")
    parser.add_argument("--dest", required=True, type=Path, help="directory to create")
    args = parser.parse_args()

    if not re.fullmatch(r"[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+", args.package):
        sys.exit(f"new-app: invalid package {args.package!r}")
    app_id = args.app_id or args.package
    ident = re.sub(r"\W", "", args.name.title().replace(" ", ""))
    if not ident or not ident[0].isalpha():
        sys.exit(f"new-app: can't derive a class name from {args.name!r}")
    dest = args.dest.expanduser().resolve()
    if dest.exists() and any(dest.iterdir()):
        sys.exit(f"new-app: {dest} exists and is not empty")

    replacements = [
        (f'applicationId = "{PACKAGE}"', f'applicationId = "{app_id}"'),
        (f"(package `{PACKAGE}`)", f"(package `{app_id}`)"),
        (PACKAGE, args.package),
        (REPO, args.repo),
        ("TemplateApplication", f"{ident}Application"),
        ("Theme.Template", f"Theme.{ident}"),
        ('<string name="app_name">Template</string>', f'<string name="app_name">{args.name}</string>'),
        ('rootProject.name = "Template"', f'rootProject.name = "{ident}"'),
        ('SPDX-PackageName = "Template"', f'SPDX-PackageName = "{args.name}"'),
        ("# Privacy Policy — Template", f"# Privacy Policy — {args.name}"),
        ("Template (package", f"{args.name} (package"),
    ]
    package_path = PACKAGE.replace(".", "/")
    new_path = args.package.replace(".", "/")

    for rel in tracked_files():
        src = TEMPLATE / rel
        out = dest / rel.replace(package_path, new_path).replace("TemplateApplication", f"{ident}Application")
        out.parent.mkdir(parents=True, exist_ok=True)
        if src.is_symlink():
            out.symlink_to(src.readlink())
            continue
        if src.suffix in BINARY:
            shutil.copy2(src, out)
            continue
        text = src.read_bytes().decode()
        if rel == "fastlane/metadata/android/en-US/title.txt":
            text = f"{args.name}\n"
        for old, new in replacements:
            text = text.replace(old, new)
        out.write_bytes(text.encode())
        shutil.copymode(src, out)

    # REUSE-IgnoreStart
    (dest / "README.md").write_text(
        "<!--\nSPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>\n"
        "SPDX-License-Identifier: Apache-2.0\n-->\n\n"
        f"# {args.name}\n\n"
        "## Build\n\n```sh\n./gradlew assembleDebug assembleRelease testDebugUnitTest lintDebug\n```\n"
    )
    # REUSE-IgnoreEnd
    print(f"Created {args.name} in {dest}")
    print("Next: replace the home feature, write the store text and privacy policy for what the app")
    print("does, set pvot.minSdk if needed, then build and commit.")


if __name__ == "__main__":
    main()

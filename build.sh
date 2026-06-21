#!/usr/bin/env bash
# =============================================================================
# build.sh — Genyris build script (converted from build.xml)
# Runs all targets sequentially: clean → init → git-info → version →
#   compile → make-jar → distributable → package → test → reports
# =============================================================================

set -euo pipefail
set -x

# =============================================================================
# Properties (mirrors Ant <property> definitions)
# =============================================================================
NEEDED_LIB_DIR="needed"
JUNIT_LIB_DIR="needed"
BUILD_DIR="build"
DIST_DIR="dist"
REL_DIR="rel"
SRC_COMPILE_DIR="${BUILD_DIR}/classes"
SRC_DIR="src"
VERSION_DIR="${SRC_DIR}/org/genyris/load/boot"
GIT_DESCRIBE_FILE="${BUILD_DIR}/git-describe.txt"
BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TODAY="$(date '+%d/%m/%Y %H:%M:%S')"

# Load secrets if present
if [[ -f secrets.properties ]]; then
    # Convert Java properties format to bash exports
    while IFS='=' read -r key value; do
        [[ "$key" =~ ^#.*$ || -z "$key" ]] && continue
        export "${key}=${value}"
    done < secrets.properties
fi

# Classpath helper — all jars in needed/
CLASSPATH_LIBS="$(find "${NEEDED_LIB_DIR}" -name '*.jar' | tr '\n' ':')"
SRC_COMPILE_CLASSPATH="${CLASSPATH_LIBS}${SRC_COMPILE_DIR}"

# =============================================================================
# Target: only-clean
# =============================================================================
target_only_clean() {
    echo "==> [only-clean] Removing build artefacts..."
    rm -rf "${BUILD_DIR}" "${DIST_DIR}" "${REL_DIR}"
}

# =============================================================================
# Target: only-init
# =============================================================================
target_only_init() {
    echo "==> [only-init] Creating directories..."
    mkdir -p "${BUILD_DIR}" "${VERSION_DIR}" "${DIST_DIR}" "${REL_DIR}" "${SRC_COMPILE_DIR}"
}

# =============================================================================
# Target: only-git-workspace  (reads Git describe info)
# =============================================================================
target_only_git_workspace() {
    echo "==> [only-git-workspace] Reading Git workspace info..."

    GIT_WORKSPACE_VERSION="$(git describe --tags)"
    echo "git.workspace.version = ${GIT_WORKSPACE_VERSION}"

    mkdir -p "${BUILD_DIR}"
    git describe --tags --abbrev=0 > "${GIT_DESCRIBE_FILE}"
    GIT_WORKSPACE_TAG="$(cat "${GIT_DESCRIBE_FILE}")"
    echo "git.workspace.tag = ${GIT_WORKSPACE_TAG}"

    if [[ "${GIT_WORKSPACE_TAG}" == "${GIT_WORKSPACE_VERSION}" ]]; then
        WORKSPACE_CONTAINS_TAGGED_VERSION=true
    else
        WORKSPACE_CONTAINS_TAGGED_VERSION=false
    fi
    echo "workspace-contains-a-tagged-version = ${WORKSPACE_CONTAINS_TAGGED_VERSION}"

    # Strip trailing .N to get specification version (e.g. 1.2.3.4 → 1.2.3)
    GIT_WORKSPACE_SPECIFICATION_VERSION="$(head -1 "${GIT_DESCRIBE_FILE}" | sed -E 's/\.[0-9]+$//')"
    echo "git.workspace.specification.version = ${GIT_WORKSPACE_SPECIFICATION_VERSION}"
}

# =============================================================================
# Target: only-git-status  (checks for uncommitted changes)
# =============================================================================
target_only_git_status() {
    echo "==> [only-git-status] Checking for uncommitted changes..."
    GIT_STATUS="$(git status --porcelain | wc -l | tr -d ' ')"
    echo "git.status = ${GIT_STATUS}"
    if [[ "${GIT_STATUS}" != "0" ]]; then
        WORKING_FOLDER_HAS_UNCOMMITTED_CHANGES=true
    else
        WORKING_FOLDER_HAS_UNCOMMITTED_CHANGES=false
    fi
    echo "working-folder-has-uncommitted-changes = ${WORKING_FOLDER_HAS_UNCOMMITTED_CHANGES}"
}

# =============================================================================
# Target: git-info  (combines workspace + status, sets build number)
# =============================================================================
target_git_info() {
    echo "==> [git-info] Gathering Git information..."
    target_only_git_workspace
    target_only_git_status

    SPECIFICATION_VERSION="${GIT_WORKSPACE_SPECIFICATION_VERSION}"

    # Emulate Ant's <buildnumber>: read/increment a counter file
    BUILD_NUMBER_FILE="${GIT_WORKSPACE_VERSION}-build.number"
    if [[ -f "${BUILD_NUMBER_FILE}" ]]; then
        BUILD_NUMBER="$(cat "${BUILD_NUMBER_FILE}")"
        BUILD_NUMBER=$(( BUILD_NUMBER + 1 ))
    else
        BUILD_NUMBER=1
    fi
    echo "${BUILD_NUMBER}" > "${BUILD_NUMBER_FILE}"
    BUILD_ID="${BUILD_NUMBER}"

    echo "specification version = ${SPECIFICATION_VERSION}"
    echo "Build Number = ${BUILD_ID}"
}

# =============================================================================
# Target: version-default  (compute version string)
# =============================================================================
target_version_default() {
    echo "==> [version-default] Computing version..."
    target_git_info

    IMPLEMENTATION_VERSION="${GIT_WORKSPACE_VERSION}-${BUILD_ID}"
    VERSION="${IMPLEMENTATION_VERSION}"
    VERSION_UNDERSCORES="${VERSION//./_}"

    echo "Version = ${VERSION}"
    echo "Version.underscores = '${VERSION_UNDERSCORES}'"
    echo "implementation-version = '${IMPLEMENTATION_VERSION}'"
}

# =============================================================================
# Target: only-compile
# =============================================================================
target_only_compile() {
    echo "==> [only-compile] Compiling Java sources..."
    javac \
        -sourcepath "${SRC_DIR}" \
        -d "${SRC_COMPILE_DIR}" \
        -classpath "${SRC_COMPILE_CLASSPATH}" \
        -source 25 -target 25 \
        -Xlint:deprecation \
        $(find "${SRC_DIR}" \( -path "*/org/**/*.java" -o -path "*/com/**/*.java" \) -print)
}

# =============================================================================
# Target: only-make-jar
# =============================================================================
target_only_make_jar() {
    echo "==> [only-make-jar] Creating JAR..."
    JAR_FILE="${DIST_DIR}/genyris-bin-${VERSION}.jar"

    # Build manifest
    MANIFEST_FILE="${BUILD_DIR}/MANIFEST.MF"
    cat > "${MANIFEST_FILE}" <<EOF
Main-Class: org.genyris.interp.ClassicReadEvalPrintLoop
Specification-Title: Genyris
Specification-Version: ${SPECIFICATION_VERSION}
Specification-Vendor: genyris.org
Implementation-Title: Genyris
Implementation-Version: ${IMPLEMENTATION_VERSION}
Implementation-Vendor: Peter William Birch
Implementation-Date: ${TODAY}
EOF

    jar cfm "${JAR_FILE}" "${MANIFEST_FILE}" \
        -C "${BASEDIR}" README \
        -C "${BASEDIR}" LICENSE \
        -C "${BUILD_DIR}" version \
        $(cd "${SRC_COMPILE_DIR}" && find . -name '*.class' ! -name 'GenyrisServlet.class' | sed "s|^|-C ${SRC_COMPILE_DIR} |") \
        $(cd "${SRC_DIR}" && find . -name '*.properties' | sed "s|^|-C ${SRC_DIR} |") \
        $(ls -1 ${SRC_DIR}/resources/boot | sed "s|^|-C src/resources ./boot/|") \
        $(cd "${BASEDIR}" && find test -type f | sed "s|^|-C ${BASEDIR} |")

    echo "Created: ${JAR_FILE}"
}

# =============================================================================
# Target: distributable  (copy needed jars, create binary zip)
# =============================================================================
target_distributable() {
    echo "==> [distributable] Creating binary distribution zip..."

    # Copy needed jars into dist/
    find "${BASEDIR}/needed" -name '*.jar' -exec cp -v {} "${DIST_DIR}/" \;

    ZIP_FILE="rel/genyris-binary-${VERSION}.zip"
    zip -r "${ZIP_FILE}" \
        LICENSE README \
        dist/ lib/ bin/ examples/ test/
    md5sum "${ZIP_FILE}" > "${ZIP_FILE}.MD5"
    echo "Created: ${ZIP_FILE}"
}

# =============================================================================
# Target: package  (Install4j packaging)
# =============================================================================
target_package() {
    echo "==> [package] Running Install4j packaging..."

    # Substitute version token in install4j config
    sed "s/@GENYRIS-VERSION@/${VERSION}/g" \
        package/genyris.install4j > dist/genyris.install4j

    bash -x -u -c "install4jc --disable-bundling --license=\"${INSTALL4J_LICENSE:-}\" dist/genyris.install4j"

    for f in \
        "rel/genyris_windows-x64_${VERSION_UNDERSCORES}.zip" \
        "rel/genyris_unix_${VERSION_UNDERSCORES}.tar.gz" \
        "rel/genyris_linux_${VERSION_UNDERSCORES}.deb" \
        "rel/genyris_linux_${VERSION_UNDERSCORES}.rpm" \
        "rel/genyris_macos_${VERSION_UNDERSCORES}.dmg"; do
        [[ -f "$f" ]] && md5sum "$f" > "$f.MD5" || echo "Warning: $f not found, skipping checksum."
    done
}

# =============================================================================
# Target: only-test
# =============================================================================
target_only_test() {
    echo "==> [only-test] Running acceptance tests..."
    GENYRIS_HOME="${BASEDIR}" bash -u -x -c \
        "java -Xmx256M \
            -classpath '${GENYRIS_HOME}/dist/*:${GENYRIS_HOME}/dist/needed/*' \
            org.genyris.interp.ClassicReadEvalPrintLoop \
            test/acceptance/suite.g"
}

# =============================================================================
# Target: only-report-todo
# =============================================================================
target_only_report_todo() {
    echo "==> [reports] Running TODO/FIXME report..."
    GENYRIS_HOME="${BASEDIR}" bash -c \
        "java -Xmx256M \
            -classpath '${GENYRIS_HOME}/dist/*:${GENYRIS_HOME}/dist/needed/*' \
            org.genyris.interp.ClassicReadEvalPrintLoop \
            examples/file-lines.g \
            '.*\.g$|.*\.java$|.*\.lyx' \
            '.*TODO.*|.*FIXME.*' \
            ${BASEDIR}"
}

# =============================================================================
# Write version.properties into build/version/ for JAR inclusion
# =============================================================================
target_write_version() {
    echo "==> [write-version] Writing version.properties to ${BUILD_DIR}/version/..."
    mkdir -p "${BUILD_DIR}/version"
    cat > "${BUILD_DIR}/version/version.properties" <<EOF
version=${VERSION}
implementation-version=${IMPLEMENTATION_VERSION}
specification-version=${SPECIFICATION_VERSION}
build.date=${TODAY}
EOF
}

# =============================================================================
# Target: compile-docs  (create PDF and HTML manual via LyX)
# =============================================================================
target_compile_docs() {
    echo "==> [compile-docs] Building PDF and HTML manual..."

    # Substitute version tokens into the LyX source
    sed \
        -e "s/@GENYRIS-SPECIFICATION-VERSION@/${SPECIFICATION_VERSION}/g" \
        -e "s/@GENYRIS-IMPLEMENTATION-VERSION@/${IMPLEMENTATION_VERSION}/g" \
        doc/reference/manual.lyx > "dist/genyris-manual-${VERSION}.lyx"
    echo "Copied manual.lyx → dist/genyris-manual-${VERSION}.lyx"

    # Copy any images needed by LyX
    find "${BASEDIR}/doc/reference/" -name '*jpg' -exec cp -v {} "${DIST_DIR}/" \;

    # Export to PDF
    lyx -batch --export pdf2 "genyris-manual-${VERSION}.lyx" 2>&1 | tee dist/lyx.log

    # Export to XHTML
    lyx -batch --export-to xhtml "genyris-manual-${VERSION}.html" "genyris-manual-${VERSION}.lyx" 2>&1 | tee dist/lyx.log

    # Clean up intermediate files
    find "${DIST_DIR}" \( -name '*jpg' -o -name '*lyx' -o -name '*dvi' \) -delete -print

    # Copy final outputs to rel/ and generate checksums
    cp -v "dist/genyris-manual-${VERSION}.pdf"  "rel/genyris-manual-${VERSION}.pdf"
    md5sum "rel/genyris-manual-${VERSION}.pdf"  > "rel/genyris-manual-${VERSION}.pdf.MD5"

    cp -v "dist/genyris-manual-${VERSION}.html" "rel/genyris-manual-${VERSION}.html"
    md5sum "rel/genyris-manual-${VERSION}.html" > "rel/genyris-manual-${VERSION}.html.MD5"

    echo "Manual PDF and HTML written to rel/"
}

# =============================================================================
# Main — run all targets sequentially (mirrors build-workspace default target)
# =============================================================================
echo "========================================================"
echo " Genyris Build Script"
echo " $(date)"
echo "========================================================"

target_only_clean
target_only_init
target_version_default
target_write_version
target_only_compile
target_only_make_jar
target_distributable
target_only_test
target_package
target_only_report_todo
target_compile_docs

echo ""
echo "========================================================"
echo " Build complete: ${VERSION}"
echo "========================================================"

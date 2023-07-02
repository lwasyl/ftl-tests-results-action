#!/bin/bash
set -eo pipefail

java -jar ftl-tests-results-r8.jar --merged-results="$INPUT_MERGED_RESULT_FILE" > markdown_summary

delimiter=$(openssl rand -hex 20)
echo "summary-markdown<<$delimiter" >> $GITHUB_OUTPUT
cat markdown_summary >> $GITHUB_OUTPUT
echo "$delimiter" >> $GITHUB_OUTPUT

cat markdown_summary >> $GITHUB_STEP_SUMMARY

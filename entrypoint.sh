#!/bin/bash
set -eo pipefail

summary=$(java -jar ftl-tests-results-r8.jar --merged-results="$INPUT_MERGED_RESULT_FILE")

delimiter=$(openssl rand -hex 20)
echo "summary-markdown<<$delimiter" >> $GITHUB_OUTPUT
echo "$summary" >> $GITHUB_OUTPUT
echo "$delimiter" >> $GITHUB_OUTPUT

echo "$summary" >> $GITHUB_STEP_SUMMARY

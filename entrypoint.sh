#!/bin/bash
set -eo pipefail

./gradlew assemble
mv build/libs/ftl-tests-results-r8.jar ftl-tests-results-r8.jar

summary=$(java -jar ftl-tests-results-r8.jar summary --merged-results="$INPUT_MERGED_RESULT_FILE")
failures=$(java -jar ftl-tests-results-r8.jar ghComment --merged-results="$INPUT_MERGED_RESULT_FILE")

if [ ! -z "failures" ]; then
  delimiter=$(openssl rand -hex 20)
  echo "summary-markdown<<$delimiter" >> $GITHUB_OUTPUT
  echo "$failures" >> $GITHUB_OUTPUT
  echo "$delimiter" >> $GITHUB_OUTPUT
fi

echo "$summary" >> $GITHUB_STEP_SUMMARY

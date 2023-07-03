# Firebase Test Lab summary action

This action extracts FTL tests results summary from a `*-merged-results.xml` file to a markdown output with the most important information.

> **_NOTE:_**  The repository is still work in progress

Example usage:

```yaml
  -   env: # set for entire job
      FTL_RESULTS_DIRECTORY: '${{ github.run_id }}-${{ github.run_number }}-${{ github.run_attempt }}'

  -   uses: google-github-actions/auth@v0
      with:
          credentials_json: ${{ secrets.YOUR_GCLOUD_CREDENTIALS }}
          project_id: your-project-id

  -   uses: google-github-actions/setup-gcloud@v0.6.2

  -   id: run-ftl-tests
      run: |
          gcloud beta firebase test android run \
          ... \
          --results-dir=$FTL_RESULTS_DIRECTORY

  -   id: fetch-merged-results
      run: gsutil cp "gs://test-lab-your-bucket-name/$FTL_RESULTS_DIRECTORY/*results_merged.xml merged_results.xml

  -   id: ftl-tests-summary
      uses: lwasyl/ftl-tests-results-action@v0.1.1
      with:
          merged-results-file: ./merged_results.xml

  # The action will automatically post a Github Job summary
  # Optionally, you can access `summary-markdown` output to e.g. post a comment with the results

  -   uses: peter-evans/find-comment@v2
      id: find_comment
      if: github.event_name == 'pull_request'
      with:
          issue-number: ${{ github.event.pull_request.number }}
          body-includes: "FTL tests results"

  -   uses: peter-evans/create-or-update-comment@v3
      with:
          body: |
              ${{ steps.ftl-tests-summary.outputs.summary-markdown }}
          edit-mode: replace
          comment-id: ${{ steps.find_comment.outputs.comment-id }}
          issue-number: ${{ github.event.pull_request.number }}
          token: ${{ secrets.GITHUB_TOKEN }}
```

License
-------

    Copyright [2023] [Łukasz Wasylkowski]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

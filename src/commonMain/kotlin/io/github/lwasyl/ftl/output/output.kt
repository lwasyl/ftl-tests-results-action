package io.github.lwasyl.ftl.output

import io.github.lwasyl.ftl.model.FtlTestsResults
import io.github.lwasyl.ftl.model.TestCase
import io.github.lwasyl.ftl.model.TestResult
import io.github.lwasyl.ftl.transformations.shorten
import kotlin.time.DurationUnit

internal fun FtlTestsResults.buildMarkdownOutput() = buildString {
    append("# FTL tests results\n\n")

    if (summary.failures > 0) {
        append("## ${summary.failures} failed tests:\n\n")
        testsTableHeader()
        tests.asSequence().filter { it.isFailed }.forEach { append(it.testsTableRow()) }
        append("\n\n\n")
    }

    if (summary.flakes > 0) {
        append("## ${summary.flakes} flaky tests:\n\n")
        testsTableHeader()
        tests.asSequence().filter { it.isFlaky }.forEach { append(it.testsTableRow()) }
        append("\n\n\n")
    }

    append("## Longest tests:\n\n")
    testsTableHeader()
    tests.sortedByDescending { it.duration }.take(10).forEach { append(it.testsTableRow()) }

    if (summary.failures > 0) {
        append("## Failed tests errors:\n")
        tests.asSequence()
            .mapNotNull { test -> (test.result as? TestResult.Failure)?.let { test.shortName to it } }
            .forEach { (name, failure) ->
                failure.errors.forEachIndexed { idx, stacktrace ->
                    append("\n")
                    append(summaryBlock("$name (${idx + 1})", stacktrace.shorten().value))
                }
            }
    }

    if (summary.flakes > 0) {
        append("## Flaky tests errors:\n")
        tests.asSequence()
            .mapNotNull { test -> (test.result as? TestResult.SuccessfulButFlaky)?.let { test.shortName to it } }
            .forEach { (name, failure) ->
                failure.errors.forEachIndexed { idx, stacktrace ->
                    append("\n")
                    append(summaryBlock("$name (${idx + 1})", stacktrace.shorten().value))
                }
            }
    }
}

private fun StringBuilder.testsTableHeader() {
    append("| Test | Result | Duration |\n")
    append("| :--: | :--: | :--: |\n")
}

private fun TestCase.testsTableRow() =
    "| $shortClassName#**$testName** | ${result.symbol} | ${duration.toString(DurationUnit.SECONDS, 2)} |\n"

private fun summaryBlock(preview: String, text: String) = """
<details>
<summary>$preview</summary>

```java
$text
```
</details>${"\n\n"}
"""

private val TestResult.symbol: String
    get() = when (this) {
        is TestResult.Failure -> "❌"
        TestResult.Success -> "✅"
        is TestResult.SuccessfulButFlaky -> "⚠\uFE0F"
    }

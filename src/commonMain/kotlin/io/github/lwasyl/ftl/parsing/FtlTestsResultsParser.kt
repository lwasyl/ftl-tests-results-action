package io.github.lwasyl.ftl.parsing

import io.github.lwasyl.ftl.model.*
import io.github.lwasyl.ftl.xml.XmlFacade
import io.github.lwasyl.ftl.xml.xmlFacade
import okio.Source
import kotlin.time.Duration.Companion.seconds

internal fun parseFtlResults(
    source: Source,
): FtlTestsResults {
    return xmlFacade().read(source)
        .getChildTagsByName("testsuite").single()
        .let { suiteTag ->
            FtlTestsResults(
                summary = suiteTag.extractTestsSummary(),
                tests = suiteTag.getChildTagsByName("testcase").map { it.extractTestCase() }.toList(),
            )
        }
}

private fun XmlFacade.XmlTag.extractTestsSummary() = TestsSummary(
    suiteName = requireAttribute("name"),
    tests = requireAttribute("tests").toInt(),
    failures = requireAttribute("failures").toInt(),
    flakes = requireAttribute("flakes").toInt(),
    errors = requireAttribute("errors").toInt(),
    skipped = requireAttribute("skipped").toInt(),
    totalDuration = requireAttribute("time").toDouble().seconds,
)

private fun XmlFacade.XmlTag.extractTestCase(): TestCase {
    val flaky = getAttribute("flaky")?.toBooleanStrict() ?: false
    val failures =
        getChildTagsByName("failure").map { Stacktrace.fromString(checkNotNull(it.textContent)) }.toList()

    return TestCase(
        fqClassname = requireAttribute("classname"),
        testName = requireAttribute("name"),
        result = when {
            flaky -> TestResult.SuccessfulButFlaky(errors = failures)
            failures.isEmpty() -> TestResult.Success
            else -> TestResult.Failure(errors = failures)
        },
        duration = requireAttribute("time").toDouble().seconds,
    )
}

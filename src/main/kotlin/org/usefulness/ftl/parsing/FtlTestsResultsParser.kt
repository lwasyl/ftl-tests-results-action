package org.usefulness.ftl.parsing

import okio.Source
import org.usefulness.ftl.model.*
import org.usefulness.ftl.xml.DefaultXmlFacade
import org.usefulness.ftl.xml.XmlFacade
import kotlin.time.Duration.Companion.seconds

internal fun parseFtlResults(
    source: Source,
): FtlTestsResults {
    return DefaultXmlFacade.read(source)
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
    val failures = getChildTagsByName("failure").map { Stacktrace.fromString(checkNotNull(it.textContent)) }.toList()

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

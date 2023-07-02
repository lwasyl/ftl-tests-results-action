package org.usefulness.ftl

import kotlin.test.Test

class FtlTestsResultsTest {

    @Test
    fun test() {
        FtlTestsResultsParser().parse("TOOD()").let {
            println("total duration: ${it.summary.totalDuration}")
            println("flakes: ${it.summary.flakes}")

            println("flakes: ${it.tests.filter { it.isFlaky }.map { it.shortName }}")
            println("failed: ${it.tests.filter { it.isFailed }.map { it.shortName }}")

            println("top 3 longest tests: ${it.tests.sortedByDescending { it.duration }.take(3).map { it.shortName }}")

            println("first failed test's failure: ${it.tests.firstNotNullOfOrNull { it.result as? TestResult.Failure }?.errors?.first()?.shorten()}")
        }
    }
}

private fun String.shorten() = lines()
    .fold(mutableListOf(mutableListOf<String>())) { currentLists, line ->
    if (line.startsWith("caused by", ignoreCase = true)) {
        currentLists.add(mutableListOf(line))
    } else {
        currentLists.last().add(line)
    }

    currentLists
}
    .filter { it.isNotEmpty() }
    .map { it.take(5) }
    .flatten()
    .joinToString(separator = "\n")

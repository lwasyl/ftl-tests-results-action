package org.usefulness.ftl

import kotlin.time.Duration

data class TestsResults(
    val summary: TestsSummary,
    val tests: Collection<TestCase>
)

data class TestsSummary(
    val suiteName: String,
    val tests: Int,
    val failures: Int,
    val flakes: Int,
    val errors: Int,
    val skipped: Int,
    val totalDuration: Duration,
)

data class TestCase(
    val className: String,
    val testName: String,
    val result: TestResult,
    val duration: Duration,
) {

    val isFlaky get() = result is TestResult.SuccessfulButFlaky
    val isFailed get() = result is TestResult.Failure
    val isSuccessful get() = result is TestResult.Success
    val fqn get() = "$className#$testName"
    val shortName get() = "${className.substringAfterLast('.')}#$testName"
}

sealed class TestResult {

    object Success : TestResult()

    data class SuccessfulButFlaky(val errors: Collection<String>) : TestResult() {

        init {
            check(errors.isNotEmpty()) { "Can't have a flaky test with no failures" }
        }
    }

    data class Failure(val errors: Collection<String>) : TestResult() {

        init {
            check(errors.isNotEmpty()) { "Can't have a failed test with no failures"}
        }
    }
}

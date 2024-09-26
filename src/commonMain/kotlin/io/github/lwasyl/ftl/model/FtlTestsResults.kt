package io.github.lwasyl.ftl.model

import kotlin.time.Duration

data class FtlTestsResults(
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
    val fqClassname: String,
    val testName: String,
    val result: TestResult,
    val duration: Duration,
) {

    val isFlaky get() = result is TestResult.SuccessfulButFlaky
    val isFailed get() = result is TestResult.Failure
    val isSuccessful get() = result is TestResult.Success
    val fqn get() = "$fqClassname#$testName"
    val shortClassName get() = fqClassname.substringAfterLast('.')
    val shortName get() = "$shortClassName#$testName"
}

sealed class TestResult {

    object Success : TestResult()

    data class SuccessfulButFlaky(val errors: Collection<Stacktrace>) : TestResult() {

        init {
            check(errors.isNotEmpty()) { "Can't have a flaky test with no failures" }
        }
    }

    data class Failure(val errors: Collection<Stacktrace>) : TestResult() {

        init {
            check(errors.isNotEmpty()) { "Can't have a failed test with no failures" }
        }
    }
}

value class Stacktrace private constructor(val value: String) {

    companion object {

        fun fromString(string: String): Stacktrace {
            check(string.isNotBlank()) { "Stacktrace can't be empty" }

            return string.lineSequence()
                .filterNot { it.isBlank() }
                .map { it.trim() }
                .map { if (it.startsWith("at")) it.padStart(2, ' ') else it }
                .joinToString(separator = "\n")
                .let { Stacktrace(it) }
        }
    }
}

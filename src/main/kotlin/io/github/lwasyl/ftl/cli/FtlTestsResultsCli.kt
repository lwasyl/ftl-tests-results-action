package io.github.lwasyl.ftl.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.versionOption
import io.github.lwasyl.ftl.output.writeOutput
import io.github.lwasyl.ftl.parsing.parseFtlResults
import okio.sink
import okio.source
import kotlin.io.path.Path

class FtlTestsResultsCli : CliktCommand(name = "summary") {

    private val mergedResultsFileName: String by option(
        "--merged-results",
        help = "Path to `*-test_results_merged.xml` file from the results bucket",
    )
        .required()

    override fun run() {
        writeOutput(
            results = parseFtlResults(Path(mergedResultsFileName).source()),
            sink = System.out.sink(),
        )
    }
}

class AKSJdhaksdjhasd : CliktCommand(name = "ghComment") {

    private val mergedResultsFileName: String by option(
        "--merged-results",
        help = "Path to `*-test_results_merged.xml` file from the results bucket",
    )
        .required()

    override fun run() {
        writeOutput(
            results = parseFtlResults(Path(mergedResultsFileName).source()),
            sink = System.out.sink(),
            onlyErrors = true,
        )
    }
}

fun main(args: Array<String>) {
    val version = NoOpCliktCommand::class.java.`package`.implementationVersion.orEmpty()

    NoOpCliktCommand(name = "ftl-tests-results")
        .versionOption(version)
        .subcommands(
            FtlTestsResultsCli(),
            AKSJdhaksdjhasd(),
        )
        .main(args.toList())
}


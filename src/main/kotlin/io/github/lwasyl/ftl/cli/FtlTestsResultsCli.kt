package io.github.lwasyl.ftl.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.github.lwasyl.ftl.output.writeOutput
import io.github.lwasyl.ftl.parsing.parseFtlResults
import okio.sink
import okio.source
import kotlin.io.path.Path

class FtlTestsResultsCli : CliktCommand() {

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

fun main(args: Array<String>) = FtlTestsResultsCli().main(args)

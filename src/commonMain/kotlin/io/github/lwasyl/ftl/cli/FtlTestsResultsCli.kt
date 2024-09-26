package io.github.lwasyl.ftl.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.github.lwasyl.ftl.output.buildMarkdownOutput
import io.github.lwasyl.ftl.parsing.parseFtlResults
import okio.FileSystem
import okio.Path.Companion.toPath

expect fun platformFileSystem(): FileSystem

class FtlTestsResultsCli : CliktCommand() {

    private val mergedResultsFileName: String by option(
        "--merged-results",
        help = "Path to `*-test_results_merged.xml` file from the results bucket",
    )
        .required()

    override fun run() {
        parseFtlResults(platformFileSystem().source(mergedResultsFileName.toPath()))
            .buildMarkdownOutput()
            .let { println(it) }
    }
}

fun main(args: Array<String>) = FtlTestsResultsCli().main(args)

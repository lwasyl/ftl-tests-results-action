package org.usefulness.ftl

import nl.adaptivity.xmlutil.dom.*
import nl.adaptivity.xmlutil.dom.NodeConsts.ATTRIBUTE_NODE
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.ElementSerializer
import nl.adaptivity.xmlutil.serialization.XML
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import kotlin.time.Duration.Companion.seconds

class FtlTestsResultsParser {

    fun parse(
        filePath: String,
    ): TestsResults {
        val xml = XML {
            repairNamespaces = true
            policy = DefaultXmlSerializationPolicy(pedantic = false, autoPolymorphic = false)
        }

        return filePath.toPath().toSource()
            .use {
                xml.decodeFromString(
                    deserializer = ElementSerializer,
                    string = it.buffer().readUtf8(),
                )
            }
            .let { element ->
                TestsResults(
                    summary = element.attributes.toTestsSummary(),
                    tests = element.getElementsByTagName("testcase").map { (it as Element).toTestCase() }
                )
            }
    }
}

private fun NamedNodeMap.toTestsSummary() = TestsSummary(
    suiteName = requireAttributeValue("name"),
    tests = requireAttributeValue("tests").toInt(),
    failures = requireAttributeValue("failures").toInt(),
    flakes = requireAttributeValue("flakes").toInt(),
    errors = requireAttributeValue("errors").toInt(),
    skipped = requireAttributeValue("skipped").toInt(),
    totalDuration = requireAttributeValue("time").toDouble().seconds
)

private fun Element.toTestCase(): TestCase {
    val flakyAttrValue = attributes.getNamedItem("flaky")?.textContent?.toBooleanStrict() ?: false
    val failureTagsTexts = getElementsByTagName("failure").map { checkNotNull(it.textContent) }
    val timeText = attributes.requireAttributeValue("time")

    return TestCase(
        className = attributes.requireAttributeValue("classname"),
        testName = attributes.requireAttributeValue("name"),
        result = when {
            !flakyAttrValue && failureTagsTexts.isEmpty() -> TestResult.Success
            !flakyAttrValue && failureTagsTexts.isNotEmpty() -> TestResult.Failure(errors = failureTagsTexts)
            flakyAttrValue -> TestResult.SuccessfulButFlaky(errors = failureTagsTexts)
            else -> error("Invalid XML structure")
        },
        duration = timeText.toDouble().seconds,
    )
}

private fun NamedNodeMap.requireAttributeValue(name: String) =
    checkNotNull(getNamedItem(name)) { "$name attribute not found in $this" }
        .let {
            check(it.nodeType == ATTRIBUTE_NODE)
            checkNotNull(it.textContent)
        }

private fun <T> NodeList.map(block: (Node) -> T) = iterator().asSequence().map(block).toList()

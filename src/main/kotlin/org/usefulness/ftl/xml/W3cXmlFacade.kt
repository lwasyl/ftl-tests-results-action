package org.usefulness.ftl.xml

import okio.Source
import okio.buffer
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory

object W3cXmlFacade : XmlFacade {

    override fun read(source: Source): XmlFacade.XmlFile {
        return object : XmlFacade.XmlFile {

            private val document = DocumentBuilderFactory.newDefaultInstance()
                .newDocumentBuilder()
                .parse(source.buffer().inputStream())

            override fun getChildTagsByName(name: String) =
                document.getElementsByTagName(name).asSequence()
                    .map { it as Element }
                    .map { element -> element.asXmlTag() }

            private fun Element.asXmlTag(): XmlFacade.XmlTag = object : XmlFacade.XmlTag {

                override val textContent: String?
                    get() = this@asXmlTag.textContent?.takeIf { it.isNotBlank() }

                override fun requireAttribute(name: String) =
                    checkNotNull(attributes.getNamedItem(name)).textContent

                override fun getAttribute(name: String) =
                    attributes.getNamedItem(name)?.textContent

                override fun getChildTagsByName(name: String) =
                    getElementsByTagName(name).asSequence().map { it as Element }.map { it.asXmlTag() }

            }
        }
    }
}

private fun NodeList.asSequence() = sequence {
    for (i in 0 until length) yield(item(i))
}

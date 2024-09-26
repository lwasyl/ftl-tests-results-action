package io.github.lwasyl.ftl.xml

import nl.adaptivity.xmlutil.dom.Element
import nl.adaptivity.xmlutil.dom.getAttributes
import nl.adaptivity.xmlutil.dom.getLength
import nl.adaptivity.xmlutil.serialization.ElementSerializer
import nl.adaptivity.xmlutil.serialization.XML
import okio.Source
import okio.buffer

actual fun xmlFacade(): XmlFacade = JsXmlFacade

object JsXmlFacade : XmlFacade {

    override fun read(source: Source): XmlFacade.XmlFile {
        return object : XmlFacade.XmlFile {

            private val document = XML.decodeFromString(ElementSerializer, source.buffer().readUtf8())

            override fun getChildTagsByName(name: String) =
                document.getElementsByTagName(name).asSequence()
                    .map { it as Element }
                    .map { element -> element.asXmlTag() }

            private fun Element.asXmlTag(): XmlFacade.XmlTag = object : XmlFacade.XmlTag {

                override val textContent: String?
                    get() = this@asXmlTag.textContent?.takeIf { it.isNotBlank() }

                override fun requireAttribute(name: String) =
                    checkNotNull(getAttributes().getNamedItem(name)).textContent!!

                override fun getAttribute(name: String) =
                    getAttributes().getNamedItem(name)?.textContent

                override fun getChildTagsByName(name: String) =
                    getElementsByTagName(name).asSequence().map { it as Element }.map { it.asXmlTag() }
            }
        }
    }
}

private fun nl.adaptivity.xmlutil.dom.NodeList.asSequence() = sequence {
    for (i in 0 until getLength()) yield(item(i))
}

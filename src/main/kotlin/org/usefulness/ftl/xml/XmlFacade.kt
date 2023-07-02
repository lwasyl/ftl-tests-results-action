package org.usefulness.ftl.xml

import okio.Source

typealias DefaultXmlFacade = W3cXmlFacade

interface XmlFacade {

    fun read(source: Source): XmlFile

    interface XmlFile {

        fun getChildTagsByName(name: String): Sequence<XmlTag>
    }

    interface XmlTag {

        val textContent: String?

        fun requireAttribute(name: String): String

        fun getAttribute(name: String): String?

        fun getChildTagsByName(name: String): Sequence<XmlTag>
    }
}

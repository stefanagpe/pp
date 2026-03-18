package main.kotlin

class ParserFactory {

    fun getParser(contentType: String?, content: String): Parser {
        val ct = contentType?.lowercase() ?: ""

        return when {
            "json" in ct -> JsonParser()
            "xml" in ct -> XmlParser()
            "yaml" in ct || "yml" in ct -> YamlParser()

            content.trim().startsWith("{") || content.trim().startsWith("[") -> JsonParser()
            content.trim().startsWith("<?xml") || content.trim().startsWith("<") -> XmlParser()
            ":" in content && "\n" in content -> YamlParser()

            else -> JsonParser()
        }
    }
}
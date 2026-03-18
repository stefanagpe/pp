package main.kotlin

class XmlParser : Parser {
    override fun parse(content: String, url: String) {
        println("=")
        println("Parser XML pentru: $url")
        println("Continut detectat ca XML")
        println("Primele 300 de caractere:")
        println(content.take(300))
        println("=")
    }
}
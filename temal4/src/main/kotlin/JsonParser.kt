package main.kotlin

class JsonParser : Parser {
    override fun parse(content: String, url: String) {
        println("=")
        println("Parser JSON pentru: $url")
        println("Continut detectat ca JSON")
        println("Primele 300 de caractere:")
        println(content.take(300))
        println("=")
    }
}
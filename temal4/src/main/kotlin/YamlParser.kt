package main.kotlin

class YamlParser : Parser {
    override fun parse(content: String, url: String) {
        println("=")
        println("Parser YAML pentru: $url")
        println("Continut detectat ca YAML")
        println("Primele 300 de caractere:")
        println(content.take(300))
        println("=")
    }
}
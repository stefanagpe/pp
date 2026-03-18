package main.kotlin

fun main() {
    val parserFactory = ParserFactory()
    val crawler = WebCrawler(parserFactory)

    val startUrl = "https://httpbin.org/json"
    val maxDepth = 1

    crawler.crawl(startUrl, maxDepth)
}
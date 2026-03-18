package main.kotlin

import org.jsoup.Connection
import org.jsoup.Jsoup
import java.net.URL

class WebCrawler(
    private val parserFactory: ParserFactory
) {

    private val visitedUrls = mutableSetOf<String>()

    fun crawl(startUrl: String, maxDepth: Int) {
        crawlRecursive(startUrl, 0, maxDepth)
    }

    private fun crawlRecursive(currentUrl: String, currentDepth: Int, maxDepth: Int) {
        if (currentDepth > maxDepth) return
        if (currentUrl in visitedUrls) return

        visitedUrls.add(currentUrl)

        println("\nVizitez: $currentUrl | adancime: $currentDepth")

        try {
            val response: Connection.Response = Jsoup.connect(currentUrl)
                .ignoreContentType(true)
                .execute()

            val content = response.body()
            val contentType = response.contentType()

            val parser = parserFactory.getParser(contentType, content)
            parser.parse(content, currentUrl)

            if (contentType != null && contentType.lowercase().contains("html")) {
                val document = Jsoup.parse(content, currentUrl)
                val links = document.select("a[href]")

                for (link in links) {
                    val nextUrl = link.absUrl("href")

                    if (nextUrl.isNotBlank() && isValidUrl(nextUrl)) {
                        crawlRecursive(nextUrl, currentDepth + 1, maxDepth)
                    }
                }
            }

        } catch (e: Exception) {
            println("Eroare la accesarea URL-ului: $currentUrl")
            println("Mesaj: ${e.message}")
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            val parsed = URL(url)
            parsed.protocol == "http" || parsed.protocol == "https"
        } catch (e: Exception) {
            false
        }
    }
}
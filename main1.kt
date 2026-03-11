package ro.mike.tuiasi

import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import sun.security.ec.point.ProjectivePoint
import kotlin.coroutines.CoroutineContext

class item(val title: String,
           val link: String,
           val description: String,
           val pubDate: String,
           val imagini: MutableList<String> = mutableListOf())
{
    fun Print()
    {
        println("\tTitlu: $title") 
        println("\tLink: $link") 
        println("\tDescription: $description")
        println("\tData publicarii: $pubDate")
        println("\timagini: ")
        for (i in imagini)
        {
            println("\t\t$i")
        }
    }
}

class FEED(val title: String,
           val link: String,
           val description: String,
           val items: MutableList<item> = mutableListOf())
{
    fun Print()
    {
        println("Afisare FEED: $title") 
        println("link: $link") 
        println("Descriere: $description") 
        println("Componente:") 
        for(i in items) {
            i.Print()
            println("******")
        }
    }
}



fun main() {
    val url = "http://rss.cnn.com/rss/edition.rss" 
    try{
        val doc = Jsoup.connect(url).parser(Parser.xmlParser()).get()

        val canal = doc.selectFirst("channel")

        if (canal != null) {
            val titlu  = canal.selectFirst("title")?.text() ?: "No title"
            val link = canal.selectFirst("link")?.text() ?: "No link"
            val descriere = canal.selectFirst("description")?.text() ?: "No description"

            val MyFEED = FEED(titlu, link, descriere) 

            val elemente= canal.select("item") 
            for(element in elemente) {
                val titlu = element.selectFirst("title")?.text() ?: "No title"
                val link = element.selectFirst("link")?.text() ?: "No link"
                val descriere = element.selectFirst("description")?.text() ?: "No description"
                val pubdate = element.selectFirst("pubDate")?.text() ?: "No pubDate"

                val media = element.select("media|content")
                val MyItem = item(titlu, link, descriere, pubdate)
                for (i in media)
                {
                    val url = i.attr("url");
                    MyItem.imagini.add(url);
                }
                MyFEED.items.add(MyItem)
            }

            MyFEED.Print() 

        }


    }catch (e: Exception){
        println(e.message) 
    }


}

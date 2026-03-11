import org.jsoup.Jsoup
// ADT pentru Arbore
class TreeNode(val url: String) {
    val children = mutableListOf<TreeNode>()
}
// Funcție recursiva pentru a extrage link-uri (ordin 2)
fun extrageLinkuri(urlCurent: String, domeniu: String, nivelCurent: Int, nivelMax: Int
= 2): TreeNode {
    val nod = TreeNode(urlCurent)
    if (nivelCurent >= nivelMax) return nod
    try {
        val document = Jsoup.connect(urlCurent).get()
        val linkuriHTML = document.select("a[href]")
        for (link in linkuriHTML) {
            val href = link.absUrl("href")
            // pastrez doar link urile care apartin aceluiasi domeniu
            if (href.contains(domeniu) && href != urlCurent) {
                // Apel recursiv pentru a scana mai adanc
                nod.children.add(extrageLinkuri(href, domeniu, nivelCurent + 1,
                    nivelMax))
            }
        }
    } catch (e: Exception) {
        println("Eroare la accesarea $urlCurent: ${e.message}")
    }
    return nod
}
// guncția de serializare
fun serializeTree(node: TreeNode, indent: String = ""): String {
    var result = "$indent${node.url}\n"
    for (child in node.children) {
        result += serializeTree(child, "$indent |-- ")
    }
    return result
}
fun main() {
    val domeniuTinta = "mike.tuiasi.ro"
    val urlStart = "http://$domeniuTinta"
    println("Începem scanarea pentru $urlStart (aceasta poate dura câteva secunde)...")
    // generam arborele(conecteaza te la net, ca iar stai o ora si nu intelegi de ce nu merge)
    val rootNode = extrageLinkuri(urlStart, domeniuTinta, 0)
    val arboreSerializat = serializeTree(rootNode)
    println("\n--- Arbore Serializat ---")
    println(arboreSerializat)

}
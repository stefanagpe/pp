fun procesareEbook(textInitial: String): String {
    var textProcesat = textInitial
    val regexSpatii = Regex(" {2,}")
    val regexLiniiNoi = Regex("\n{2,}")
    val regexNumarPagina = Regex("\\s+\\d+\\s+")
textProcesat = regexSpatii.replace(textProcesat, " ")
textProcesat = regexLiniiNoi.replace(textProcesat, "\n")
textProcesat = regexNumarPagina.replace(textProcesat, "\n")
// Extra
textProcesat = textProcesat.replace("ş", "ș").replace("ţ", "ț")
return textProcesat
}
fun main() {
    val textMurdar = "Acesta este un text cu multe spatii.\n\n\nAcesta este capitolul 5.\n 67 \ntextul continua aici cu diacritice: mătrăgună."
    println("TEXT ORIGINAL")
    println(textMurdar)
    println("\nTEXT PROCESAT")
}
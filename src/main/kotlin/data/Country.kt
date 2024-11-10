package data

data class Country(
    val id: Long,
    val position: Int,
    val name: String,
    val flag: String,
    val goldMedals: Int,
    val silverMedals: Int,
    val bronzeMedals: Int

) {
    override fun toString(): String {
        return "\n\n(Posicion:$position, Pais:'$name', Medallas doradas:$goldMedals, Medallas plateadas:$silverMedals, Medallas de bronce:$bronzeMedals)"
    }
}
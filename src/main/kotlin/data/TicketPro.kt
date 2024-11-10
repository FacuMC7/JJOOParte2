package data

class TicketPro : Intermediario(){

    override fun calcularComision(monto : Double): Double = monto.times(1.02)

}
package data

import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Elite : Intermediario() {

    override fun calcularComision(monto : Double): Double {
        val horaActual = LocalTime.now()
        val inicioComisionReducida = LocalTime.of(20, 0) // 20:00 horas
        val finComisionReducida = LocalTime.of(23, 59)
        val comision = when {
            horaActual.isAfter(inicioComisionReducida) && horaActual.isBefore(finComisionReducida) -> 1.01
            else -> 1.03
        }
        return comision.times(monto)
    }

}
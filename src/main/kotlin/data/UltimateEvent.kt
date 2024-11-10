package data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class UltimateEvent : Intermediario(){

    override fun calcularComision(monto : Double): Double{

        val fechaActual = LocalDate.now()
        val diaDeLaSemana = fechaActual.dayOfWeek

        val comision = when(diaDeLaSemana){
            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> 1.03
            else -> 1.0075
        }
        return comision.times(monto)

    }

}
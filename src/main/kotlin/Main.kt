import data.*
import repositories.EventRepository
import repositories.MedalTableRepository
import repositories.PurchaseRepository
import repositories.UserRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {

    val purchaseRepo = PurchaseRepository
    val userRepo = UserRepository
    val eventoRepo = EventRepository
    val medalleroRepo = MedalTableRepository


    var usuarioActual = iniciarSesion(userRepo)

    var opcionSeleccionada : Int = 0

    do {
        try { //Con este try - catch buscamos sacarnos de encima que cuando el usuario ingrese un char o string se nos cierre el programa.

            println("---MENU JUEGOS OLIMPICOS---")
            println("0 - COMPRAR ENTRADAS")
            println("1 - HISTORIAL DE COMPRAS POR USUARIO")
            println("2 - MEDALLERO OLIMPICO")
            println("3 - Salir")
            println("Ingrese una opcion: ")

            opcionSeleccionada = readln().toInt()

            when(opcionSeleccionada) {
                0 -> comprarEntradas(usuarioActual, userRepo, eventoRepo, purchaseRepo)

                1 -> mostrarHistorialDeCompras(usuarioActual, purchaseRepo)

                2 -> mostrarMedalleroOlimpico(medalleroRepo)

                3 -> println("Saliendo...")


                else -> println("Error: Debes ingresar un numero válido")
            }
        }catch (e: NumberFormatException){
            println("Error: Debes ingresar un caracter válido")

        }

    }while (opcionSeleccionada != 3)

}

fun iniciarSesion(userRepo: UserRepository): User {

    var usuarioActual: User?

    do {
        println("Ingrese su nickname: ")
        val nickname = readln()

        println("Ingrese su password: ")
        val password = readln()

        usuarioActual = userRepo.login(nickname, password)

        if (usuarioActual == null) {
            println("Error: Contraseña o usuario equivocado")
        }
    } while (usuarioActual == null)
    return usuarioActual
}

fun comprarEntradas(usuarioActual: User, userRepo: UserRepository, eventoRepo: EventRepository, purchaseRepo: PurchaseRepository) {

    val intermediario = seleccionarIntermediario()
    val eventoActual = seleccionarEvento(eventoRepo)

    val day = LocalDate.now()
    val fecha: String = day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val asiento = seleccionarAsiento(eventoActual.id, purchaseRepo)

    var lastId = purchaseRepo.get().maxOfOrNull { it.id } ?: 0L

    val precioFinal = calcularPrecioFinal(intermediario, eventoActual.price)

    println("Datos de la compra: Precio inicial: ${eventoActual.price},Precio Final: $precioFinal ")

    if (confirmarCompra()) {
        if (usuarioActual.money > precioFinal) {
            val newPurchase = registrarCompra(usuarioActual, eventoActual, precioFinal, fecha, asiento, purchaseRepo)
            userRepo.restarPrecioAlSaldo(usuarioActual, precioFinal)
            println("La compra se realizó correctamente: $newPurchase")
        } else {
            println("No se pudo realizar la compra, saldo insuficiente")
        }
    } else {
        println("Compra cancelada.")
    }
}

fun seleccionarIntermediario(): Int{
    var indice: Int = 0
    do {
        try {

            println("Intermediarios: ")
            println("0 - TicketPro (Comision del 2%)")
            println(
                "1 - Elite (Si la compra se realiza entre las\n" +
                        "20:00 hs y las 23:59 hs aplica\n" +
                        "una comisión del 1%, sino aplica\n" +
                        "una comisión del 3%)"
            )
            println(
                "2 - UltimateEvent (Si la compra se realiza un\n" +
                        "sábado o domingo se aplica una\n" +
                        "comisión del 3%, sino aplica una\n" +
                        "comisión del 0.75%\n)"
            )
            println("Ingrese una opcion: ")

            indice = readln().toInt()

            if (indice < 0 || indice > 2) {
                println("Opcion incorrecta, introduzca una opcion valida")
            }

        }catch(e: NumberFormatException){
            println("Error: Debes ingresar un caracter válido")
        }

    } while (indice < 0 || indice > 2)
    return indice
}

fun seleccionarEvento(eventoRepo: EventRepository): Event{
    println("Eventos disponibles: ")
    println(eventoRepo.get())

    println("Ingrese el ID del evento: ")
    val idEvento = readln().toLong()
    return eventoRepo.getById(idEvento)!!
}

fun seleccionarAsiento(idEvento: Long, purchaseRepo: PurchaseRepository): String{

    var asiento: String
    var ocupado: Boolean = false

    do {
        println("Ingrese el asiento: ")

        asiento = readln().toString()

        if (asiento.isBlank()) {
            println("El campo asiento no puede estar vacío. Intente nuevamente.")
        }

        ocupado = purchaseRepo.estaOcupado(idEvento, asiento)

        if (ocupado) {
            println("Asiento ocupado")
        }

    } while (ocupado || asiento.isBlank())
    return asiento

}

fun calcularPrecioFinal(indice: Int, precioBase: Double): Double{
    return when (indice) {
        0 -> {
            TicketPro().calcularComision(precioBase)
        }

        1 -> {
            Elite().calcularComision(precioBase)
        }

        2 -> {
            UltimateEvent().calcularComision(precioBase)
        }
        else -> 0.0
    }

}

fun confirmarCompra(): Boolean {
    println("Desea realizar la compra?:")
    println("0- Si")
    println("1- No")

    val opcionSeleccionada2 = readln().toInt()
    return opcionSeleccionada2 == 0
}

fun registrarCompra(usuario: User, evento: Event, precio: Double, fecha: String, asiento: String, purchaseRepo: PurchaseRepository): Purchase {
    val lastId = purchaseRepo.get().maxOfOrNull { it.id } ?: 0
    val nuevaCompra = Purchase(lastId + 1, usuario.id, evento.id, precio, fecha, asiento)
    purchaseRepo.add(nuevaCompra)
    return nuevaCompra
}

fun mostrarHistorialDeCompras(usuarioActual: User, purchaseRepo: PurchaseRepository){

    val allpurchase = purchaseRepo.getByUserId(usuarioActual.id)
    println("Historial de compras: ")
    for (i in allpurchase) {
        println(i)
    }
}

fun mostrarMedalleroOlimpico(medalleroRepo: MedalTableRepository) {
    println("Medallero olimpico: ")
    println(medalleroRepo.get())
}

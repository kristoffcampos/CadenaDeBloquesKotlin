package CadenaDeBloquesKotlin

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
public class CDBController {

    private val cadena_de_bloques = CadenaDeBloques(
            transacciones_actuales = arrayListOf(),
            cadena = arrayListOf(),
            nodos = mutableSetOf()
    )
    private val id_nodo =  UUID.randomUUID().toString().replace("-", "")

    @RequestMapping(value= "/minar", method = arrayOf(RequestMethod.GET))
    private fun minar(): Respuesta {

        val ultimo_bloque = cadena_de_bloques.cadena.last()
        val ultima_prueba = ultimo_bloque.prueba
        // Se llama a prueba_de_trabajo() para obtener la siguiente prueba
        val prueba = cadena_de_bloques.prueba_de_trabajo(ultima_prueba)
        val hash_previo = hashear_bloque(cadena_de_bloques.cadena.last())

        // Recibe una recompensa por encontrar la prueba
        // el emisor es 0 para denotar que el nodo ha minado una nueva moneda
        cadena_de_bloques.nueva_transaccion(
                emisor = "0",
                receptor = id_nodo,  // dirección global con valor único para nodo local
                cantidad = 1.0
        )

        // Crea un nuevo bloque y lo añade a la cadena
        val nuevo_bloque = cadena_de_bloques.nuevo_bloque(prueba, hash_previo)

        return Respuesta(
                mensaje = "Un nuevo bloque ha sido forjado.",
                index = nuevo_bloque.index,
                transacciones = nuevo_bloque.transacciones,
                prueba = nuevo_bloque.prueba,
                hash = nuevo_bloque.hash_previo
        )
    }

    @RequestMapping(value= "/transacciones/nueva", method = arrayOf(RequestMethod.POST))
    private fun nueva_transaccion(@RequestBody transaccion: Transaccion): String {

        if(transaccion.emisor == ""
                || transaccion.receptor == ""
                || transaccion.cantidad == 0.0)
            return "Ingrese correctamente los datos de la transacción requeridos."

        val index = cadena_de_bloques.nueva_transaccion(
                transaccion.emisor,
                transaccion.receptor,
                transaccion.cantidad
        )

        return "La transacción será añadida al Bloque: $index"
    }

    @RequestMapping(value= "/cadena", method = arrayOf(RequestMethod.GET))
    fun obtener_cadena(): Cadena {
        return Cadena(cadena_de_bloques.cadena, cadena_de_bloques.cadena.size)
    }

    // Recordar que el @RequestBody solicita una Lista de nodos
    @RequestMapping(value="/nodos/registrar", method= arrayOf(RequestMethod.POST))
    private fun registrar_nodos(@RequestBody nodos: MutableSet<String>): String{

        if(nodos.isEmpty()){
            return "Error: Por favor ingresa una lista de nodos válida."
        }

        for (nodo in nodos){
            cadena_de_bloques.registrar_nodo(nodo)
        }

        return "Nuevos nodos han sido añadidos ${cadena_de_bloques.nodos}"
    }

    @RequestMapping(value="/nodos/resolver", method = arrayOf(RequestMethod.GET))
    private fun consenso(): String{
        val reemplazada = cadena_de_bloques.resolver_conflictos(cadena_de_bloques.nodos)
        val msj : String

        if(reemplazada) {
            msj = "Nuestra cadena fue reemplazada por la de la red ${cadena_de_bloques.cadena}"
        } else{
            msj = "Nuestra cadena es autoritativa ${cadena_de_bloques.cadena}"
        }

        return msj
    }
}
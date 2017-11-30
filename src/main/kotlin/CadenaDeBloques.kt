package CadenaDeBloquesKotlin

import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.time.Instant
import kotlin.collections.ArrayList
import com.fasterxml.jackson.module.kotlin.*

/**
 * Algoritmo simple de prueba de trabajo:
 * La función encontrará un número cuyo hash(pp') finalice en 4 ceros.
 * donde p es la prueba previa (ultima_prueba) y p' es la nueva prueba
 *
 * @param ultima_prueba: prueba previa
 * @param prueba: prueba actual
 * @return Boolean - retorna true si la prueba termina en 4 ceros, false si no
 */
private fun prueba_valida(ultima_prueba: Int, prueba: Int): Boolean{
    val adivinar: String = "%d, %s".format(ultima_prueba, prueba)
    val md = MessageDigest.getInstance("SHA-256")
    val bytes_del_hash = md.digest(adivinar.toByteArray())
    val hash_formateado: String = String.format("%032X", BigInteger(1, bytes_del_hash))

    return hash_formateado.endsWith("0000")
}

/**
 * Crea un hash del bloque de tipo SHA-256
 *
 * @param bloque: bloque al cual se realizará el hash
 * @return String - hash del bloque
 */
internal fun hashear_bloque(bloque: Bloque): String{
    val md = MessageDigest.getInstance("SHA-256")
    val bytes_del_hash = md.digest(bloque.toString().toByteArray())

    return  String.format("%032X", BigInteger(1, bytes_del_hash))
}

data class Bloque(
        val index: Int, val fecha_hora: Long,
        val transacciones: ArrayList<Transaccion>,
        var prueba: Int, val hash_previo: String
)

data class Transaccion(val emisor: String, val receptor: String, val cantidad: Double)

data class Cadena(var cadena: ArrayList<Bloque>, var largo: Int)

data class Respuesta(
        val mensaje: String,
        val index: Int,
        var transacciones: ArrayList<Transaccion>,
        val prueba: Int,
        val hash: String
)

class CadenaDeBloques(
        private var transacciones_actuales: ArrayList<Transaccion>,
        internal var cadena: ArrayList<Bloque>,
        internal var nodos: MutableSet<String>
){
    init{
        transacciones_actuales = arrayListOf()
        cadena = arrayListOf()
        nodos = mutableSetOf()

        // Crea bloque raíz
        nuevo_bloque(hash_previo = "1", prueba = 100)
    }

    /**
     * Crea una nueva transacción la cual se guardará en el próximo bloque minado
     *
     * @param emisor: quien envía la transacción
     * @param receptor: quien recibe la transacción
     * @param cantidad: cantidad enviada
     * @return retorna el index del bloque en el cual se guardará la transacción
     */
    internal fun nueva_transaccion(emisor: String, receptor: String, cantidad: Double): Int{

        transacciones_actuales.add(Transaccion(emisor, receptor, cantidad))

        return cadena.lastIndex +2
    }

    /**
     * Forja un nuevo bloque en la cadena
     *
     * @param prueba: prueba retornada por el algoritmo de prueba de trabajo
     * @param hash_previo: hash del bloque anterior
     * @return retorna el bloque creado en la cadena
     */
    internal fun nuevo_bloque(prueba: Int, hash_previo: String): Bloque {

        val nuevo_bloque = Bloque(
                cadena.size + 1,
                Instant.now().epochSecond,
                transacciones_actuales,
                prueba,
                hash_previo)

        transacciones_actuales = arrayListOf()
        cadena.add(nuevo_bloque)

        return nuevo_bloque
    }

    /**
     * Función que retorna la evidencia de la prueba de trabajo
     *
     * @param ultima_prueba: prueba previa
     * @return Int
     */
    internal fun prueba_de_trabajo(ultima_prueba: Int): Int{
        var prueba = 0

        while (!prueba_valida(ultima_prueba, prueba)){
            prueba++
        }

        return prueba
    }


    /**
     * Añade un nuevo nodo a la lista de nodos
     *
     * @param direccion: direccción del nodo (Ej: "http://127.0.0.7:8080")
     */
    internal fun registrar_nodo(direccion: String){
        val urlParseada = URL(direccion).toString()
        nodos.add(urlParseada)
    }

    /**
     * Determina la validez de una cadena
     *
     * @param cadena: una cadena de bloques
     * @return true si es válida, false si no
     */
    private fun cadena_valida(cadena: ArrayList<Bloque>): Boolean{
        var ultimo_bloque = cadena[0]
        var index_actual = 1

        while (index_actual < cadena.size){
            val bloque = cadena[index_actual]
            println("$ultimo_bloque")
            println("$bloque")
            println("\n-----------------------\n")

            if (bloque.hash_previo != hashear_bloque(ultimo_bloque)) return false

            ultimo_bloque = bloque
            index_actual++
        }
        return true
    }


    /**
     * Algoritmo de consenso que resuelve los posibles conflictos
     * reemplazando la cadena más larga de la red por la local.
     * Si la local es la más larga de la red, ésta pasa a ser autoritaria.
     *
     * @param nodos: nodos de la red
     * @return true si la cadena fue reemplazada, false si no
     */
    internal fun resolver_conflictos(nodos: MutableSet<String>): Boolean{
        val vecinos = nodos
        var largo_maximo = cadena.size
        var respuesta: Int = 0

        // Recorre los nodos de la red extrayendo sus cadenas y su tamaño
        for (nodo in vecinos){
            var nodo_en_cadena = Cadena(arrayListOf(), 0)

            val conexion = URL("$nodo/cadena").openConnection() as HttpURLConnection
            conexion.inputStream.bufferedReader().use { reader ->
                respuesta = conexion.responseCode
                val reader_input = reader.readText()
                val MAPEO = jacksonObjectMapper()
                nodo_en_cadena = MAPEO.readValue(reader_input)
            }

            if(respuesta == 200){
                val largo: Int = nodo_en_cadena.largo
                val obtener_cadena: ArrayList<Bloque> = nodo_en_cadena.cadena

                // Luego compara sus tamaños con la cadena local
                if(largo > largo_maximo && cadena_valida(obtener_cadena)){
                    largo_maximo = largo
                    var nueva_cadena = obtener_cadena
                    cadena = nueva_cadena
                    return true
                }
            }
        }
        return false
    }
}
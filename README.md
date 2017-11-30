# Cadena de Bloques en Kotlin + Spring Boot

## Descripción

Adaptación de un proyecto realizado por [Daniel van Flymen](https://github.com/dvf) en Python + Flask, que busca implementar de forma básica el funcionamiento de una cadena de bloques (Blockchain) con el lenguaje de programación Kotlin y Spring Boot. Esta tecnología es utilizada en diversas criptomonedas y sistemas descentralizados. La finalidad de este pequeño proyecto es netamente pedagógica, lo que no impide ser una base para una implemetación más completa y robusta.

#### Tecnologías

- Kotlin (version: 1.1.60)
- Spring Boot (version: 1.5.7.RELEASE)

---

## Cómo usar

#### Instalación

1. Asegúrate de tener correctamente instalado el JDK de Java (versión 1.8)

2. Opciones:

a) Importa el proyecto desde el IDE Intellij Idea (Recomendada)

b) Teniendo instalado [gradle](https://gradle.org/), navega desde la línea de comandos hacia la raíz del proyecto y ejecuta:

```
$ gradlew bootRun
```
---
## Referencias
- [¿Qué es una cadena de bloques?](https://es.wikipedia.org/wiki/Cadena_de_bloques)
- [Proyecto original realizado en Python por Daniel van Flymen](https://github.com/dvf/blockchain)

---
## Notas
- Se optó por una programación en español debido al menor número de proyectos basados en la tecnología Blockchain en este idioma, además de sumar claridad para personas hispanohablantes.
- Tener presente la naturaleza pedagógica del proyecto. Esto significa que las prioridades fueron la simplicidad y claridad. Esto supone ciertas desventajas que se ven reflejadas en los algoritmos de consenso y prueba de trabajo, entre otros.
- Para resolver de alguna manera el punto anterior se sugiere incluir el hash del bloque previo en la concatenación usada en la prueba de trabajo. Esto resolvería de alguna manera que la prueba de trabajo no esté relacionada al historial de transacciones. En la implementación actual, cualquier sujeto(nodo) de la red podría sobreescribir el historial de transacciones, calcular el hash de los bloques y reusar las pruebas de trabajo. Luego, podría simplemente minar un nuevo bloque y al mismo tiempo su cadena pasaría a ser autoritaria en la red.
- Para una implementación de prueba de trabajo más robusta se sugiere un algoritmo hashCash (utilizado en Bitcoin y spam de emails). El usuario [Akrisanov](https://github.com/akrisanov) implementa uno muy parecido en su [proyecto](https://github.com/akrisanov/blockchain-in-kotlin/blob/master/src/main/kotlin/blockchain/ProofOfWork.kt) realizado también en Kotlin.
- Se agradece de antemano cualquier aporte, crítica, corrección o comentario. La idea detrás de esto es el continuo aprendizaje. 
---

## License

MIT License

Copyright (c) [2017] [Kristoff Campos]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.





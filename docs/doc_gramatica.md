# Gramática de AVIMO

## Introducción
Es necesario procesar una entrada de texto proporcionada por el usuario, en donde este nos indique que quiere que AVIMO haga y seamos capaces de entenderlo. Por tanto para este fin se han explorado distintas opciones.

## Opciones
Se han explorado como opciones de analizador léxico y sintáctico usar JFLEX y YACC respectivamente, o bien usar JavaCC que es un generador de analizadores sintácticos (compiladores) similar a YACC, pero usa otra filosofía. El análisis lo hace de arriba a abajo de la gramática.

Como la diferencia entre ambos es como realiza el análisis no hay una diferencia importante entre usar uno y otro, y al encontrar ejemplos de como integrar JavaCC en Android Studio (algo engorrosos), he decidido explorar más esta opción.

Viendo como funcionan el analizador léxico y el sintáctico, me he ido dando cuenta poco a poco que es muy parecido a usar solamente expresiones regulares, salvo porque en la parte del análisis sintáctico te permite incluir código Java además de usar las expresiones regulares o **tokens** (símbolos que están definidos por un valor o una expresión regular). No he visto necesario el uso del código Java para el procesamiento de la entrada. Dicho esto he decidido que en lugar de complicarme en como integrar JavaCC, usar expresiones regulares propias de Java para detectar la información clave y la sintáxis de la entrada.

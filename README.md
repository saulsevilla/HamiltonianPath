# HamiltonianPath
Programa aplicando los conceptos de programación concurrente para encontrar un camino Hamiltoniano dentro de una gráfica generada aleatoriamente.

-----------------------------------
Universidad Nacional Autónoma de México

Licenciatura en Ciencia de Datos - FES Acatlán

Autor: Sevilla Gallardo Saúl Sebastián

-----------------------------------
1. [¿Qué es un camino Hamiltoniano?](#hampath)
2. [Backtracking](#backtracking)
    1. [Funcionamiento del Backtracking](#funcionamiento)
    2. [Uso para encontrar un camino Hamiltoniano](#uso)
    3. [Paralelización](#par)
1. [Tiempo secuencial contra concurrente](#tiempo-secuencial-contra-concurrente)
    1. [Consideraciones](#consideraciones)
1. [Ejecución](#ejecución)
-----------------------------------
## ¿Qué es un camino Hamiltoniano? <a name="hampath"></a>
Un camino Hamiltoniano en un grafo es un recorrido que visita cada vértice exactamente una vez. En otras palabras, es una trayectoria que pasa por todos los nodos de un grafo sin repetir ninguno. Este concepto se basa en el matemático y físico irlandés William Rowan Hamilton.

Considere el siguiente grafo

![image](https://github.com/saulsevilla/HamiltonianPath/assets/100658499/ea39fea9-8a4e-4857-b430-8708f9dbb8b7)

(Imágen obtenida de [Wikipedia](https://es.wikipedia.org/wiki/Teor%C3%ADa_de_grafos#/media/Archivo:Connexe_et_pas_connexe.svg))

Un camino Hamiltoniano para la primer gráfica podría ser: \[3, 2, 1, 4, 5\]. Nótemos que existen varios caminos Hamiltonianos, por ejemplo, también podemos tener el camino \[1, 4, 5, 2, 3\].

Sin embargo, para la segunda gráfica no es posible encontrar un camino que pase por todos los nodos, entonces decimos que no es Hamiltoniana.

[Más sobre caminos Hamiltoianos]((https://es.wikipedia.org/wiki/Camino_hamiltoniano)

## Backtracking <a name="bt"></a>

El backtracking es un enfoque algorítmico que busca soluciones a problemas de manera incremental, probando diversas opciones y retrocediendo cuando se encuentra en un callejón sin salida. Este método es especialmente útil para problemas combinatorios y de optimización.

### Funcionamiento del Backtracking <a name="funcionamiento"></a>
1. **Elección**: Se elige un nodo o una opción para avanzar.
2. **Exploración**: Se explora la solución de manera recursiva.
3. **Validación**: Se verifica si la solución parcial cumple con los requisitos.
4. **Solución**: Si se cumple la condición de parada, se obtiene la solución.
5. **Backtrack**: Si la solución parcial no es válida, se retrocede y se realiza otra elección.

### Uso para encontrar un camino Hamiltoniano <a name="uso"></a>
En el contexto de los caminos Hamiltonianos, el backtracking puede utilizarse para explorar diferentes combinaciones de vértices, buscando un camino que visite cada nodo exactamente una vez. La idea es probar diferentes caminos de manera recursiva, retrocediendo cuando se alcanza un punto muerto y probando una alternativa.

[Más sobre Backtracking](https://es.wikipedia.org/wiki/Vuelta_atr%C3%A1s)

~~~java
public boolean visit(Integer v, List<Integer> path){
        // Agregamos el nodo al camino
        path.add(v);

        // Si el camino tiene longitud de la gráfica, hemos encontrado el camino Hamiltoniano
        if(path.size() == this.graph.size()){
            this.path = path;
            return true;
        }

        for( Integer n : this.graph.get(v)){
            // por cada vecino, si alguno ya está en el camino, no hacer nada
            if( !path.contains(n) ){
                // Si no, visitar el vecino
                if(this.visit(n, path)){
                    // Si algún vecino encuentra el camino hamiltoniano, entonces hemos terminado
                    return true;
                }
            }
        }
        // Al llegar a este punto, este nodo no puede estar en el camino en esta posición
        path.remove(path.size()-1);

        // Si ningun vecino encontró un camino hamiltoniano regresamos falso
        return false;
    }
~~~
### Paralelización <a name="par"></a>
Afortunadamente el Bactracking es múy fácil de paralelizar, pues su naturaleza recursiva nos permite dividir los subprocesos en diferentes hilos. En este caso, suponiendo que la gráfica es de tamaño $n$, es decir, tiene $n$ vétrices, se usaron $n$ hilos donde cada uno inicia la búsqueda de un camino Hamiltoniano en un vértice diferente.

~~~java
// Crea un hilo por nodo
int n = this.graph.size();
Thread[] threads = new Thread[n];
BackTracker[] bt = new BackTracker[n];

for (int i =0; i<n; i++){
    // El hilo tiene un nodo de inicio para poder ser concurrente
    // Cada hilo tiene acceso a los otros para terminarlos
    bt[i] = new BackTracker(graph, Integer.valueOf(i), bt);
    threads[i] = new Thread(bt[i]);
}
~~~

Cabe mencionar que para optimizar aún más la búsqueda concurrente el hilo que encuentre primero un camino Hamiltoniano detiene al resto de hilos para que dejen de buscar.

~~~java
public void run(){
    // Visita el nodo de inicio, iniciando con un camino vacío
    if (this.visit(Integer.valueOf(this.start), new ArrayList<>())){

        // Si se encuentra el camino, terminar todos los hilos
        System.out.println("\tThread "+this.start+" is stopping all threads");

        for (BackTracker bt: pool){
            if(bt.start != this.start){ //Comprobamos que el hilo no se detenga a si mismo
                // Detener el hilo bt
                bt.stop = true;
            }
        }
        // Indica que este hilo encontró el camino H
        this.isH = true;
    }
}
~~~

## Tiempo secuencial contra concurrente
El programa concurrente mejora el rendimiento teniendo tiempos de ejecución hasta 400 veces menor que el secuencial, mientras se aumentan más la cantidad de vértices de la gráfica mejora más el rendimiento concurrente contra el secuencial.

![tiempoHP](https://github.com/saulsevilla/HamiltonianPath/assets/100658499/c48e431c-20dc-4b2c-8ffa-974ec77519ce)

En esta captura podemos ver diferentes ejecuciones del programa donde se compara el tiempo secuencial contra el tiempo concurrente, podemos notar que existen casos donde el tiempo secuencial es menor que el concurrente. La primer ejecución fue con una gráfica de tamaño 20, mientras que el resto fueron con gráficas de tamaño 30 generadas aleatoriamente.

### Consideraciones

El programa secuencial puede tardar menos que el programa concurrente cuando la gráfica es pequeña (menos de 10 vértices), esto debido al costo computacional que requiere crear hilos y juntar sus resultados con una operación *join*.

Además, cuando el camino Hamiltoniano que se encuentra primero comienza en el vértice etiquetado con 0, es decir, el primer vértice, el programa secuencial tarda unos microsegundos menos que el paralelo, pues el primer hilo encuentra el camino al mismo tiempo que lo hace el secuencial, sin embargo, tarda más porque debe detenerl el resto de hilos y como se mencionó anteriormente, juntar los resultados de los hilos.
Un ejemplo de esto se puede encontrar en la segunda ejecución, donde se muestra que el camino encontrado empieza en el nodo 0 en ambos programas.

Por último, no se recomienda probar este programa con una gráfica de tamaño mayor a 30, pues el programa secuencial puede tardar varios minutos en encontrar el camino si existe, si no existe tarda mucho más pues tiene que terminar de buscar en todos los caminos posibles.

## Ejecución
El proyecto cuenta con 3 carpetas, un archivo *"pom.xml"* y un ejecutable *jar* para el uso de la librería UnderTow.

El archivo *"pom.xml"* contiene las especificaciones para Maven, puede que sea necesario cambiar la siguiente sección, pues es la que define la versión de Java a usar.
~~~xml
<properties>
  <maven.compiler.source>11</maven.compiler.source>
  <maven.compiler.target>11</maven.compiler.target>
</properties>
~~~

Ahora, procedemos con la ejecución del programa

1. Descargue el repositorio o realice una clonación a través de Git en su consola, seleccionando el directorio de su preferencia.
1. Es requisito indispensable contar con la instalación previa de Maven en su equipo.
1. Una vez que haya clonado o descargado el repositorio, abra su consola de preferencia y sitúese en el directorio que contiene el proyecto. A continuación, ejecute el siguiente comando para compilar y construir el proyecto:
~~~
mvn
~~~
Posteriormente, ejecute el siguiente comando para ejecutar el proyecto:
~~~
mvn exec:java
~~~

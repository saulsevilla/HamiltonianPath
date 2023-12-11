import java.util.ArrayList;
import java.util.List;

public class BackTracker extends Thread {
    public List<List<Integer>> graph = new ArrayList<>();
    public List<Integer> path = new ArrayList<>();
    public BackTracker[] pool;
    public Integer start;
    public boolean stop = false;
    public boolean isH = false;

    public BackTracker(List<List<Integer>> graph, Integer start, BackTracker[] pool){
        this.graph = graph;
        this.pool = pool;
        this.start = start;
    }

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

    public boolean visit(Integer v, List<Integer> path){
        if(this.stop){
            return false;
        }
        // Agrega el nodo actual al camino
        path.add(v);
        
        // Si el camino tiene todos los nodos, hemos encontrado el camino hamiltoniano
        if(path.size() == this.graph.size()){
            this.path = path;
            return true;
        }

        
        for( Integer n : this.graph.get(v)){
            // por cada vecino, si alguno está en el camino, no hacer nada
            if( !path.contains(n) ){
                // Si no lo hemos incluído, visitar el vecino
                if(this.visit(n, path)){
                    // Si algún vecino encuentra el camino, entonces regresamos verdadero
                    return true;
                }
            }
        }
        // Al llegar a este punto, este nodo no puede estar en el camino en esta posición
        path.remove(path.size()-1);

        // Si ningun vecino encontró un camino hamiltoniano regresamos falso
        return false;
    }
}

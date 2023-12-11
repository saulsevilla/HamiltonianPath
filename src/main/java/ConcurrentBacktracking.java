import java.util.ArrayList;
import java.util.List;

public class ConcurrentBacktracking {
    public List<List<Integer>> graph = new ArrayList<>();
    public List<Integer> path = new ArrayList<>();

    public ConcurrentBacktracking(List<List<Integer>> graph){
        this.graph = graph;
    }

    public boolean hasHamiltonianPath(){

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

        // Inicia los hilos
        for(int i =0; i<n; i++){
            threads[i].start();
        }

        // Unir los hilos
        try{
            for( Thread t : threads){
                t.join();
            }
        }catch ( Exception e ){
            System.out.println("No se pudo padrino");
        }

        for( BackTracker b : bt){
            // Si algún hilo encontró el camino, imprimirlo y regresar verdadero
            if(b.isH){
                System.out.println(b.path);
                this.path = b.path;
                return true;
            }
        }
        
        // Si ningún hilo encontró el camino, es porque no existe
        return false;
    }
}

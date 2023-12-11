import java.util.ArrayList;
import java.util.List;

public class Backtracking {
    public List<List<Integer>> graph = new ArrayList<>();
    public List<Integer> path = new ArrayList<>();

    public Backtracking(List<List<Integer>> graph){
        this.graph = graph;
    }

    public boolean isHamiltonian(){
        // Busca un camino iniciando en cada nodo
        for (int i =0; i<this.graph.size(); i++){
            if (this.visit(Integer.valueOf(Integer.valueOf(i)), new ArrayList<>())){
                return true;
            }
        }
        return false;
    }

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
}

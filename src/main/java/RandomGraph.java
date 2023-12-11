import java.util.Random;
import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomGraph{

    public RandomGraph(){

    }

    public List<List<Integer>> generate(int size, int max_degree){
        List<List<Integer>> g = new ArrayList<>();
        
        // If max_degree is -1, then max_degree is the size of the graph minus one
        if(max_degree <=0){
            max_degree = size-1;
        }

        // Set the degree for each vertex
        Random rand = new Random(); // To make a random number for the degree of each vertex

        // Generate a list of vertices
        int[] nums = IntStream.range(0, size).toArray(); //int auxiliar
        List<Integer> vertices = new ArrayList<>(Arrays.stream(nums).boxed().toList());
        
        // Makes random adyacency lists for each vertex
        for(int i = 0; i<size; i++){

            // Shuffles the vertices to take a random sample
            Collections.shuffle(vertices);
            List<Integer> neighbors = new ArrayList<>();

            int deg = rand.nextInt(max_degree)+1;

            // Adds the vertices to the neighborhood of the vertex i
            for(Integer v: vertices.subList(0, deg)){
                if(v.intValue() != i){ // Doesn't allow self to self edge
                    neighbors.add(v);
                }
            }
            // Adds the neighborhood of the vertex i to the graph
            g.add(neighbors);
        }

        // Returns the graph
        return g;
    }
}

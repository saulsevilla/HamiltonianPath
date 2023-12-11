// import java.util.ArrayList;
import java.util.List;

public class ListToJSON {
    public ListToJSON(){

    }

    public String convert(List<List<Integer>> graph, List<Integer> path){
        String str = new String();

        int[] siguiente = new int[graph.size()];
        for(int i=0; i< graph.size(); i++ ){
            siguiente[i] = -1;
        }

        int n = 0;
        try{
            n = path.size();
        }catch(Exception e){

        }

        if(n!= 0){
            for(int i=0; i< graph.size()-1; i++ ){
                // siguiente[path.get(i+1).intValue()] = -1;
                siguiente[path.get(i).intValue()] = path.get(i+1).intValue();
            }
        }

        // Nodos
        str = "{\"nodes\": [";

        for(int i=0; i< graph.size(); i++ ){
            str += "{\"name\": "+(i+1)+"}, ";
        }

        // Enlaces entre nodos
        str += "], \"links\": [";

        for(int i=0; i< graph.size(); i++ ){
            for(Integer v : graph.get(i)){
                str += "{\"source\": "+ (i+1) +",\"target\": "+(v+1);

                if (v.intValue() == siguiente[i]){
                    str += ", \"color\": \"1\"";
                }else{
                    str += ", \"color\": \"0\"";
                }
                str += "}, ";
            }
        }

        str += "]}";
        return str.replaceAll(", ]", "]");
    }
}

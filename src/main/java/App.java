import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.ArrayList;
import java.util.List;

import java.lang.Integer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class App {

    private static final String HTML_FILE_PATH = "html/pagina.html"; 

    public static void main(String[] args){

        Undertow server = Undertow.builder().addHttpListener(8080, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        String message = exchange.getQueryParameters().get("mensaje") != null
                                ? exchange.getQueryParameters().get("mensaje").getFirst()
                                : null;

                        if (message != null) {

                            //Procesar mensaje
                            String responseMessage = run(message);
                            
                            // Responder al sender
                            exchange.getResponseSender().send(responseMessage);
                        } else {
                            // Serve the HTML page
                            String htmlContent = readHtmlFile();
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                            exchange.getResponseSender().send(htmlContent);
                        }
                    }
                }).build();
        server.start();
    }

    public static String run(String num){
        long start, end, stime, ctime; // Tiempos de inicio y fin

        int size = 0;

        // Intenta pasar el tamaño a entero
        try{
            size = Integer.parseInt(num);
        }catch(Exception e){
            System.err.println(num + " no se pudo convertir a entero");
            return "No es número";
        }
        
        // Comprueba que el tamaño sea positivo
        if(size < 0){
            System.err.println(num + " no es mayor a 0");
            return "No puede ser negativo";
        }

        // Crea una gráfica aleatoria para analizar
        RandomGraph g = new RandomGraph();

        // Tomamos el grado máximo como un tercio del tamaño
        // esto para evitar que sea tan fácil encontrar el camino H
        List<List<Integer>> graph = g.generate(size, size/3);
        
        // Realiza la búsqueda secuencial
        start = System.currentTimeMillis();
        System.out.println("------------------------------");
        System.out.println("Ejecutando búsqueda secuencial");
        int res = busquedaSecuencial(graph);
        end = System.currentTimeMillis();
        stime = end-start;
        

        // Realiza la búsqueda concurrente
        start = System.currentTimeMillis();
        System.out.println("------------------------------");
        System.out.println("Ejecutando búsqueda concurrente");
        List<Integer> path = busquedaConcurrente(graph);
        end = System.currentTimeMillis();
        ctime = end-start;

        // Pasa la gráfica a un JSON para ser graficada en el HTML
        ListToJSON l2Json = new ListToJSON();
        String json = l2Json.convert(graph, path);
        
        // Regresa la respuesta junto a el JSON de la gráfica
        String response = "{";

        // Si la gráfica es hamiltoniana
        response += "\"hamiltonian\": "+res;

        // Tiempos de ejecución
        response += ", \"time\": {\"secuential\": " + stime + ", \"concurrent\":" + ctime + "}";
        // response += ", \"performance\" : \"" +(float)stime/ctime+"\" ";
        response += ", \"performance\" : " +(float)stime/ctime;

        // Gráfica
        response += ", \"graph\": "+json+"}";
        
        return response;
    }

    public static List<Integer> busquedaConcurrente(List<List<Integer>> graph){
        
        // Realiza la búsqueda concurrente
        ConcurrentBacktracking cb = new ConcurrentBacktracking(graph);

        List<Integer> result = new ArrayList<>();

        // Revisa si tiene un camino Hamiltoniano
        if(cb.hasHamiltonianPath()){
            System.out.println("La gráfica es Hamiltoniana");
            result = cb.path;
        }else{
            System.out.println("La gráfica NO es Hamiltoniana :(");
        }

        return result;
    }

    public static int busquedaSecuencial(List<List<Integer>> graph){

        // Realiza la búsqueda secuencial
        Backtracking bt = new Backtracking(graph);

        int result = 0;

        // Revisa si se tiene un camino Hamiltoniano
        if(bt.isHamiltonian()){
            System.out.println("La gráfica es Hamiltoniana");
            System.out.println(bt.path); // Imprime el camino hamiltoniano
            result = 1;
        }else{
            System.out.println("La gráfica NO es Hamiltoniana :(");
        }

        return result;
    }

    private static String readHtmlFile() {
        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream(HTML_FILE_PATH)) {
            if (inputStream != null) {
                try (Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("/A")) {
                    return scanner.hasNext() ? scanner.next() : "";
                }
            } else {
                return "HTML file not found";
            }
        } catch (IOException e) {
            // Handle the IOException or log the exception
            e.printStackTrace();
            return "Error leyendo archivo HTML principal: debe estar en src/resources/html";
        }
    }
}

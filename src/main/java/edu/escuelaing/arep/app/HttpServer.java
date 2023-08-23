package edu.escuelaing.arep.app;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class HttpServer {

    private static HttpConnection httpConnection = new HttpConnection();
    private static HashMap<String, String> cache = new HashMap<String, String>();

    /**
     * Método principal para iniciar el servidor HTTP.
     *
     * @throws IOException Si ocurre un error al configurar o aceptar conexiones.
     */
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Socket clientSocket = null;
        while (!serverSocket.isClosed()) {
            try {
                System.out.println("Operando Buscador de Películas ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;
            boolean firstLine = true;
            String uriString = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (firstLine) {
                    firstLine = false;
                    uriString = inputLine.split(" ")[1];
                }
                if (!in.ready()) {
                    break;
                }
            }
            System.out.println("URI: " + uriString);
            if (uriString.split("=").length > 1) {
                outputLine = getHello(uriString.split("=")[1]);
            } else if (uriString.startsWith("/movie")) {
                outputLine = "";
            } else {
                outputLine = getIndexResponse();
            }
            out.println(outputLine);
            out.close();
            in.close();
        }
        clientSocket.close();
        serverSocket.close();
    }


    /**
     * Obtiene información de una película desde el caché o desde el servicio externo.
     *
     * @param movieTitle Título de la película a buscar.
     * @return Respuesta HTTP con la información de la película.
     * @throws IOException Si ocurre un error al obtener la información.
     */
    private static String getHello(String movieTitle) throws IOException {
        if (cache.containsKey(movieTitle)) {
            return "HTTP/1.1 200 OK"
                    + "Content-Type: text/html\r\n"
                    + "\r\n" + cache.get(movieTitle);
        } else {
            String movieData = httpConnection.getMovieData(movieTitle);
            cache.put(movieTitle, formatMovieData(movieData));
            return "HTTP/1.1 200 OK"
                    + "Content-Type: text/html\r\n"
                    + "\r\n" + formatMovieData(movieData);
        }
    }
    /**
     * Retorna una página HTML con el formulario de búsqueda de películas.
     *
     * @return Respuesta HTTP con la página de inicio.
     */

    private static String formatMovieData(String movieData) {
        JsonObject jsonObject = JsonParser.parseString(movieData).getAsJsonObject();

        String title = jsonObject.get("Title").getAsString();
        String year = jsonObject.get("Year").getAsString();
        String director = jsonObject.get("Director").getAsString();
        String actors = jsonObject.get("Actors").getAsString();
        String plot = jsonObject.get("Plot").getAsString();
        String posterUrl = jsonObject.get("Poster").getAsString();

        return "<h2>Movie information</h2>"
                + "<p><strong>Title:</strong> " + title + "</p>"
                + "<p><strong>Year:</strong> " + year + "</p>"
                + "<p><strong>Director:</strong> " + director + "</p>"
                + "<p><strong>Actors:</strong> " + actors + "</p>"
                + "<p><strong>Plot:</strong> " + plot+ "</p>"
                + "<img src=\"" + posterUrl + "\" alt=\"Póster de la película\">";
    }

    public static String getIndexResponse() {
        String response = "HTTP/1.1 200 OK"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Buscador de Películas</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>\n" +
                "Movie Search</h1>\n" +
                "        <div>\n" +
                "            <form action=\"/hello\">\n" +
                "                <div><label for=\"name\">Movie:</label><br>\n" +
                "                <input type=\"text\" id=\"name\" name=\"name\"></div><br><br>\n" +
                "                <input type=\"button\" value=\"Search\" onclick=\"loadGetMsg()\">\n" +
                "            </form>\n" +
                "            <div id=\"getrespmsg\"></div>\n" +
                "        </div>\n" +
                "\n" +
                "        <script>\n" +
                "            function loadGetMsg() {\n" +
                "                let nameVar = document.getElementById(\"name\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/movie?title=\"+nameVar);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "            document.getElementById(\"name\").addEventListener(\"keydown\", function(event) {\n" +
                "                if (event.key === \"Enter\") {\n" +
                "                    event.preventDefault();\n" +
                "                    loadGetMsg();\n" +
                "                }\n" +
                "            });\n" +
                "        </script>\n" +
                "    </body>\n" +
                "</html>";
        return response;
    }

}

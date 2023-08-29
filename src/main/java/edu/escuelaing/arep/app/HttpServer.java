package edu.escuelaing.arep.app;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpServer {

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
                System.out.println("Operando APLICACIONES DISTRIBUIDAS EN INTERNET ...");
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
            String responseBody = "";

            if (uriString != null && uriString.equals("/")) {
                responseBody = getIndexResponse();
                outputLine = getResponse(responseBody);
            } else if (uriString != null && !getFile(uriString).equals("Not Found")) {
                responseBody = getFile(uriString);
                outputLine = getResponse(responseBody);
            } else if (uriString != null && uriString.split("\\.")[1].equals("jpg") ||
                    uriString.split("\\.")[1].equals("png")) {
                OutputStream outputStream = clientSocket.getOutputStream();
                File file = new File("src/main/resources/public/" + uriString);
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                    ImageIO.write(bufferedImage, uriString.split("\\.")[1], byteArrayOutputStream);
                    outputLine = getImageResponseHeader("");
                    dataOutputStream.writeBytes(outputLine);
                    dataOutputStream.write(byteArrayOutputStream.toByteArray());
                    System.out.println(outputLine);
                } catch (IOException e) {
                    e.printStackTrace();
                    responseBody = getFile(uriString);
                    outputLine = getResponse(responseBody);
                }
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
     * Método para obtener un archivo estático
     *
     * @param route String de la ruta para buscar fichero
     * @return los datos del fichero en un String
     */
    public static String getFile(String route) {
        Path file = FileSystems.getDefault().getPath("src/main/resources/public", route);
        String web = "";
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                web += line + "\n";
            }
        } catch (IOException x) {
            web = "Not Found";
        }
        return web;
    }

    private static String getIndexResponse() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>APLICACIONES DISTRIBUIDAS EN INTERNET</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>TALLER DISENO Y ESTRUCTURACION DE APLICACIONES DISTRIBUIDAS EN INTERNET</h1>\n" +
                "        <form id=\"redirectForm\">\n" +
                "            <input type=\"text\" id=\"urlInput\" placeholder=\"Introduce la ruta\">\n" +
                "            <button type=\"button\" onclick=\"redirectToURL()\">Buscar</button>\n" +
                "        </form>\n" +
                "        <script>\n" +
                "            function redirectToURL() {\n" +
                "                var url = document.getElementById(\"urlInput\").value;\n" +
                "                window.location.href = url;\n" +
                "            }\n" +
                "        </script>\n" +
                "    </body>\n" +
                "</html>";
    }

    private static String getResponse(String responseBody) {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                responseBody;
    }

    private static String getImageResponseHeader(String responseBody) {
        System.out.println("response Body" + responseBody);
        return "HTTP/1.1 200 OK \r\n"
                + "Content-Type: image/jpg \r\n"
                + "\r\n";

    }
}

package edu.escuelaing.arep.app.controller;

import edu.escuelaing.arep.app.controller.componentes.Component;
import edu.escuelaing.arep.app.controller.componentes.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component

public class WebController {

    @RequestMapping(value = "/index")
    public static String index() {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-type: text/html\r\n" +
                "\r\n" + body("taller.html");
    }

    private static String body(String extention){
        byte[] content = new byte[0];
        try {
            Path file = Paths.get("src/main/resources/"+extention);
            content = Files.readAllBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(content);
    }

    @RequestMapping(value = "/style")
    public static String indexcss() {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-type: text/css\r\n" +
                "\r\n" + body("style.css");
    }

    @RequestMapping(value = "/main")
    public static String indexjs() {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-type: text/javascript\r\n" +
                "\r\n" + body("javascript.js");
    }


}

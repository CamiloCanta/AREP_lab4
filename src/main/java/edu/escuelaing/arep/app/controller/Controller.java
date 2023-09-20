package edu.escuelaing.arep.app.controller;


import edu.escuelaing.arep.app.controller.componentes.RequestMapping;

public class Controller {
    @RequestMapping(value = "/hello")
    public static String index() {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-type: text/html\r\n" +
                "\r\n" + "Hola, este es el laboratorio 4 de AREP!";
    }

}
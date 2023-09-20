package edu.escuelaing.arep.app.webApps;

import edu.escuelaing.arep.app.HttpServer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {
    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        HttpServer server = HttpServer.getInstance();
        server.run(args);
    }
}

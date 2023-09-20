package edu.escuelaing.arep.app;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    public static ConcurrentHashMap<String,String> movies = new ConcurrentHashMap<>();


    public static String inMemory(String titulo) throws IOException {
        String n="";
        if (movies.containsKey(titulo)){
            n += movies.get(titulo);
        }else{
            n += HttpClient.consultarInfo(titulo);
            movies.put(titulo,n);
        }
        return n;
    }
}
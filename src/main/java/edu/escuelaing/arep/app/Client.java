package edu.escuelaing.arep.app;

import java.io.IOException;

public class Client {

    public static void main(String[] args) {
        int numThreads = 10;

        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    testServerFunctionality();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }

    public static void testServerFunctionality() throws IOException {
        HttpConnection httpConnection = new HttpConnection();

        String[] movieTitles = {"Wall e", "The matrix", "Inception"};

        for (String movieTitle : movieTitles) {
            String response = httpConnection.getMovieData(movieTitle);
            System.out.println("Movie: " + movieTitle);
            System.out.println(response);
            System.out.println();
        }
    }
}


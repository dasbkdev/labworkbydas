package server;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = new HttpServer(8080);
        server.start();
        System.out.println("Server started: http://localhost:8080");
    }
}
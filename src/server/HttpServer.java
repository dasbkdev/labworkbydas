package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> {
                try {
                    new RequestHandler(socket).handle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
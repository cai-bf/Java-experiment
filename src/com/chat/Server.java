package com.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {
    private int port;
    private ArrayList<PrintWriter> clientOutputStream;

    public static void main(String[] args){
        Server server = new Server(5557);
        server.start();
    }

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        clientOutputStream = new ArrayList<>();
        try {

            Thread serverThread = new Thread(()->{
                    ServerSocket serverSocket;
                    try {
                        serverSocket = new ServerSocket(port);
                        while (true) {
                            Socket clientSocket = serverSocket.accept();
                            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                            clientOutputStream.add(writer);
                            Thread t = new Thread(new ClientHandler(clientSocket));
                            t.start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            });
            serverThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket socket;

        public ClientHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader ireader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(ireader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String msg;
            try {
                while ((msg = reader.readLine()) != null) {
                    // System.out.println(msg);
                    broadcast(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void broadcast(String msg) {
            Iterator<PrintWriter> it = clientOutputStream.iterator();
            while (it.hasNext()){
                try {
                    PrintWriter writer = it.next();
                    writer.println(msg);
                    writer.flush();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}

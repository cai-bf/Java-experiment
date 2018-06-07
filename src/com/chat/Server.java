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
        Server server = new Server(5560);
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
                        // 监听端口，接受客户端请求
                        while (true) {
                            Socket clientSocket = serverSocket.accept();
                            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                            // 将writer放进ArrayList
                            clientOutputStream.add(writer);
                            // 开启线程
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

    //继承runnable, 处理客户端请求
    private class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket socket;

        public ClientHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader ireader = new InputStreamReader(socket.getInputStream(), "UTF-8");
                reader = new BufferedReader(ireader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String msg;
            try {
                SensitiveWord sen = new SensitiveWord("此处填入敏感词文件"); // 文件按每个敏感词占一行的格式
                while ((msg = reader.readLine()) != null) {

                    StringBuilder temp = new StringBuilder(msg);
                    msg = sen.filter(temp);
                    broadcast(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 广播消息.
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

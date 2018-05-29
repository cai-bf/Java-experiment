package com.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private JFrame clientFrame;
    private JLabel IPLabel;
    private JLabel portLabel;
    private JLabel msgLabel;
    private JLabel nicknameLabel;
    private JTextField IPText;
    private JTextField portText;
    private JTextField msgText;
    private JTextField nicknameText;
    private JButton connectButton;
    private JButton nicknameButton;
    private JButton sendButton;
    private JPanel jPanelnorth;
    private JPanel jPanelsouth;
    private JPanel jPanelsouth1;
    private JPanel jPanelsouth2;
    private JTextArea clientTextArea;
    private JScrollPane scrollPane;
    private BufferedReader reader;
    private PrintWriter writer;
    private String nickname;

    public static void main(String[] args){
        Client client = new Client();
        client.start();
    }

    public Client(){
        clientFrame =  new JFrame();
        jPanelnorth = new JPanel();
        IPLabel = new JLabel("服务器IP", JLabel.LEFT);
        IPText = new JTextField(10);
        portLabel = new JLabel("服务器端口", JLabel.LEFT);
        portText = new JTextField(10);
        connectButton = new JButton("连接");
        clientTextArea = new JTextArea();
        scrollPane = new JScrollPane(clientTextArea);
        jPanelsouth = new JPanel();
        jPanelsouth1 = new JPanel();
        jPanelsouth2 = new JPanel();
        nicknameLabel = new JLabel("昵称", JLabel.LEFT);
        nicknameText = new JTextField(30);
        nicknameButton = new JButton("确认");
        msgLabel = new JLabel("消息", JLabel.LEFT);
        msgText = new JTextField(30);
        sendButton = new JButton("确认");
    }

    private void setGUI(){
        // 设置窗口
        clientFrame.setTitle("简易聊天室");
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientFrame.setSize(600, 600);

        // 设置北区组件
        jPanelnorth.add(IPLabel);
        jPanelnorth.add(IPText);
        jPanelnorth.add(portLabel);
        jPanelnorth.add(portText);
        jPanelnorth.add(connectButton);
        clientFrame.getContentPane().add(BorderLayout.NORTH, jPanelnorth);

        // 设置中间组件
        clientTextArea.setFocusable(false);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        clientFrame.getContentPane().add(BorderLayout.CENTER, scrollPane);

        // 设置南区组件
        jPanelsouth1.add(nicknameLabel);
        jPanelsouth1.add(nicknameText);
        jPanelsouth1.add(nicknameButton);
        jPanelsouth2.add(msgLabel);
        jPanelsouth2.add(msgText);
        jPanelsouth2.add(sendButton);
        jPanelsouth.setLayout(new BoxLayout(jPanelsouth, BoxLayout.Y_AXIS));
        jPanelsouth.add(jPanelsouth1);
        jPanelsouth.add(jPanelsouth2);
        clientFrame.getContentPane().add(BorderLayout.SOUTH, jPanelsouth);

        // 设置窗口可见
        clientFrame.setVisible(true);
    }

    // 监听connect, 连接服务器
    private void listenIP(){
        connectButton.addActionListener((ActionEvent e)->{
                String ip = IPText.getText();
                String port = portText.getText();

                if (ip.equals("") || port.equals("")){
                    JOptionPane.showMessageDialog(clientFrame, "IP or Port wrong");
                } else {
                    try {
                        Socket clientSocket = new Socket(ip, Integer.parseInt(port));
                        InputStreamReader streamReader = new InputStreamReader(clientSocket.getInputStream());

                        reader = new BufferedReader(streamReader);
                        writer = new PrintWriter(clientSocket.getOutputStream());

                        clientTextArea.append("服务器连接成功...\n");

                        Thread thread = new Thread(()->{
                            String message;
                            try {
                                while ((message = reader.readLine()) != null){
                                    clientTextArea.append(message + "\n");
                                }
                            } catch (IOException ex){
                                ex.printStackTrace();
                            }
                        });
                        thread.start();
                    } catch (IOException es){
                        JOptionPane.showMessageDialog(clientFrame, "连接服务器失败, 请检查IP及端口是否正确");
                    }
                }
        });

    }

    // 监听nickname, 设置昵称
    private void listenNickname(){
        ActionListener nicknameListener = (ActionEvent e)->{
                String name = nicknameText.getText();
                if (!name.equals("")){
                    nickname = name;
                } else
                    JOptionPane.showMessageDialog(clientFrame, "请输入昵称");
        };
        nicknameButton.addActionListener(nicknameListener);
        nicknameText.addActionListener(nicknameListener);
        nicknameText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                String name = nicknameText.getText();
                if (!name.equals("")){
                    nickname = name;
                }
            }
        });

    }

    // 监听send按钮，　往服务器传信息
    private void listenSend(){
        ActionListener sendListener = (ActionEvent event)->{

                String msg = msgText.getText();
                if (msg.equals("")){
                    JOptionPane.showMessageDialog(clientFrame, "内容不能为空");
                } else {
                    try {
                        writer.println(nickname + ": " + msg);
                        writer.flush();
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                    msgText.setText("");
                }

        };
        sendButton.addActionListener(sendListener);
        msgText.addActionListener(sendListener);
    }

    public void start(){
        setGUI();
        listenIP();
        listenNickname();
        listenSend();
    }
}

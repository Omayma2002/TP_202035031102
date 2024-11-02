package ip_package;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


import java.net.URI;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;

public class Server_Interface extends JFrame {
    private JTextField serverIPField, serverPortField;
    private JTextField responseInputField;
    private JTextArea outputArea;
    private JButton startButton, sendButton;
    private DatagramSocket serverSocket;
    private InetAddress clientAddress;
    private int clientPort;

    public Server_Interface() {
        setTitle("Server Chat");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel serverIpLabel = new JLabel("Server IP Address:");
        serverIpLabel.setBounds(20, 20, 200, 25);
        add(serverIpLabel);
        serverIPField = new JTextField("127.0.0.1");
        serverIPField.setBounds(150, 20, 150, 25);
        add(serverIPField);

        JLabel serverPortLabel = new JLabel("Server Port:");
        serverPortLabel.setBounds(320, 20, 100, 25);
        add(serverPortLabel);
        serverPortField = new JTextField("1234");
        serverPortField.setBounds(400, 20, 100, 25);
        add(serverPortField);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(20, 60, 480, 300);
        add(scrollPane);

        startButton = new JButton("Start Server");
        startButton.setBounds(20, 380, 120, 25);
        add(startButton);

        JLabel responseLabel = new JLabel("Response:");
        responseLabel.setBounds(20, 420, 100, 25);
        add(responseLabel);
        responseInputField = new JTextField();
        responseInputField.setBounds(80, 420, 320, 25);
        add(responseInputField);

        sendButton = new JButton("Send");
        sendButton.setBounds(420, 420, 80, 25);
        add(sendButton);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendResponse(responseInputField.getText());
            }
        });
    }

    private void startServer() {
        try {
            String serverIP = serverIPField.getText();
            int serverPort = Integer.parseInt(serverPortField.getText());
            serverSocket = new DatagramSocket(serverPort, InetAddress.getByName(serverIP));
            outputArea.append("Server started on " + serverIP + ":" + serverPort + "\n");

            new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (true) {
                    try {
                        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                        serverSocket.receive(receivePacket);

                        clientAddress = receivePacket.getAddress();
                        clientPort = receivePacket.getPort();
                        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        outputArea.append("Received from client: " + receivedMessage + "\n");

                        if (isValidURL(receivedMessage)) {
                            openURL(receivedMessage);
                        } else if (receivedMessage.equalsIgnoreCase("screenshot")) {
                            captureScreen();
                        } else if (receivedMessage.equalsIgnoreCase("close")) {
                            outputArea.append("Client requested to close the connection.\n");
                        }

                    } catch (Exception ex) {
                        outputArea.append("Error receiving data: " + ex.getMessage() + "\n");
                        break;
                    }
                }

            }).start();
        } catch (Exception ex) {
            outputArea.append("Error starting server: " + ex.getMessage() + "\n");
        }
    }


    private boolean isValidURL(String message) {
        return message.startsWith("http://") || message.startsWith("https://") || message.matches("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    private void openURL(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
                outputArea.append("Opened URL in browser: " + url + "\n");
            } else {
                outputArea.append("Error opening URL.\n");
            }
        } catch (Exception e) {
            outputArea.append("Error opening URL: " + e.getMessage() + "\n");
        }
    }


    private void sendResponse(String response) {
        if (clientAddress != null && clientPort > 0) {
            try {
                byte[] buffer = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
                outputArea.append("Sent to client: " + response + "\n");
            } catch (Exception e) {
                outputArea.append("Error sending response: " + e.getMessage() + "\n");
            }
        } else {
            outputArea.append("No client connected to respond to.\n");
        }
    }
    
    private void captureScreen() {
        try {

        } catch (Exception e) {
            outputArea.append("Error : " + e.getMessage() + "\n");
        }
    }
}

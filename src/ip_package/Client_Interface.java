package ip_package;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client_Interface extends JFrame{
    private JTextField clientIPField, clientPortField;
    private JTextField serverIPField, serverPortField;
    private JTextField msgInputField, replyInputField;
    private JTextArea outputArea;
    private JButton sendButton, replyButton;

    public Client_Interface() {
        setTitle("Client Chat");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel clientIpLabel = new JLabel("Client IP Address:");
        clientIpLabel.setBounds(20, 20, 200, 25);
        add(clientIpLabel);
        clientIPField = new JTextField("127.0.0.1");
        clientIPField.setBounds(150, 20, 150, 25);
        add(clientIPField);

        JLabel clientPortLabel = new JLabel("Client Port:");
        clientPortLabel.setBounds(320, 20, 100, 25);
        add(clientPortLabel);
        clientPortField = new JTextField("1235");
        clientPortField.setBounds(400, 20, 100, 25);
        add(clientPortField);

        JLabel serverIpLabel = new JLabel("Server IP Address:");
        serverIpLabel.setBounds(20, 60, 200, 25);
        add(serverIpLabel);
        serverIPField = new JTextField("127.0.0.1");
        serverIPField.setBounds(150, 60, 150, 25);
        add(serverIPField);

        JLabel serverPortLabel = new JLabel("Server Port:");
        serverPortLabel.setBounds(320, 60, 100, 25);
        add(serverPortLabel);
        serverPortField = new JTextField("1234");
        serverPortField.setBounds(400, 60, 100, 25);
        add(serverPortField);

        JLabel msgLabel = new JLabel("Message:");
        msgLabel.setBounds(20, 100, 200, 25);
        add(msgLabel);
        msgInputField = new JTextField();
        msgInputField.setBounds(20, 130, 400, 25);
        add(msgInputField);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(20, 170, 480, 150);
        add(scrollPane);

        sendButton = new JButton("Send");
        sendButton.setBounds(440, 130, 80, 25);
        add(sendButton);

        /*JLabel replyLabel = new JLabel("Reply:");
        replyLabel.setBounds(20, 330, 100, 25);
        add(replyLabel);
        replyInputField = new JTextField();
        replyInputField.setBounds(20, 360, 400, 25);
        add(replyInputField);

        replyButton = new JButton("Reply");
        replyButton.setBounds(440, 360, 80, 25);
        add(replyButton);*/

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(msgInputField.getText());
            }
        });

       /* replyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(replyInputField.getText());
            }
        });*/
    }

    private void sendMessage(String message) {
        try {
            String clientIP = clientIPField.getText();
            int clientPort = Integer.parseInt(clientPortField.getText());
            String serverIP = serverIPField.getText();
            int serverPort = Integer.parseInt(serverPortField.getText());

            try (DatagramSocket socket = new DatagramSocket(clientPort, InetAddress.getByName(clientIP))) {

                byte[] buffer = message.getBytes();
                InetAddress serverAddress = InetAddress.getByName(serverIP);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
                socket.send(packet);
                outputArea.append("Sent to server: " + message + "\n");

                buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String response = new String(packet.getData(), 0, packet.getLength());
                outputArea.append("Received from server: " + response + "\n");

                if (message.equalsIgnoreCase("close")) {
                    outputArea.append("Closing client interface.\n");
                    dispose(); // Close the interface
                }

            } catch (Exception ex) {
                outputArea.append("Error: " + ex.getMessage() + "\n");
            }
        } catch (NumberFormatException ex) {
            outputArea.append("Error: Invalid port number\n");
        }
    }


}

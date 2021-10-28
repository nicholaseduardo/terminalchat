/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifms.terminalchat;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author santos
 */
public class ClientThread {

    private Socket socket;

    public ClientThread(String host, int port) throws IOException {
        /**
         * Conecta-se ao servidor
         */
        socket = new Socket(host, port);
    }

    private void listenServerMessages() {
        /**
         * Cria uma Thread para receber as mensagens do servidor
         */
        TrataServidor ts = new TrataServidor();
        Thread t = new Thread(ts);
        t.start();
    }

    public void init() throws IOException {
        listenServerMessages();
        Scanner teclado = new Scanner(System.in);
        /**
         * Prepara para iniciar a transmissão das mensagens
         */
        String mensagem = "";
        PrintStream ps = new PrintStream(socket.getOutputStream());
        while (!"sair".equals(mensagem)) {
            System.out.println("[Input Message :>]");
            mensagem = teclado.nextLine();
            ps.println(mensagem);
        }
        ps.close();
        teclado.close();
    }

    private class TrataServidor implements Runnable {

        @Override
        public void run() {
            try {
                Scanner scanner = new Scanner(socket.getInputStream());
                while (scanner.hasNextLine()) {
                    String msg = scanner.nextLine();
                    System.out.printf("%s\n", msg);
                }
            } catch (IOException ex) {
                System.err.printf("\n---------\n[Exceção] %s\n--------\n",
                        ex.getMessage());
            }
        }

    }

    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
//        System.out.println("Server Host: ");
        String host = "localhost";
//        System.out.println("Port: ");
        int port = 12345;
        try {
            ClientThread ct = new ClientThread(host, port);
            ct.init();
        } catch (IOException ex) {
            System.err.printf("\n---------\n[Exceção] %s\n--------\n",
                    ex.getMessage());
        }
    }
}

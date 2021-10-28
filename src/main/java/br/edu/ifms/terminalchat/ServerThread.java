/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifms.terminalchat;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author santos
 */
public class ServerThread extends Thread {

    private ServerSocket serverSocket;
    private List<Socket> clientList;
    private static boolean connected = false;
    
    public ServerThread() throws IOException {
        clientList = new ArrayList();
        serverSocket = new ServerSocket(12345);
    }
    
    public void stopServer() {
        connected = false;
    }
    
    public void close() throws IOException {
        serverSocket.close();
    }

    @Override
    public void run() {
        try {
            connected = true;
            init();
        } catch (IOException ex) {
            System.err.printf("\n---------\n[Run Exceção] %s\n--------\n",
                        ex.getMessage());
        }
    }
    
    public void init() throws IOException {
        System.out.println("---- **** Server iniciado **** ----");
        while (connected) {
            // Aguarda a conexão do cliente
            Socket socket = serverSocket.accept();
            // Adiciona o cliente na lista
            clientList.add(socket);
            /**
             * Se prepara para escutar os comandos do cliente
             * por meio de uma classe que implementa Thread
             * Neste caso, cada Thread representará um cliente conectado
             */
            TrataCliente tc = new TrataCliente(socket);
            Thread t = new Thread(tc);
            t.start();
        }
    }
    
    private class TrataCliente implements Runnable {
        
        private Socket socket;
        
        public TrataCliente(Socket socket) {
            this.socket = socket;
        }
        
        private void sendMessage(String msg) throws IOException {
            for(Socket client : clientList) {
                PrintStream ps = new PrintStream(client.getOutputStream());
                ps.println(msg);
            }
        }
        
        private void stopClients() throws IOException {
            for(Socket client : clientList) {
                PrintStream ps = new PrintStream(client.getOutputStream());
                ps.println("[Server Message] Servidor te desconectou!");
                ps.close();
                client.close();
            }
        }

        @Override
        public void run() {
            try {
                String host = socket.getInetAddress().getHostAddress();
                /**
                 * Essa Thread ficara sempre lendo as informações
                 * que o cliente enviar
                 */
                Scanner scanner = new Scanner(socket.getInputStream());
                while(scanner.hasNextLine()) {
                    String msg = scanner.nextLine();
                    /**
                     * Escreve na tela do servidor a mensagem enviada
                     * pelo cliente
                     */
                    System.out.printf("[Origem: %s] %s\n", host, msg);
                    /**
                     * Distribui as mensagens para os clientes conectados,
                     * inclusive o próprio
                     */
                    sendMessage(String.format("[Origem: %s] %s\n", host, msg));
                    /**
                     * Caso o servidor tenha pedido para parar,
                     * finaliza a comunicação com o cliente
                     */
                    if (!connected) {
                        break;
                    }
                }
                stopClients();
            } catch (IOException ex) {
                System.err.printf("\n---------\n[Thread Exceção] %s\n--------\n",
                        ex.getMessage());
            }
        }   
    }
    
    public static void main(String args[]) {
        try {
            Scanner s = new Scanner(System.in);
            ServerThread server = new ServerThread();
            boolean repeat = true;
            while(repeat) {
                System.out.println("1. Iniciar Servidor");
                System.out.println("2. Parar Servidor");
                System.out.println("3. Sair");
                System.out.println("----------");
                System.out.println("Opção: ");
                int op = s.nextInt();
                s.nextLine();
                switch(op) {
                    case 1: server.start(); break;
                    case 2: server.stopServer(); break;
                    case 3:
                        server.close();
                        repeat = false;
                }
                System.out.println("");
            }
            s.close();
        } catch (IOException ex) {
            System.err.printf("\n---------\n[Main Exceção] %s\n--------\n",
                        ex.getMessage());
        }
        
    } 
}

package br.edu.ifms.terminalchat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Estudante
 */
public class ServerChat {

    private ServerSocket serverSocket;
    private List<Socket> listaDeClientesConectados;
    private int numberOfConnections;

    /**
     * Construtor da classe. Método responsável por iniciar o servidor em uma
     * determinada porta e permitir um número limitado de clientes.
     *
     * @param port
     * @param numberOfConnections
     * @throws IOException
     */
    public ServerChat(int port, int numberOfConnections) throws IOException {
        this.numberOfConnections = numberOfConnections;
        listaDeClientesConectados = new ArrayList();
        serverSocket = new ServerSocket(port);
        System.out.printf("Porta aberta: %d\n", port);

        System.out.println("Lista das interfaces de conexão do servidor");
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            System.out.println("    " + intf.getName() + " " + intf.getDisplayName());
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                System.out.println("        " + enumIpAddr.nextElement().toString());
            }
        }
    }

    /**
     * Escuta de conexões. Método utilizado para aguardar as conexões que serão
     * realizadas pelos clientes. <br/>
     * O servidor aceitará somente o número de conexões determinado no atributo
     * <b>numberOfConnections</b>.
     *
     * @throws IOException
     */
    public void listenConnetions() throws IOException {
        int count = 0;
        while (count < numberOfConnections) {
            System.out.println("Aguardando conexão ...");
            Socket clientSocket = serverSocket.accept();

            System.out.printf("Cliente %s conectado.\n", clientSocket.getInetAddress().getHostAddress());
            listaDeClientesConectados.add(clientSocket);

            System.out.printf("%s adicionado na lista. \n",
                    clientSocket.getInetAddress().getHostAddress());
            enviarMensagem(clientSocket.getOutputStream(), "-- Aguarde --");
            count++;
        }
    }

    /**
     * Inicialização do chat. Método responsável por verificar a demanada de
     * envio de mensagem por cliente conectado. Aqui, o servidor entra em
     * contato com cada cliente para informar o que ele deve fazer.
     *
     * @throws java.io.IOException
     */
    public void initChat() throws IOException {
        for (Socket client : listaDeClientesConectados) {
            /**
             * Notificar o cliente informando que ele tem a vez para enviar uma
             * mensagem
             */
            OutputStream os = client.getOutputStream();
            enviarMensagem(os, "-- Pode enviar uma mensagem --", Boolean.TRUE);

            /**
             * Captura da mensagem enviada pelo cliente
             */
            InputStream is = client.getInputStream();
            Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine()) {
                String c = scanner.nextLine();
                if (!"<END>".equals(c)) {
                    String hostaddress = client.getInetAddress().getHostAddress();
                    for (int i = 0; i < listaDeClientesConectados.size(); i++) {
                        Socket socket = listaDeClientesConectados.get(i);
                        /**
                         * Verifica se o cliente quer sair do chat
                         */
                        if (c.equals("sair")
                                && socket.getInetAddress().getHostAddress().equals(hostaddress)) {
                            /**
                             * Localiza o cliente para excluí-lo da lista e
                             * encerrar a sua conexão
                             */
                            System.out.printf("Conexão com %s foi encerrada a pedido do cliente.",
                                    hostaddress);
                            socket.close();
                            client.close();
                            listaDeClientesConectados.remove(i);
                            break;
                        } else {
                            /**
                             * Envia a mensagem a todos os clientes conectados,
                             * inclusive o próprio que solicitou o envio
                             */
                            enviarMensagem(socket.getOutputStream(), String.format("[Mensagem de %s] %s",
                                    hostaddress, c));
                        }
                    }
                } else {
                    break;
                }
            }
            if (!client.isClosed()) {
                enviarMensagem(os, "-- Aguarde --");
            }
        }
    }

    public void enviarMensagem(OutputStream stream, String mensagem, Boolean end) {
        PrintStream ps = new PrintStream(stream);
        ps.println(mensagem);
        if (Boolean.TRUE.equals(end)) {
            ps.println("<END>");
        }
    }

    public void enviarMensagem(OutputStream stream, String mensagem) {
        enviarMensagem(stream, mensagem, Boolean.FALSE);
    }

}

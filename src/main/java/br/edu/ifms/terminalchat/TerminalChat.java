package br.edu.ifms.terminalchat;

import java.io.IOException;
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
public class TerminalChat {

    private static Scanner teclado = new Scanner(System.in);

    private static int opcoes() {
        int op;
        while (true) {
            System.out.println("\n\n-- Menu de Opções --\n");
            System.out.println("1 - Iniciar Cliente");
            System.out.println("2 - Iniciar Servidor");
            System.out.println("3 - Sair");
            System.out.println("Informe uma opção: ");
            op = teclado.nextInt();
            if (op > 0 && op < 4) {
                return op;
            } else {
                System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }

    private static void initClient() throws IOException {
        System.out.println("Iniciando cliente.");
        System.out.println("\nInforme o IP ou o HOST do servidor: ");
        String host = teclado.next();
        System.out.println("Informe a porta do servidor: ");
        int port  = teclado.nextInt();
        teclado.nextLine();

        ClientSocket cliente = new ClientSocket(host, port);
        int op;
        String msg;
        while (true) {
            /**
             * Aguardando orientações do servidor
             */
            cliente.receberMensagem();
            System.out.print("Mensagem: ");
            msg = teclado.nextLine();
            cliente.enviarMensagem(msg);
            if ("sair".equals(msg)) {
                cliente.sair();
                break;
            }
        }
    }

    private static void initServer() throws IOException {
        System.out.println("Informe a porta do servidor: ");
        int port = teclado.nextInt();
        System.out.println("Informe o número de conexões: ");
        int n = teclado.nextInt();
        /**
         * Cria o servidor
         */
        ServerChat servidor = new ServerChat(port, n);
        /**
         * Aguarda as conexões serem realizadas
         */
        servidor.listenConnetions();
        /**
         * Inicia o CHAT após o limite de conexões ser atingido
         */
        while (true) {
            servidor.initChat();
        }
    }

    public static void main(String[] args) {
        int op = 0;
        do {
            try {
                op = opcoes();
                switch (op) {
                    case 1:
                        initClient();
                        break;
                    case 2:
                        initServer();
                        break;
                }
            } catch (IOException ex) {
                System.err.println("ERRO: " + ex.getMessage());
                System.out.println("-- Tente novamente -- \n");
            }
        } while (op != 3);

    }
}

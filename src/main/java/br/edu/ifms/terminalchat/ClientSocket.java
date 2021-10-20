package br.edu.ifms.terminalchat;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
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
public class ClientSocket {

    private final Socket socket;

    public ClientSocket(String host, int port) throws IOException {
        socket = new Socket(host, port);
    }

    public void enviarMensagem(String msg) throws IOException {
        PrintStream ps = new PrintStream(socket.getOutputStream());
        ps.println(msg);
        ps.println("<END>");
    }

    public void receberMensagem() throws IOException {
        if (!socket.isClosed()) {
            Scanner scanner = new Scanner(socket.getInputStream());
            String msg = "";
            while (scanner.hasNextLine()) {
                msg = scanner.nextLine();
                if ("<END>".equals(msg)) {
                    break;
                } else {
                    System.out.println(msg);
                }
            }
        }
    }

    public void sair() throws IOException {
        enviarMensagem("sair");
        socket.close();
    }
}

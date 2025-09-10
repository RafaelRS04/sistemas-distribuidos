/*
 * Laboratorio 2 de Sistemas Distribuidos
 *
 * Autor: Lucio A. Rocha
 * Ultima atualizacao: 17/12/2022
 *
 *  Alunos:
 * - Carlos Eduardo da Silva Ribeiro
 * - Rafael Rodrigues Sanches
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private static Socket socket;
    private static DataInputStream input;
    private static DataOutputStream output;

    private static final String requestFormat = """
                                                {
                                                    "method": "%s",
                                                    "args": ["%s"]
                                                }
                                                """;

    private int port = 1025;

    public void iniciar() {
        System.out.println("Cliente iniciado na porta: " + port);
        String request;

        try {

            socket = new Socket("127.0.0.1", port);

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            /* Menu do usuario  */
            Scanner scanner = new Scanner(System.in);
            System.out.println("OPCOES");
            System.out.println("1 - READ");
            System.out.println("2 - WRITE");
            System.out.println("Escolha uma opcao: ");
            int option = Integer.parseInt(scanner.nextLine());

            switch(option) {
                case 1:
                    request = String.format(requestFormat, "read", "");
                    output.writeUTF(request);
                    break;
                case 2:
                    StringBuilder builder = new StringBuilder();
                    boolean done = false;

                    System.out.println("Digite uma fortuna (use um unico % para registrar):");

                    /*
                     * Le ate encontrar uma linha com unico %,
                     * mas garante que a fortuna nao seja vazia
                     */
                    while(!done || builder.isEmpty()) {
                        String line = scanner.nextLine();
                        done = false;

                        if(line.equals("%")) {
                            done = true;
                        } else {
                            builder.append("\n");
                            builder.append(line);
                        }
                    }

                    request = String.format(requestFormat, "write", builder.toString());
                    break;
                default:
                    request = String.format(requestFormat, "Unknown", "Not Defined");
            }

            /* Envia requisicao */
            output.writeUTF(request);

            /* Recebe-se o resultado do servidor */
            String resultado = input.readUTF();
            System.out.println(resultado);

            socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Cliente().iniciar();
    }

}

/*
 * Laboratorio 2 de Sistemas Distribuidos
 *
 * Professor: Lucio A. Rocha
 *
 *  Alunos:
 * - Carlos Eduardo da Silva Ribeiro
 * - Rafael Rodrigues Sanches
 */

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private static Socket socket;
    private static ServerSocket server;

    private static DataInputStream input;
    private static DataOutputStream output;

    private final static Path path = Paths.get("fortune-br.txt");

    private static final String badRequestResult = """
                                                   {
                                                       "result": "false"
                                                   }
                                                   """;

    private static final String resultFormat = """
                                               {
                                                  "result": "%s"
                                               }
                                               """;

    private HashMap<Integer, String> hm = new HashMap<Integer, String>();
    private int port = 1025;

    private void loadFile(HashMap<Integer, String> hm) throws FileNotFoundException {
        try(BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            StringBuilder fortune = new StringBuilder();

            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                if(line.equals("%")) {
                    Integer key = hm.size();
                    hm.put(key, fortune.toString());
                    System.out.println("_________________________________________________________________________");
                    System.out.println(key);
                    System.out.println(fortune.toString());
                    fortune = new StringBuilder();
                } else {
                    fortune.append(line);
                    fortune.append("\n");
                }
            }

            /* Adiciona ultima fortuna */
            if(!fortune.isEmpty()) {
                Integer key = hm.size();
                hm.put(key, fortune.toString());
                System.out.println("_________________________________________________________________________");
                System.out.println(key);
                System.out.println(fortune.toString());
                System.out.println("_________________________________________________________________________");
            }
        } catch(IOException e) {
            System.out.println("ERRO: Excecao na leitura do arquivo.");
        }
    }

    private String randomFortune(HashMap<Integer, String> hm) {
        /* Lista as chaves de todas as fortunas */
        Set<Integer> keys = hm.keySet();
        List<Integer> keysList = keys.stream().toList();

        /* Escolhe um indice aleatorio para selecionar uma chave */
        SecureRandom random = new SecureRandom();
        int choice = random.nextInt(keysList.size());
        Integer fortuneKey = keysList.get(choice);

        /* Obtem uma fortuna usando a chave aleatoria */
        return hm.get(fortuneKey);
    }

    private void writeFortune(String fortune, HashMap<Integer, String> hm) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString(), true))) {
            writer.write("\n%\n");
            writer.write(fortune);
        } catch(IOException e) {
            System.out.println("ERRO: Excecao na escrita do arquivo.");
        }

        System.out.println("\nFortuna registrada com sucesso!\n");
        System.out.println("=========================== FORTUNA CADASTRADA ===========================");
        System.out.println(fortune);
        System.out.println("--------------------------------------------------------------------------");

        /* Adiciona ao HashMap se possivel */
        if(hm != null) {
            Integer key = hm.size();
            hm.put(key, fortune);
            System.out.println("CHAVE DA FORTUNA: " + key);
        }
    }

    private String parse(String request) {
        System.out.println(request);

        /* Define o padrao das requisicoes */
        Pattern requestPattern = Pattern.compile(
                "\\s*\\{\\s*\"method\"\\s*:\\s*\"(.*)\"\\s*,\\s*\"args\"\\s*:\\s*\\[\\s*\"(.*)\"\\s*]\\s*}\\s*",
                Pattern.DOTALL
        );

        /* Cria um objeto para verificar a requisicao */
        Matcher matcher = requestPattern.matcher(request);

        /* Caso esteja mal formatada, retorna falso */
        if(!matcher.matches()) {
            System.out.println("ERRO: requesicao mal formatada");
            return badRequestResult;
        }

        /* Separa os campos */
        String method = matcher.group(1);
        String args = matcher.group(2);
        System.out.println("Recebido: Method = " + method + ", Args = \"" + args + "\"");

        String result = badRequestResult;

        switch(method) {
            case "read":
                if(args.isEmpty()) {
                    String fortune = randomFortune(hm);
                    result = String.format(resultFormat, fortune);
                } else {
                    System.out.println("ERRO: o metodo 'read' nao possue argumentos");
                }
                break;
            case "write":
                if(args.endsWith("\n")) {
                    writeFortune(args, hm);
                    result = String.format(resultFormat, args);
                } else {
                    System.out.println("ERRO: o metodo 'write' possue argumentos terminando em '\\n'");
                }
                break;
            default:
                System.out.printf("ERRO: o metodo '%s' nao existe", method);
        }

        return result;
    }

    public void iniciar() {
        System.out.println("Servidor iniciado na porta: " + port);

        try {
            /* Criar porta de recepcao */
            server = new ServerSocket(port);
            socket = server.accept();  //Processo fica bloqueado, ah espera de conexoes

            /* Criar os fluxos de entrada e saida */
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            /* Carrega base de dados */
            loadFile(hm);

            /* Recebimento da string */
            String request = input.readUTF();
            String result = parse(request);

            /* Envio dos dados (resultado) */
            output.writeUTF(result);
            socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }

}

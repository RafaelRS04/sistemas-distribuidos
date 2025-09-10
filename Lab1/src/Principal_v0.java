/*
 * Lab1: Leitura de Base de Dados Nao-Distribuida
 *
 * Autor: Lucio A. Rocha
 * Ultima atualizacao: 20/02/2023
 *
 * Referencias:
 * https://docs.oracle.com/javase/tutorial/essential/io
 *
 * Autores read e write (Alunos):
 * - Carlos Eduardo da Silva Ribeiro
 * - Rafael Rodrigues Sanches
 *
 */

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;

public class Principal_v0 {

    public final static Path path = Paths
            .get("fortune-br.txt");
    private int NUM_FORTUNES = 0;

    public class FileReader {

        public int countFortunes() throws FileNotFoundException {

            int lineCount = 0;

            InputStream is = new BufferedInputStream(new FileInputStream(
                    path.toString()));
            try(BufferedReader br = new BufferedReader(new InputStreamReader(
                    is))) {

                String line = "";
                while(!(line == null)) {

                    if(line.equals("%"))
                        lineCount++;

                    line = br.readLine();

                }// fim while

                System.out.println(lineCount);
            } catch(IOException e) {
                System.out.println("SHOW: Excecao na leitura do arquivo.");
            }
            return lineCount;
        }

        public void parser(HashMap<Integer, String> hm)
                throws FileNotFoundException {

            InputStream is = new BufferedInputStream(new FileInputStream(
                    path.toString()));
            try(BufferedReader br = new BufferedReader(new InputStreamReader(
                    is))) {

                int lineCount = 0;

                String line = "";
                while(!(line == null)) {

                    if(line.equals("%"))
                        lineCount++;

                    line = br.readLine();
                    StringBuffer fortune = new StringBuffer();
                    while(!(line == null) && !line.equals("%")) {
                        fortune.append(line + "\n");
                        line = br.readLine();
                        // System.out.print(lineCount + ".");
                    }

                    hm.put(lineCount, fortune.toString());
                    System.out.println(fortune.toString());

                    System.out.println(lineCount);
                }// fim while

            } catch(IOException e) {
                System.out.println("SHOW: Excecao na leitura do arquivo.");
            }
        }

        public void read(HashMap<Integer, String> hm)
                throws FileNotFoundException {
            /* Lista as chaves de todas as fortunas */
            Set<Integer> keys = hm.keySet();
            List<Integer> keysList = keys.stream().toList();

            /* Escolhe um indice aleatorio para selecionar uma chave */
            SecureRandom random = new SecureRandom();
            int choice = random.nextInt(keysList.size());
            Integer fortuneKey = keysList.get(choice);

            /* Obtem uma fortuna usando a chave aleatoria */
            String fortune = hm.get(fortuneKey);

            /* Mostra a fortuna na tela */
            System.out.println("======================== LENDO FORTUNA ALEATORIA ========================");
            System.out.println(fortune);
            System.out.println("-------------------------------------------------------------------------");

        }

        public void write(HashMap<Integer, String> hm)
                throws FileNotFoundException {
            System.out.println("Digite uma fortuna (use um unico % para registrar):");

            /* Cria um scanner e variaveis de controle */
            Scanner scanner = new Scanner(System.in);
            StringBuilder builder = new StringBuilder("\n%");
            boolean hasContent = false;
            boolean done = false;

            /*
             * Le ate encontrar uma linha com unico %,
             * mas garante que a fortuna nao seja vazia
             */
            while(!done || !hasContent) {
                String line = scanner.nextLine();
                done = false;

                if(line.equals("%")) {
                    done = true;
                } else {
                    hasContent = true;
                    builder.append("\n");
                    builder.append(line);
                }
            }

            /* Concatena fortuna ao arquivo */
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString(), true))) {
                writer.write(builder.toString());
            } catch(IOException e) {
                System.out.println("SHOW: Excecao na escrita do arquivo.");
            }

            System.out.println("\nFortuna registrada com sucesso!\n");

            /* Adiciona ao HashMap se possivel */
            if(hm != null) {
                int key = hm.size();
                String fortune = builder.toString().replace("\n%\n", "");
                hm.put(key, fortune);

                System.out.println("=========================== FORTUNA CADASTRADA ===========================");
                System.out.println(fortune);
                System.out.println("--------------------------------------------------------------------------");
                System.out.println("CHAVE DA FORTUNA: " + key);
            }
        }
    }

    public void iniciar() {

        FileReader fr = new FileReader();
        try {
            NUM_FORTUNES = fr.countFortunes();
            HashMap hm = new HashMap<Integer, String>();
            fr.parser(hm);
            fr.read(hm);
            fr.write(hm);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Principal_v0().iniciar();
    }
}
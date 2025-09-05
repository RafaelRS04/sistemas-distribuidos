import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

	private static Socket socket;
	private static ServerSocket server;

	private static DataInputStream entrada;
	private static DataOutputStream saida;

	private int porta = 1025;

	public void iniciar() {
		System.out.println("Servidor iniciado na porta: " + porta);
		try {

			// Criar porta de recepcao
			server = new ServerSocket(porta);
			socket = server.accept();  //Processo fica bloqueado, ah espera de conexoes

			// Criar os fluxos de entrada e saida
			entrada = new DataInputStream(socket.getInputStream());
			saida = new DataOutputStream(socket.getOutputStream());

			// Recebimento do valor inteiro
			int valor = entrada.readInt();
			System.out.println(valor);

			// Processamento do valor
			String resultado = "";
			if (valor > 0)
				resultado = "O valor enviado eh maior que 0";
			else
				resultado = "O valor enviado eh menor que 0";

			// Envio dos dados (resultado)
			saida.writeUTF(resultado);

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		new Servidor().iniciar();

	}

}

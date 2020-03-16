
/**
 * Fichier HelloClient.java
 * Auteur  : Claude Duvallet
 * Crée le : 13/11/2004
 * Modifié : 14/11/2004
 * Description : Un client permettant d'envoyer un "Bonjour" 
 * à un serveur au moyen d'une socket réseau et attendant la 
 * réponse du serveur pour l'afficher.
 */
import java.net.*;
import java.util.Scanner;

public class HelloClient {

	private DatagramSocket ds;
	private String server_address;
	private int server_port;

	public HelloClient(String server_address, int server_port) {
		this.server_address = server_address;
		this.server_port = server_port;
		// Création d'une socket sur le port 5001 pour le client
		try {
			ds = new DatagramSocket(5001);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void start() {
		Scanner scanner;
		DatagramPacket dpr;
		int i = 0;
		byte[] buf = null;
		try {
			// Création d'un DatagramPacket pour envoi au serveur dont le nom et le port ont
			// été précisé en entrée
			System.out.println("Envoi d'un message à " + server_address);
			scanner = new Scanner(System.in);
			String nomInternet = scanner.nextLine();
			buf = (new String(nomInternet).getBytes());
			DatagramPacket dps = new DatagramPacket(buf, buf.length, InetAddress.getByName(server_address),
					server_port);
			System.out.println("Création du Datagram et envoi ...");
			
			// L'envoi se fait à partir de la socket client, l'adresse
			// de destination étant précisée dans le DatagramPacket
			ds.send(dps);
			System.out.println("Envoi effectué");
			
			// Création d'une socket pour recevoir les informations du serveur
			buf = new byte[1000];
			dpr = new DatagramPacket(buf, buf.length);
			System.out.println("Création du Datagram pour réception");
			ds.receive(dpr);
			//System.out.println(dpr.getLength());
			String[] receivedDatas = new String(dpr.getData(), 0, dpr.getLength()).split(":");
			System.out.println(
					"Numéro attribué par le serveur pour " + receivedDatas[0] + ": '" + receivedDatas[1] + "'");
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String args[]) {
		if (args.length < 2) {
			System.out.println("Nombre d'arguments insuffisant");
			System.out.println("Syntaxe : java HelloClient server_address server_port");
			System.exit(-1);
		}
		int port = Integer.parseInt(args[1]);
		HelloClient hc = new HelloClient(args[0], port);
		hc.start();
	}
}
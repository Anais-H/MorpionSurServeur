import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Classe modelisant un client du serveur de jeu de morpion.
 * @author HELOISE Anais - LAMBARD Maxence
 *
 */
public class MorpionClient {

    // instance qui gere la reception et l'envoie de datagrampaquet
    private DatagramSocket ds;
    
    // l'adresse serveur
    private String server_address;
    
    // le port du serveur
    private int server_port;
    

    // est a vrai si la communication avec le serveur est en cours
    private boolean communicationEnCours;

    public MorpionClient(String server_address, int server_port) {
        this.server_address = server_address;
        this.server_port = server_port;
        
        // Création d'une socket sur le port 5001 pour le client
        try {
            ds = new DatagramSocket(5001);
        } catch (Exception e) {
            System.out.println(e);
        }
        communicationEnCours = false;
    }

    public MorpionClient(String server_address, int server_port, int client_port) {
        this.server_address = server_address;
        this.server_port = server_port;
        
        // Création d'une socket sur le port 5001 pour le client
        try {
            ds = new DatagramSocket(client_port);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        communicationEnCours = false;
    }

    /**
     * Gestion de la communication avec le serveur de joueur.
     */
    public void start() {
        Scanner scanner;
        DatagramPacket dpr;
        byte[] buf = null;
        
        try {
            // Création d'un DatagramPacket pour envoi au serveur dont le nom et le port ont
            // été précisé en entrée
            System.out.println("Envoi de votre nom de joueur à " + server_address);
            scanner = new Scanner(System.in);
            String nomInternet = scanner.nextLine();
            buf = (new String(nomInternet).getBytes());
            DatagramPacket dps = new DatagramPacket(buf, buf.length, InetAddress.getByName(server_address),
                    server_port);
            System.out.println("Création du Datagram et envoi...");

            // L'envoi se fait à partir de la socket client, l'adresse
            // de destination étant précisée dans le DatagramPacket
            ds.send(dps);
            System.out.println("Envoi effectué");

            communicationEnCours = true;
            
            buf = new byte[1000];
            dpr = new DatagramPacket(buf, buf.length);
            System.out.println("Création du Datagram pour réception...");
            ds.receive(dpr);

            String messageRecu = new String(dpr.getData(), 0, dpr.getLength());
            System.out.println("Message reçu :");
            System.out.println(messageRecu);
            
            while (communicationEnCours) {
                
                // Création d'une socket pour recevoir les informations du serveur
                buf = new byte[1000];
                dpr = new DatagramPacket(buf, buf.length);
                System.out.println("En attente du serveur...\n");
                ds.receive(dpr);

                messageRecu = new String(dpr.getData(), 0, dpr.getLength());
                System.out.println("Message reçu :");
                System.out.println(messageRecu);
                
                //cas ou le serveur attend une reponse du client
                if (messageRecu.contains("-request-")) {
                    
                    System.out.println("Envoi d'un message à " + server_address);
                    scanner = new Scanner(System.in);
                    String coordonneesJeu = scanner.nextLine();
                    buf = (new String(coordonneesJeu).getBytes());
                    dps = new DatagramPacket(buf, buf.length, InetAddress.getByName(server_address),
                            server_port);
                    System.out.println("Création du Datagram et envoi ...");

                    // L'envoi se fait à partir de la socket client, l'adresse
                    // de destination étant précisée dans le DatagramPacket
                    ds.send(dps);
                    System.out.println("Envoi effectué");
                    
                // cas ou le serveur n'attend pas de reponse du client, il ne fait que l'informer
                } else if (messageRecu.contains("-exit-")) {
                    System.exit(0);
                }
            }
            
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
        
        int serveur_port = Integer.parseInt(args[1]);
        int client_port = Integer.parseInt(args[2]);
        MorpionClient joueur = new MorpionClient(args[0], serveur_port, client_port);
        joueur.start();

    }
}

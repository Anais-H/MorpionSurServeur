
import java.net.*;
import java.util.Arrays;
import java.math.*;

public class MorpionServeur {

    private Morpion morpion;

    private DatagramSocket dg;

    private String[] lesJoueurs;
    
    private int nbJoueurs;

    /**
     * Le serveur crée une socket pour écouter sur le port précisé en paramètre
     */
    public MorpionServeur(int port) {
        try {
            dg = new DatagramSocket(port);
        } catch (Exception e) {
            System.out.println("Constructeur :" + e);
        }
        morpion = new Morpion();
        nbJoueurs = 0;
        lesJoueurs = new String[2];
    }

    public void start() {
        DatagramPacket dpr;
        System.out.println("Démarrage du serveur");
        int i = 1;
        // Démarrage de la boucle d'attente des connexions clients
        while (true) {
            if (nbJoueurs < 2) {
                System.out.println("réception en cours");
                // Création du DatagramPacket de réception
                byte[] buf = new byte[1000];
                dpr = new DatagramPacket(buf, buf.length);
                
                try {
                    // Attente de la réception d'un DatagramPacket
                    dg.receive(dpr);
                    i++;
                    
                    String idJoueur = dpr.getAddress().getHostName() + ":" + dpr.getPort();
                    
                    System.out.println("Réception d'un message de " + idJoueur);
                    
                    System.out.println("Message reçu : " + new String(dpr.getData(), 0, dpr.getLength()));
                    
                    // verifie que la personne qui se co n'est pas deja enregistree en tant que joueur
                    boolean joueurDejaConnu = Arrays.stream(lesJoueurs).anyMatch(idJoueur::equals);
                    if (joueurDejaConnu) {
                        System.out.println("Envoie de Je vous connais déjà ! à " + idJoueur);
                        buf = ("Je vous connais déjà !").getBytes();
                        
                    } else {
                        
                        lesJoueurs[nbJoueurs] = dpr.getAddress().getHostName() + ":" + dpr.getPort();
                        nbJoueurs++;
                        
                        // Renvoi d'un message au client avec le numéro attribué
                        System.out.println("Numéro attribué par le serveur : " + new String(buf, 0, buf.length));
                        System.out.println("Numéro attribué au client :" + i);
                        
                        buf = Integer.toString(i).getBytes();
                        
                    }
                    
                    

                    
                    DatagramPacket dps = new DatagramPacket(buf, buf.length, dpr.getAddress(), dpr.getPort());
                    
                    dg.send(dps);
                }
                // catch(Error e){System.out.println(""+e);}
                catch (Exception e) {
                    // System.err.println (e);
                    System.out.println(e.toString());
                    System.exit(0);
                }
            } else {

            }
        }
    }

    public static void main(String args[]) {
        MorpionServeur hs = null;
        int port = 5000; // Par défaut le port est 5000
        if (args.length == 1) {
            // sinon récupération en ligne de commande du numéro de port
            port = Integer.parseInt(args[0]);
        }
        hs = new MorpionServeur(port);
        hs.start();
    }
}
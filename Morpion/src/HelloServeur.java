
/**
 * Fichier HelloServeur.java
 * Auteur  : Claude Duvallet
 * Crée le : 13/11/2004
 * Modifié : 14/11/2004
 * Description : Un serveur attendant les connexions de clients 
 * au moyen d'une socket réseau et renvoyent au client un numéro 
 * d'identification.
 */
import java.net.*;
import java.math.*;

public class HelloServeur {

    private DatagramSocket dg;

    /**
     * Le serveur crée une socket pour écouter sur le port précisé en paramètre
     */
    public HelloServeur(int port) {
        try {
            dg = new DatagramSocket(port);
        } catch (Exception e) {
            System.out.println("Constructeur :" + e);
        }
    }

    public void start() {
        DatagramPacket dpr;
        System.out.println("Démarrage du serveur");
        int i = 0; // pourquoi on devait demarrer a 1 ?
        // Démarrage de la boucle d'attente des connexions clients
        while (true) {
            // Création du DatagramPacket de réception
            byte[] buf = new byte[1000];
            dpr = new DatagramPacket(buf, buf.length);
            try {
                // Attente de la réception d'un DatagramPacket
                System.out.println("essai");
                dg.receive(dpr);
                i++;
                System.out.println("Réception d'un message de " + dpr.getAddress().getHostName() + "/" + dpr.getPort());
                System.out.println("Message reçu : " + new String(dpr.getData(), 0, dpr.getLength()));
                
                buf = (new String(dpr.getData(), 0, dpr.getLength()) + ":" + Integer.toString(i)).getBytes();

                // Renvoi d'un message au client avec le numéro attribué
                DatagramPacket dps = new DatagramPacket(buf, buf.length, dpr.getAddress(), dpr.getPort());
                System.out.println("Numéro attribué par le serveur : " + new String(buf, 0, buf.length));
                System.out.println("Numéro attribué au client :" + i);
                dg.send(dps);
            }
            // catch(Error e){System.out.println(""+e);}
            catch (Exception e) {
                // System.err.println (e);
                System.out.println(e.toString());
                System.exit(0);
            }
        }
    }

    public static void main(String args[]) {
        HelloServeur hs = null;
        int port = 5000; // Par défaut le port est 5000
        if (args.length == 1) {
            // sinon récupération en ligne de commande du numéro de port
            port = Integer.parseInt(args[0]);
        }
        hs = new HelloServeur(port);
        hs.start();
    }
}

import java.net.*;
import java.util.Arrays;
import java.io.IOException;
import java.math.*;

/**
 * Classe modelisant un serveur permettant de client qui se connectent de jouer
 * au morpion entre eux.
 * 
 * @author HELOISE Anais - LAMBARD Maxence
 *
 */
public class MorpionServeur {

    // instance qui gere le jeu de morpion
    private Morpion morpion;

    // instance qui gere la reception et l'envoie de datagrampaquet
    private DatagramSocket dg;

    // instances qui sauvegardent les informations de routage des clients
    private Joueur joueur1, joueur2;

    private boolean enCours;

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
        enCours = false;
    }

    /**
     * Attente de l'enregistrement des deux joueurs
     */
    public void start() {
        DatagramPacket dpr = null;
        DatagramPacket dps = null;

        System.out.println("Démarrage du serveur");

        int nbJoueurs = 0;

        // Démarrage de la boucle d'attente des connexions clients
        while (nbJoueurs < 2) { // while (true) {

            // Création du DatagramPacket de réception
            byte[] buf = new byte[1000];
            dpr = new DatagramPacket(buf, buf.length);

            try {
                // Attente de la réception d'un DatagramPacket
                dg.receive(dpr);

                System.out.println("Réception d'un message de " + dpr.getAddress().getHostName() + ":" + dpr.getPort());

                String nomDuJoueur = new String(dpr.getData(), 0, dpr.getLength());

                System.out.println("Message reçu : " + nomDuJoueur);

                // verifie que la personne qui se co n'est pas deja enregistree en tant que
                if (nbJoueurs == 1 && (joueur1.getAdresse() + ":" + joueur1.getPort())
                        .equals(dpr.getAddress() + ":" + dpr.getPort())) {

                    System.out.println("Envoie de Vous êtes déjà enregistré. à " + joueur1);

                    buf = ("-info-\nVous êtes déjà enregistré en tant que " + joueur1.getNom()
                            + "\nEn attente de votre adversaire...").getBytes();

                } else { // c'est un nouveau joueur

                    Joueur nouveauJoueur = new Joueur(dpr.getAddress(), dpr.getAddress().getHostName(), dpr.getPort(),
                            nomDuJoueur);
                    nbJoueurs++;

                    // Renvoi d'un message au client avec son nom de joueur
                    System.out.println("Nom attribué par le serveur : " + new String(buf, 0, buf.length));
                    System.out.println("Nom attribué au client :" + nouveauJoueur.getNom());

                    buf = ("-info-\n" + nouveauJoueur.getNom()).getBytes();

                    // on determine qui est le nouveau joueur
                    if (nbJoueurs == 1) {
                        joueur1 = nouveauJoueur;

                    } else if (nbJoueurs == 2) {
                        joueur2 = nouveauJoueur;
                    }

                }

                dps = new DatagramPacket(buf, buf.length, dpr.getAddress(), dpr.getPort());

                dg.send(dps);
            }

            catch (Exception e) {
                System.out.println(e.toString());
                System.exit(0);
            }

        }
    }

    /**
     * Le jeu est en cours. Traitement des tours des joueurs.
     */
    public void jeuEnCours() {
        String messageJC = "";
        String messageJA = "";

        // Création du DatagramPacket de réception
        byte[] bufJC = new byte[1000];
        byte[] bufJA = new byte[1000];

        DatagramPacket dpsJC = null;
        DatagramPacket dpsJA = null;

        messageJC = "-request-\nVous jouez contre " + joueur2.getNom() + ". Vous avez la main !\n"
                + "Vous pouvez jouer !\n" + morpion.toString()
                + "Entrez les coordonnées de la case entre 1 et 3 (numLigne numColonne)";
        messageJA = "-info-\nVous jouez contre " + joueur1.getNom() + ". C'est votre adversaire qui commence !";

        System.out.println();

        bufJC = messageJC.getBytes();
        bufJA = messageJA.getBytes();

        dpsJC = new DatagramPacket(bufJC, bufJC.length, joueur1.getAdresse(), joueur1.getPort());
        dpsJA = new DatagramPacket(bufJA, bufJA.length, joueur2.getAdresse(), joueur2.getPort());

        try {
            if (dpsJC != null)
                dg.send(dpsJC);
            if (dpsJA != null)
                dg.send(dpsJA);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // vidage des messages
        messageJC = "";
        messageJA = "";

        DatagramPacket dpr = null;

        enCours = true;

        while (enCours) {
            System.out.println("\nTour " + morpion.getNbTours() + "\n");

            // Attente de la réception d'un DatagramPacket
            Joueur joueurCourant = null;
            Joueur joueurEnAttente = null;

            dpr = new DatagramPacket(bufJC, bufJC.length);

            // recuperation du joueur courant en fonction de la parite du tour
            if (morpion.getNbTours() % 2 == 1) {
                joueurCourant = joueur1;
                joueurEnAttente = joueur2;

            } else {
                joueurEnAttente = joueur1;
                joueurCourant = joueur2;

            }

            System.out.println("-log-\nLes infos des joueurs :\njoueurCourant : " + joueurCourant
                    + "\njoueurEnAttente : " + joueurEnAttente);

            try {
                System.out.println("-log-\nRéception des cordonnées de jeu...");
                dg.receive(dpr);

                String[] coordonneesDeJeu = (new String(dpr.getData(), 0, dpr.getLength())).trim().split(" ");

                System.out.println("-log-Coordonnées reçues : " + coordonneesDeJeu);

                if (verifFormatCoordonnees(coordonneesDeJeu)) {

                    int numLigne = Integer.parseInt(coordonneesDeJeu[0]);
                    int numCol = Integer.parseInt(coordonneesDeJeu[1]);

                    if (morpion.mouvementAutorise(numLigne - 1, numCol - 1)) {
                        morpion.getJoueurCourant().jouer(numLigne - 1, numCol - 1);

                        // on verifie le nombre de tour pour savoir si c'est la fin du jeu
                        if (morpion.victoire()) {
                            enCours = false;

                            System.out.println("La partie est finie !");

                            messageJA = "-exit-\n" + morpion + "Vous avez perdu...";
                            messageJC = "-exit-\n" + morpion + "Vous avez gagné, félicitations !";

                        } else if (morpion.fini()) {
                            enCours = false;

                            System.out.println("La partie est finie !");

                            messageJC = "-exit-\nLa partie est finie ! Personne n'a gagné...";
                            messageJA = messageJC;

                        } else {

                            messageJA = "-request-Tour " + morpion.getNbTours() + "\n" + morpion + "A vous de jouer "
                                    + joueurEnAttente.getNom() + " ! (numLigne numColonne)";
                            messageJC = "-info-\n" + morpion + "En attente de " + joueurEnAttente.getNom() + "...";

                        }

                    } else {
                        messageJC = "-request-Tour " + morpion.getNbTours() + "\n" + morpion + "La case [" + numLigne
                                + ", " + numCol + "] est déjà jouée !\nChoisissez une case libre !";
                    }

                } else {
                    messageJC = "-request-Tour " + morpion.getNbTours() + "\n" + morpion
                            + "Les coordonnées envoyées n'ont pas un format correctes...\n"
                            + "Veuillez utiliser le format suivant pour des coordonnées comprises entre 1 et 3 : numLigne numColonne";
                }

                bufJC = messageJC.getBytes();
                bufJA = messageJA.getBytes();

                if (messageJC.length() > 0) {
                    System.out.println("-log-\nEnvoie à " + joueurCourant.getNom() + " de :\n" + messageJC + "\n("
                            + bufJC.length + " bytes)");
                    dpsJC = new DatagramPacket(bufJC, bufJC.length, joueurCourant.getAdresse(),
                            joueurCourant.getPort());
                }

                if (messageJA.length() > 0) {
                    System.out.println("-log-\nEnvoie à " + joueurEnAttente.getNom() + " de :\n" + messageJA + "\n("
                            + bufJA.length + " bytes)");
                    dpsJA = new DatagramPacket(bufJA, bufJA.length, joueurEnAttente.getAdresse(),
                            joueurEnAttente.getPort());
                }

                if (messageJC.length() > 0)
                    dg.send(dpsJC);
                if (messageJA.length() > 0)
                    dg.send(dpsJA);

                // vidage des messages
                messageJC = "";
                messageJA = "";

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // a ameliorer en recommençant a attendre de nouveaux joueurs
        System.out.println("Extinction du serveur...");
        System.exit(0);

    }

    /**
     * Vérifie le format des coordonnées recues.
     * 
     * @param coord Les coordonnees recues
     * @return vrai si les coordonnees sont de la former 'i j', i et j des chiffres
     *         entre 1 et 3, faux sinon
     */
    private boolean verifFormatCoordonnees(String[] coord) {
        if (coord.length == 2 && coord[0].length() == 1 && coord[1].length() == 1) {
            if (Character.isDigit(coord[0].charAt(0)) && Character.isDigit(coord[1].charAt(0))) {
                int numLigne = Integer.parseInt(coord[0]);
                int numColonne = Integer.parseInt(coord[1]);
                if (numColonne >= 1 && numColonne <= 3 && numLigne >= 1 && numLigne <= 3) {
                    return true;
                }
            }
        }
        return false;
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
        hs.jeuEnCours();
    }
}
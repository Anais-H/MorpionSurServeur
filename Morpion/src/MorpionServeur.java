
import java.net.*;
import java.util.Arrays;
import java.io.IOException;
import java.math.*;

public class MorpionServeur {

    private Morpion morpion;

    private DatagramSocket dg;

    private Joueur joueur1, joueur2;

    // private String[] lesJoueurs;
    private String[] joueursNoms;

    private int nbJoueurs;

    private String etatJeu;

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
        nbJoueurs = 0;
        enCours = false;
        // lesJoueurs = new String[2];
    }

    /**
     * Attente de l'enregistrement des deux joueurs
     */
    public void start() {
        DatagramPacket dpr = null;
        DatagramPacket dps = null;

        System.out.println("Démarrage du serveur");
        // int i = 1;

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

    // cas ou le jeu a commence, un joueur vient d'envoyer les coordonnees de la
    // case jouee
    public void jeuEnCours() {
        // Création du DatagramPacket de réception
        byte[] bufJC = new byte[1000];
        byte[] bufJA = new byte[1000];

        DatagramPacket dpsJC = null;
        DatagramPacket dpsJA = null;

        // les deux joueurs requis sont enregistres, on les previent
        System.out.println(
                "Informe le joueur 1 qu'il a la main.\nInforme le joueur 2 que c'est le joueur 1 qui commence.");
        bufJC = ("-request-\nVous jouez contre " + joueur2.getNom() + ". Vous avez la main !\n"
                + "Vous pouvez jouer !\n" + morpion.toString()
                + "Entrez les coordonnées de la case entre 1 et 3 (numLigne numColonne)").getBytes();
        bufJA = ("-info-\nVous jouez contre " + joueur1.getNom() + ". C'est votre adversaire qui commence !\n")
                .getBytes();

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
            
            System.out.println("les infos des joueurs :\njC : " + joueurCourant + "\njA : " + joueurEnAttente);

            try {
                System.out.println("Réception des cordonnées de jeu");
                dg.receive(dpr);

                String[] coordonneesDeJeu = (new String(dpr.getData(), 0, dpr.getLength())).split(" ");
                int numLigne = Integer.parseInt(coordonneesDeJeu[0]);
                int numCol = Integer.parseInt(coordonneesDeJeu[1]);

                if (morpion.mouvementAutorise(numLigne, numCol)) {
                    morpion.getJoueurCourant().jouer(numLigne, numCol);

                    System.out.println(
                            "Demande au joueur en attente de jouer.\nInforme l'autre joueur qu'il est en attente.");
                    bufJA = ("-request-Tour \n" + morpion.getNbTours() +"\n" + morpion + "A vous de jouer " + joueurEnAttente.getNom()
                            + " ! (numLigne numColonne)").getBytes();
                    bufJC = ("-info-\n" + morpion + "En attente de " + joueurEnAttente.getNom() + "...").getBytes();

                } else {
                    System.out.println("Demande au joueur courant de rejouer.");
                    bufJC = ("-request-Tour \n" + morpion.getNbTours() + "\nLa case [" + numLigne + ", " + numCol
                            + "] est déjà jouée !\nChoisissez une case libre !").getBytes();
                }

                if (bufJC.length > 0)
                    dpsJC = new DatagramPacket(bufJC, bufJC.length, joueurCourant.getAdresse(), joueurCourant.getPort());

                if (bufJA.length > 0)
                    dpsJA = new DatagramPacket(bufJA, bufJA.length, joueurEnAttente.getAdresse(),
                        joueurEnAttente.getPort());

                if (bufJC.length > 0)
                    dg.send(dpsJC);
                if (bufJA.length > 0)
                    dg.send(dpsJA);
                
                // vidage des buffers
                bufJC = new byte[1000];
                bufJA = new byte[1000];

            } catch (IOException e) {
                e.printStackTrace();
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
        hs.jeuEnCours();
    }
}
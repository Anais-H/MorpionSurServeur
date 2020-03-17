/**
 * Classe modelisant un jeu de Morpion standard de taille de grille 3 et avec deux joueurs.
 * La classe Morpion ne gere pas les règle du jeu, des méthodes sont a
 * disposition de l'utilisateur pour gérer par exemple les mouvements des
 * joueurs.
 * 
 * @author HELOISE Anais - LAMBARD Maxence
 *
 */
public class Morpion {
    final char ROND = 'O';
    final char CROIX = 'X';

    private JoueurMorpion j1, j2; // les deux joueurs

    // la grille de jeu
    private char[][] grille;

    // la taille de la grille de jeu
    private int taille;

    // le numero du tour courant
    private int nbTours;

    // l'etat du jeu (enCours, fin, victoire)
    private String etat;

    public Morpion() {
        nbTours = 1;
        etat = "enCours";

        taille = 3;
        grille = new char[taille][taille];

        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                grille[i][j] = ' ';
            }
        }

        j1 = new JoueurMorpion("Joueur1", ROND);
        j2 = new JoueurMorpion("Joueur2", CROIX);
    }

    /**
     * Retourne le joueur courant sachant que si le tour est pair, c'est au joueur2
     * de jouer.
     * 
     * @return Le joueur qui est en train de jouer
     */
    public JoueurMorpion getJoueurCourant() {
        if (nbTours % 2 == 0) {
            return j2;
        } else {
            return j1;
        }
    }

    public String getEtat() {
        return etat;
    }

    public int getNbTours() {
        return nbTours;
    }

    /**
     * Retourne si le joueur peut jouer dans une case sachant qu'on peut jouer dans
     * une case si elle n'est pas deja cauchee.
     * 
     * @param numLigne Le numero de ligne de la case jouee
     * @param numCol   Le numero de colonne de la case jouee
     * @return vrai si le mouvent est autorisee, faux sinon
     */
    public boolean mouvementAutorise(int numLigne, int numCol) {
        if (grille[numLigne][numCol] == ' ') {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Verifie si la partie est fini, c'est-a-dire si un joueur a gagne ou si plus
     * aucun mouvement n'est possible. Tant que toutes les cases ne sont pas
     * remplies, la partie n'est pas considerees comme finie...
     */
    private void verifFinJeu() {
        JoueurMorpion joueur = getJoueurCourant();
        boolean aGagne = false;

        // verification de victoire (trois fois le meme symbole de suite)
        for (int p = 1; p <= 2; p++) {
            // lignes
            for (int i = 0; i < taille - 2; i++)
                for (int j = 0; j < taille; j++)
                    if (grille[i + 1][j] == joueur.getSymbole() && grille[i][j] == joueur.getSymbole()
                            && grille[i + 2][j] == joueur.getSymbole())
                        aGagne = true;
            // colonnes
            for (int i = 0; i < taille; i++)
                for (int j = 0; j < taille - 2; j++)
                    if (grille[i][j + 1] == joueur.getSymbole() && grille[i][j + 2] == joueur.getSymbole()
                            && grille[i][j] == joueur.getSymbole())
                        aGagne = true;
            // diagonales
            for (int i = 0; i < taille - 2; i++)
                for (int j = 0; j < grille[i].length - 2; j++)
                    if (grille[i + 1][j + 1] == joueur.getSymbole() && grille[i + 2][j + 2] == joueur.getSymbole()
                            && grille[i][j] == joueur.getSymbole()
                            || grille[i + 2][j] == joueur.getSymbole() && grille[i + 1][j + 1] == joueur.getSymbole()
                                    && grille[i][j + 2] == joueur.getSymbole())
                        aGagne = true;
        }

        if (aGagne) {
            etat = "victoire";
            joueur.gagne();

        } else if (nbTours == 9) {
            etat = "fin";
        }
    }

    /**
     * Retourne si la partie est fini (pas de victoire d'un des joueurs).
     * 
     * @return vrai si la partie est fini, faux sinon
     */
    public boolean fini() {
        return etat == "fin";
    }

    /**
     * Retourne si la partie est gagnee par un joueur.
     * 
     * @return vrai si un joueur a gagne la partie, faux sinon
     */
    public boolean victoire() {
        return etat == "victoire";
    }

    /**
     * Retourne si la partie est en cours.
     * 
     * @return vrai si la partie est en cours, faux sinon
     */
    public boolean enCours() {
        return etat == "enCours";
    }

    @Override
    public String toString() {
        String retour = "";
        for (int i = 0; i < taille; i++) {
            retour += "|";
            for (int j = 0; j < taille; j++) {
                retour += grille[i][j] + "|";
            }
            retour += "\n";

        }
        return retour;
    }

    /**
     * Classe qui modelise un joueur de morpion.
     * 
     * @author HELOISE Anais - LAMBARD Maxence
     *
     */
    class JoueurMorpion {
        // le nom (ou pseudo) du joueur
        private String nom;

        // le symbole utilise par le joueur pour jouer (exemple crois, rond...)
        private char symbole;

        JoueurMorpion(String nom, char symbole) {
            this.nom = nom;
            this.symbole = symbole;
        }

        public char getSymbole() {
            return symbole;
        }

        /**
         * Le joueur joue une case.
         * @param numLigne le numero de ligne de la case jouee
         * @param numCol   le numero de colonne de la case jouee
         */
        public void jouer(int numLigne, int numCol) {
            grille[numLigne][numCol] = symbole;
            verifFinJeu();
            nbTours++;

        }

        /**
         * Signale au joueur qu'il a gagne.
         */
        public void gagne() {
            System.out.println(nom + " a gagné !");
        }
    }

    public static void main(String[] args) {
        Morpion m = new Morpion();
        try {
            m.getJoueurCourant().jouer(1, 1);
            m.getJoueurCourant().jouer(1, 2);
            m.getJoueurCourant().jouer(2, 2);
            m.getJoueurCourant().jouer(0, 2);
            m.getJoueurCourant().jouer(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(m);
    }
}

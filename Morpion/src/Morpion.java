
public class Morpion {
    final char ROND = 'O';
    final char CROIX = 'X';

    private JoueurMorpion j1, j2; // les deux joueurs
    private char[][] grille;
    private int taille;
    private int nbTours; // le joueur 1 joue aux tours impaires, le joueur 2 aux tours pairs
    private String etat; // l'etat du jeu (en Cours, fini, gagne)

    public Morpion() {
        nbTours = 1;
        etat = "en Cours";

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

    private boolean mouvementAutorise(int numLigne, int numCol) throws Exception {
        if (grille[numLigne][numCol] == ' ') {
            return true;
        }
        throw new Exception("Mouvement non autorisé !");
    }

    private void verifFinJeu() {
        JoueurMorpion joueur = getJoueurCourant();
        boolean aGagne = false;

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
            joueur.gagne();
        }
    }

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

    class JoueurMorpion {
        private String nom;
        private char symbole;

        JoueurMorpion(String nom, char symbole) {
            this.nom = nom;
            this.symbole = symbole;
        }

        public char getSymbole() {
            return symbole;
        }

        public void jouer(int numLigne, int numCol) throws Exception {
            if (mouvementAutorise(numLigne, numCol)) {
                grille[numLigne][numCol] = symbole;
            }
            verifFinJeu();
            nbTours++;
        }

        public void gagne() {
            etat = "gagne";
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

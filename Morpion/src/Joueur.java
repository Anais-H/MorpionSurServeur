import java.net.InetAddress;

/**
 * Classe modelisant un joueur pour le serveur de morpion.
 * @author HELOISE Anais - LAMBARD Maxence
 *
 */
public class Joueur {
    // l'adresse Inet du joueur
    private InetAddress adresse;
    
    // l'host du joueur
    private String hostName;
    
    // le port du joueur
    private int port;
    
    // le nom (ou pseudo) du joueur
    private String nom;
    
    
    public Joueur(InetAddress adresse, String hostName, int port, String nom) {
        this.adresse = adresse;
        this.hostName = hostName;
        this.port = port;
        this.nom = nom;
    }
    
    public String getNom() {
        return nom;
    }
    
    public int getPort() {
        return port;
    }
    
    public InetAddress getAdresse() {
        return adresse;
    }
    
    public String getHostName() {
        return hostName;
    }
    
    /**
     * Retourne une combinaison de l'host et du port du joueur de la facon suivante : host:port
     * @return
     *    host:port du joueur
     */
    public String getHostPort() {
        return hostName + ":" + port;
    }
    
    @Override
    public String toString() {
        return getAdresse() + " alias " + nom;
    }
}

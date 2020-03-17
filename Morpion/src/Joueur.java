import java.net.InetAddress;

public class Joueur {
    private InetAddress adresse;
    private String hostName;
    private int port;
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
    
    public String getHostPort() {
        return hostName + ":" + port;
    }
    
    @Override
    public String toString() {
        return getAdresse() + " alias " + nom;
    }
}

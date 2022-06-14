import java.io.Serializable;

public class Archivo implements Serializable {
    
    private String archivo;
    private String hash;
    private long tam;
    
    public Archivo(){
  
    }
    
    public Archivo(String archivo, String hash){
        this.archivo = archivo;
        this.hash = hash;   
    }
    
    public long getTam() {
        return tam;
    }

    public void setTam(long tam) {
        this.tam = tam;
    }
    
    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
    
}

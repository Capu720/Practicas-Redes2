import java.io.*;

class SopaTiempo implements Serializable {

    String tiempo;
    String juego;
    boolean terminado;

    SopaTiempo(String tiempo, String juego, boolean terminado){
        this.tiempo = tiempo;
        this.juego = juego;
        this.terminado = terminado;
    }

    public String getTiempo(){
        return this.tiempo;
    }

    public String getJuego(){
        return this.juego;
    }

    public String getTerminado(){
        return this.terminado;
    }
}
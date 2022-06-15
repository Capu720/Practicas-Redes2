import java.io.*;

public class Datos implements Serializable{
    private File[] archivos;
    private String opcion;
    private int numArchivos;


    public Datos(String opcion){
        this.opcion = opcion;
    }

    public Datos(String opcion, File[] archivos)
    {
        this.opcion = opcion;
        this.archivos = archivos;
    }

    public Datos(File[] archivos, String opcion){
        this.archivos = archivos;
        this.opcion = opcion;
        this.numArchivos = archivos.length;
    }

    public int numArchivos(){
        return numArchivos;
    }

    public String opcion(){
        return opcion;
    }
}

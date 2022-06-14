
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;


public class ClienteRMI extends Thread {
    private String archivo;
    private int puertoRMI;
    private ArrayList <Archivo> Lista;
    private String direccion;
    
    
    public ClienteRMI(String archivo, int puertoRMI, ArrayList <Archivo> Lista)
    {
        this.archivo = archivo;
        this.puertoRMI = puertoRMI;
        this.Lista = Lista;  
    }
    
    public ClienteRMI(String archivo, int puertoRMI, ArrayList <Archivo> Lista, String direccion)
    {
        this.archivo = archivo;
        this.puertoRMI = puertoRMI;
        this.Lista = Lista;
        this.direccion = direccion;
    }
      
    
    public void run()
    {
        String host = "127.0.0.1";
        try
        {
            Registry registry = LocateRegistry.getRegistry(host, puertoRMI);
            System.out.println("Puerto cliente RMI: "+puertoRMI);
            ListaArchivos interfaz = (ListaArchivos) registry.lookup("ListasArchivos");
            //ListaArchivos listaArch = (ListaArchivos) registry.lookup("ListaArchivos");
            
            ArrayList <Archivo> archbusca = interfaz.busquedaArchivos(archivo,direccion);
            
            Lista.addAll(archbusca);
        
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }  
}

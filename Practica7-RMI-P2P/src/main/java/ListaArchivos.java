import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ListaArchivos extends Remote{
   ArrayList <Archivo> busquedaArchivos(String archivo, String direccion) throws RemoteException; 
}

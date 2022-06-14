
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteMulticast extends Thread{
    private final MulticastSocket ms;
    private final String host = "228.1.1.1"; 
    private final int PuertoRMI;
    private final int PuertoFlujo;
    private final String ID;
    private ArrayList <Datos> servidores;
    private final String direccion;
    
    
    
    public ClienteMulticast(MulticastSocket ms, int PuertoRMI, int PuertoFlujo, String ID, ArrayList <Datos> servidores, String direccion)
    {
        this.ms = ms;
        this.PuertoRMI = PuertoRMI;
        this.PuertoFlujo = PuertoFlujo;
        this.ID = ID;
        this.servidores = servidores;
        this.direccion = direccion;
    }
    
    @Override
    public void run()
    {
        try 
        {
            InetAddress gp = InetAddress.getByName(host);
            ms.joinGroup(gp);
            System.out.println("Cliente unido a la direccion de grupo 228.1.1.1");
            
            //Creamos un datagrama de lectura
            DatagramPacket dpRecive = new DatagramPacket(new byte[6535], 6535);
      
            while(true)
            {
                //Recibimos los datos provenietes del servidor multicast
                ms.receive(dpRecive);
                String mensaje = new String(dpRecive.getData(), 0 ,dpRecive.getLength());
                String datos[] = mensaje.split(" ");
                String PuertoRMI = datos[0];
                String PuertoEnvio = datos[1];
                String ID = datos[2];
                String direccion = datos[3];
               
                //Datos(String PuertoRMI, String PuertoEnvio, String ID, String Dirrecion)
                Datos dat = new Datos(PuertoRMI, PuertoEnvio, ID, direccion);
                dat.setID(ID);
                dat.setPuertoEnvio(PuertoEnvio);
                dat.setPuertoRMI(PuertoRMI);
                dat.setDirrecion(direccion);
                
                //Creamos una lista de servidores en linea, vemos si tiene distinto ID para no meter el mismo
                int encontrado = 0;
                for(int i=0; i<servidores.size(); i++)
                {
                    if(servidores.get(i).getID().equals(dat.getID())){
                        encontrado++;
                    }
          
                }
                
                if(encontrado == 0 && !dat.getID().equals(this.ID))
                {
                    servidores.add(dat);
                    //ServidorMulticast(MulticastSocket ms, int PuertoRMI, int PuertoEnvio, String ID, String direccion)
                    new ServidorMulticast(ms, this.PuertoRMI, this.PuertoRMI, this.ID, this.direccion).start();
                }
                
                
                //System.out.println("Servidores disponibles: "+servidores.size());
               
            }
           
        } catch (IOException ex) {
            Logger.getLogger(ClienteMulticast.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       
    }
    
    
    
}

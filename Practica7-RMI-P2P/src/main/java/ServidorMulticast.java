
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class ServidorMulticast extends Thread{ 
    private final MulticastSocket ms;
    private final String host = "228.1.1.1"; 
    private final int PuertoRMI;
    private final int PuertoEnvio;
    private final String ID;
    private final String direccion;
    
    public ServidorMulticast(MulticastSocket ms, int PuertoRMI, int PuertoEnvio, String ID, String direccion){
        this.ms = ms;
        this.PuertoRMI = PuertoRMI;
        this.PuertoEnvio = PuertoEnvio;
        this.ID = ID;
        this.direccion = direccion;
    }
    
    @Override
    public void run()
    {  
        try
        {
            InetAddress gpo = InetAddress.getByName(host);
            String mensaje = "" + PuertoRMI + " " + PuertoEnvio + " " + ID + " " + direccion;
            EnviarMensaje(mensaje, gpo);
            
            try{
                //Lo envia cada 5 segundos de que esta ahi
                Thread.sleep(5000);
            }catch(InterruptedException ie){}
        }
        catch(IOException ex){
            ex.printStackTrace();
        } 
    }
    
    private void EnviarMensaje(String mensaje, InetAddress gpo) throws IOException
    {
        byte[] buffer = mensaje.getBytes();
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length, gpo,7777);
        ms.send(dp);
    }
    
}

import java.net.*;
import java.io.*;

public class Servidor {
      
    public static void main(String[] args) {
        try
        {
            ServerSocket ss = new ServerSocket(3000);
	        System.out.println("Servidor iniciado. Esperando cliente");
            for(;;){
                Socket cl = ss.accept();
                byte[] datosrecibidos = new byte[1024];
                BufferedInputStream bis = new BufferedInputStream(cl.getInputStream());
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                String archivo = dis.readUTF();
                archivo = archivo.substring(archivo.indexOf('/')+1,archivo.length());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(archivo));
                int leidos;
                while((leidos = bis.read(datosrecibidos)) != -1){
                    bos.write(datosrecibidos,0,leidos);
                }
                bos.close();
                dis.close();

            }
            

        }catch(Exception ex){
                  
        }
        
    }
}

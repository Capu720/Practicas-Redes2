import java.net.*;
import java.io.*;

public class Servidor {
      
    public static void main(String[] args) {
        try
        {
            ServerSocket ss = new ServerSocket(3000);
	        System.out.println("Servidor iniciado. Esperando cliente");
            for(;;)
            {
                Socket cl = ss.accept();
                int leidos = 0;
                int completados = 0;

                //Recibimos la ruta del archivo
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                String file = dis.readUTF();
                file = file.substring(file.indexOf('/')+1,file.length());

                BufferedOutputStream bos = new BufferedOutputStream(cl.getOutputStream());
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                
                byte[] buf = new byte[1024];
                int tam_bloque = (bis.available() >= 1024)? 1024 :bis.available();
                int tam_archivo = bis.available();
                System.out.println("Tamaño del archivo:"+bis.available()+ " bytes");
                int b_leidos;
                while((b_leidos = bis.read(buf,0,buf.length)) != -1){
                    bos.write(buf,0,b_leidos);
                    bos.flush();
                    leidos += tam_bloque;
                    completados = (leidos*100) / tam_archivo;
                    System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
                    System.out.print("Completado:"+completados+" %");
                    tam_bloque=(bis.available()>=1024)?1024:bis.available();
                }
                bos.close();
                bis.close();
            }
            

        }catch(Exception ex){
                  
        }
        
    }
}

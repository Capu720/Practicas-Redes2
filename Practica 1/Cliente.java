import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.JFileChooser;


public class Cliente{

    Socket cl1;

    public void Menu()
    {
        int opcion;
        Scanner sca = new Scanner(System.in);
        System.out.println("Bienvenido, seleccione alguna de las siguientes opciones:");
        System.out.println("1. Subir archivos o carptea:");
        System.out.println("2. Descargar archivo");
        System.out.println("3. Listar conenido");
        System.out.println("4. Eliminar archivos");
        System.out.print("Su opcion:");
        opcion = Integer.parseInt(sca.nextLine());

        switch(opcion)
        {
            case 1:
                Subir();
                break;

            case 2:
                Descargar();
                break;

            case 3:
                Listar();
                break;

            case 4:
                Conectar();
                break;
            
            default:
                System.out.println("Introduzca un valor del 1-4");
        }
    
    }

    public void Subir(){       
        
        try
        {
            //Seleccionamos el archivo
            JFileChooser file = new JFileChooser();
            file.showOpenDialog(file);
            String ruta = file.getSelectedFile().getAbsolutePath();
            
            File archivo = new File(ruta);
            BufferedInputStream bis = new BufferedInputStream(cl1.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(archivo));
            cl1.setSoTimeout(3000);
            byte[] buf = new byte[1024];
            int leidos;
            
            while((leidos = bis.read(buf, 0, buf.length)) != -1){
                bos.write(buf,0,leidos);
                bos.flush();
            }

            bis.close();
            bos.close();
            System.out.println("Archivo subido exitosamente");

        
        }catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        
    }

    public void Descargar(){
        
    }

    public void Listar(){
        
    }

    public void Eliminar(){
        
    }

    public void Conectar(){
        try
        {
            cl1 = new Socket("localhost", 3000);
            System.out.println("Conexion con el servidor");
            Menu();
        }
        catch(Exception ex){
            System.out.println("No se conecto"); 
        }
    }


    public static void main(String[] args) {
        Cliente cl = new Cliente();
        cl.Conectar();
    }


}
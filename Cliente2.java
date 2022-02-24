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
            file.setMultiSelectionEnabled(true);
            file.showOpenDialog(null);
            File [] files = file.getSelectedFiles();
            int archivos = files.length;
            
            for(int i = 0; i< archivos; i++){
                
                Socket enviar = new Socket(InetAddress.getByName("127.0.0.1"), 4000);
                String nombre = files[i].getName();
                String path = files[i].getAbsolutePath();
                long tam = files[i].length();

                DataOutputStream dos = new DataOutputStream(enviar.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(path));
                
                dos.writeUTF(files[i].getName());
                dos.flush();
                dos.writeLong(tam);
                dos.flush();

                long enviados = 0;
                int l=0, porcentaje = 0;

                while(enviados < tam){
                    byte[] b = new byte[1500];
                    l=dis.read(b);
                    System.out.println("enviados: "+l);
                    dos.write(b,0,l);
                    dos.flush();
                    enviados = enviados + l;
                    porcentaje = (int)((enviados*100)/tam);
                    System.out.print("\rEnviado el "+porcentaje+" % del archivo");
                }

                dis.close();
                dos.close();
                enviar.close();
                System.out.println("Archivo subido exitosamente");
            }
        
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
            cl1 = new Socket(InetAddress.getByName("127.0.0.1"), 4000);
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

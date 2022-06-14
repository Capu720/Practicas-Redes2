import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.MalformedURLException;
import java.net.URL;


public class Wget extends Thread {
    URL url;
    String path;
    private final ExecutorService pool;

    //Contructor
    public Wget(String url, String path, int opcion)
    {
        pool = Executors.newFixedThreadPool(10);
        try{
            this.url = new URL(url);
            this.path = path;
            if(opcion == 1){
                procesoUrl();
            }
            else{
                writeData(new File(path + "/" + getName(url)));
            }
        }catch(Exception ex){
            pool.shutdown();
            ex.printStackTrace();
        }
        pool.shutdown();
    }

    public Wget(String url, String path, final ExecutorService pool, int opcion)
    {
        this.pool = pool;
        try
        {
            this.url = new URL(url);
            this.path = path;
            if(opcion == 1)
            {
                procesoUrl();
            }
            else
            {
                writeData(new File(path + "/index.html"));
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void procesoUrl()
    {
        if(url.getFile().indexOf(".") == -1){
            isDir();
        }
        else{
            writeData(new File(path + getName(url.getFile())));
        }
    }

    public void writeData(File file)
    {
        try
        {
            System.out.println("Descargando el archivo proveniente de: " + file.getName());
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
            DataInputStream dis = new DataInputStream(url.openStream());
            byte[] bytes = new byte[65535];
            int read = dis.read(bytes);
            while(read != -1)
            {
                dos.write(bytes, 0, read);
                read = dis.read(bytes);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private String getName(String file)
    {
        String[] x = file.split("/");
        return x[x.length - 1];
    }

    private void isDir()
    {
        String pare = "PARENTDIR";
        File file = new File(path + getName(url.getFile()));
        if(file.mkdir())
        {
            path = file.getAbsolutePath() + "\\";
            try
            {
                DataInputStream dis = new DataInputStream(url.openStream());
                byte[] bytes = new byte[65535];
                int read = dis.read(bytes);
                String str = "";
                while(read != -1)
                {
                    str += new String(bytes, 0, read);
                    read = dis.read();
                }

                int index = str.indexOf(pare);
                if(index == -1)
                {
                    System.out.println(getName("Error " + url.getFile()) + "No encontrado");
                    System.out.println("Obteniendo bytes.");
                    pool.execute(new Wget(url.toString(), path, pool, 2));
                    return;
                }
                index = str.indexOf("href", index);
                pool.execute(new Wget(url.toString(), path, pool, 2));

                while((index = str.indexOf("href",index+1)) != -1)
                {
                    String next = str.substring(index + 6, str.indexOf("\"", index + 6));
                    pool.execute(new Wget(url.toString() + next, path, pool, 1));
                }

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        else{
            System.out.println("Error: " + file.getAbsolutePath());
        }
    }

    public static void main(String[] args)
    {
        if(args.length > 0){
            Wget wg = new Wget(args[0], "", 1);
        }
        else{
            Scanner scanner = new Scanner(System.in);
            System.out.println("Ingresa la URL:");
            String url = scanner.nextLine();
            Wget wg = new Wget(url, "", 1);
        }
    }
}

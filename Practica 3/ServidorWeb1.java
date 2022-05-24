import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorWeb1 {
    // Propiedades
    public static final int PUERTO = 8000;
    ServerSocket ss;

    class Manejador implements Runnable {
        protected Socket socket;
        protected PrintWriter pw;
        protected BufferedOutputStream bos;
        protected BufferedReader br;
        DataOutputStream dos;
        DataInputStream dis;
        protected String FileName;

        public Manejador(Socket _socket) throws Exception {
            this.socket = _socket;
        }

        @Override
        public void run() {
            try {
                File f = new File("");
                String ruta = f.getAbsolutePath();
                String carpeta = "archivos";
                String ruta_archivos = ruta + "\\" + carpeta + "\\";
                File f2 = new File(ruta_archivos);
                f2.mkdirs();
                f2.setWritable(true);
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());
                // String line=br.readLine();
                byte[] b = new byte[1024];
                int t = dis.read(b);
                String peticion = new String(b, 0, t);
                System.out.println("t: " + t);

                if (peticion == null) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("<html><head><title>Servidor WEB\n");
                    sb.append("</title><body bgcolor=\"#AACCFF\"<br>Linea Vacia</br>\n");
                    sb.append("</body></html>\n");
                    dos.write(sb.toString().getBytes());
                    dos.flush();
                    socket.close();
                    return;
                }

                System.out.println("\nCliente Conectado desde: " + socket.getInetAddress());
                System.out.println("Por el puerto: " + socket.getPort());
                System.out.println("Datos:\n" + peticion + "\r\n\r\n");
                StringTokenizer st1 = new StringTokenizer(peticion, "\n");
                String line = st1.nextToken();
                String method = line.split(" ")[0];

                // DELETE ---------------------------------------------------------------------------------------------------
                if (line.toUpperCase().startsWith("DELETE")) {
                    System.out.println("\n\n\tMetodo " + method);
                    String parametros = st1.nextToken();

                    while (st1.hasMoreTokens())
                        parametros = st1.nextToken();

                    if (parametros.indexOf(".") < 0)
                        parametros += ".txt";

                    File delFile = new File("archivos/" + parametros);
                    
                    if (delFile.delete()) {
                        System.out.println("Deleted the file: " + delFile.getName());
                        StringBuffer respuesta = new StringBuffer();
                        respuesta.append("HTTP/1.0 301 Okay \n");
                        respuesta.append("Location: http://127.0.0.1/\n");
                        String fecha = "Date: " + new Date() + " \n";
                        respuesta.append(fecha);
                        String tipo_mime = "Content-Type: text/html\n\n";
                        respuesta.append(tipo_mime);
                        respuesta.append("<html><head><title>SERVIDOR WEB</title></head>\n");
                        respuesta.append("<body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos.. (Metodo " + method + ")</br></h1><h3><b>\n");
                        respuesta.append(parametros);
                        respuesta.append("</b></h3>\n");
                        respuesta.append("</center></body></html>\n\n");
                        System.out.println("Respuesta:\n" + respuesta);
                        dos.write(respuesta.toString().getBytes());
                    } 
                    else {
                        System.out.println("Failed to delete the file.");
                    }
                    dos.flush();
                    dos.close();
                    socket.close();
                } 
                // PUT ---------------------------------------------------------------------------------------------------------------
                else if (line.toUpperCase().startsWith("PUT")) {
                    System.out.println("\n\n\tMetodo " + method);
                    String parametros = st1.nextToken();

                    while (st1.hasMoreTokens())
                        parametros = st1.nextToken();

                    if (parametros.indexOf(".") < 0)
                        parametros += ".txt";

                    File newFile = new File("archivos/" + parametros);

                    if (newFile.createNewFile()) {
                        System.out.println("File created: " +
                        newFile.getName());
                        StringBuffer respuesta = new StringBuffer();
                        respuesta.append("HTTP/1.0 301 Okay \n");
                        respuesta.append("Location: http://127.0.0.1/\n");
                        String fecha = "Date: " + new Date() + " \n";
                        respuesta.append(fecha);
                        String tipo_mime = "Content-Type: text/html\n\n";
                        respuesta.append(tipo_mime);
                        respuesta.append("<html><head><title>SERVIDOR WEB</title></head>\n");
                        respuesta.append("<body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos.. (Metodo " + method + ")</br></h1><h3><b>\n");
                        respuesta.append(parametros);
                        respuesta.append("</b></h3>\n");
                        respuesta.append("</center></body></html>\n\n");
                        System.out.println("Respuesta:\n" + respuesta);
                        dos.write(respuesta.toString().getBytes());
                    } 
                    else {
                        System.out.println("File already exists.");
                    }
                    dos.flush();
                    dos.close();
                    socket.close();
                }
                // POST ------------------------------------------------------------------------------------------------------------------------------------ 
                else if (line.toUpperCase().startsWith("POST")) {
                    System.out.println("\n\n\tMetodo " + method);
                    String parametros = st1.nextToken();

                    while (st1.hasMoreTokens()) {
                        if (parametros.startsWith("Apellido="))
                            break;
                        parametros = st1.nextToken();
                    }

                    while (st1.hasMoreTokens())
                        parametros += "&" + st1.nextToken();

                    StringBuffer respuesta = new StringBuffer();
                    respuesta.append("HTTP/1.0 200 Okay \n");
                    String fecha = "Date: " + new Date() + " \n";
                    respuesta.append(fecha);
                    String tipo_mime = "Content-Type: text/html \n\n";
                    respuesta.append(tipo_mime);
                    respuesta.append("<html><head><title>SERVIDOR WEB</title></head>\n");
                    respuesta.append("<body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos.. (Metodo " + method + ")</br></h1><h3><b>\n");
                    respuesta.append(parametros);
                    respuesta.append("</b></h3>\n");
                    respuesta.append("</center></body></html>\n\n");
                    System.out.println("Respuesta:\n" + respuesta);
                    dos.write(respuesta.toString().getBytes());
                    dos.flush();
                    dos.close();
                    socket.close();
                }
                // HEAD ---------------------------------------------------------------------------------------------------
                else if (line.toUpperCase().startsWith("HEAD")) {
                    StringTokenizer tokens = new StringTokenizer(line, "?");
                    String req_a = tokens.nextToken();
                    System.out.println("Metodo: " + req_a);
                    //System.out.println("parametros: " + parametros);
                    StringBuffer respuesta = new StringBuffer();
                    respuesta.append("HTTP/1.0 200 Okay \n");
                    String fecha = "Date: " + new Date() + " \n";
                    respuesta.append(fecha);
                    String tipo_mime = "Content-Type: text/html \n\n";
                    respuesta.append(tipo_mime);
                    System.out.println("Respuesta: " + respuesta);
                    dos.write(respuesta.toString().getBytes());
                    dos.flush();
                    dos.close();
                    socket.close();
                } 
                else if (line.indexOf("?") == -1) {
                    getArch(line);
                    if (FileName.compareTo("") == 0) {
                        SendA("index.htm", dos);
                    } else {
                        SendA(FileName, dos);
                    }
                    // System.out.println(FileName);
                } 
                else {
                    dos.write("HTTP/1.0 501 Not Implemented\r\n".getBytes());
                    dos.flush();
                    dos.close();
                    socket.close();
                    // pw.println();
                }
                // dos.flush();
                // bos.flush();
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }// run

        public void getArch(String line) {
            int i;
            int f;

            if (line.toUpperCase().startsWith("GET")) {
                i = line.indexOf("/");
                f = line.indexOf(" ", i);
                FileName = line.substring(i + 1, f);
            }
        }

        public void SendA(String fileName, Socket sc, DataOutputStream dos) throws Exception{
            // System.out.println(fileName);
            int fSize = 0;
            byte[] buffer = new byte[4096];

            try {
                // DataOutputStream out =new DataOutputStream(sc.getOutputStream());
                // sendHeader();
                DataInputStream dis1 = new DataInputStream(new
                FileInputStream(fileName));
                // FileInputStream f = new FileInputStream(fileName);
                int x = 0;
                File ff = new File("fileName");
                long tam, cont = 0;
                tam = ff.length();

                while (cont < tam) {
                    x = dis1.read(buffer);
                    dos.write(buffer, 0, x);
                    cont = cont + x;
                    dos.flush();
                }
                // out.flush();
                dis.close();
                dos.close();
            } 
            catch (FileNotFoundException e) {
                /*msg.printErr("Transaction::sendResponse():1", "El archivo no existe: " + fileName);
                } catch (IOException e) {
                // System.out.println(e.getMessage());
                // msg.printErr("Transaction::sendResponse():2", "Error en la
                lectura del
                // archivo: " + fileName);*/
            }
        }

        public void SendA(String arg, DataOutputStream dos1) {
            try {
                int b_leidos = 0;
                DataInputStream dis2 = new DataInputStream(new FileInputStream(arg));
                // BufferedInputStream bis2=new BufferedInputStream(newFileInputStream(arg));
                byte[] buf = new byte[1024];
                int x = 0;
                File ff = new File(arg);
                long tam_archivo = ff.length(), cont = 0;
                /***********************************************/
                String sb = "";
                sb = sb + "HTTP/1.0 200 ok\n";
                sb = sb + "Server: Axel Server/1.0 \n";
                sb = sb + "Date: " + new Date() + " \n";
                sb = sb + "Content-Type: text/html \n";
                sb = sb + "Content-Length: " + tam_archivo + " \n";
                sb = sb + "\n";
                dos1.write(sb.getBytes());
                dos1.flush();
                /***********************************************/
                while (cont < tam_archivo) {
                x = dis2.read(buf);
                dos1.write(buf, 0, x);
                cont = cont + x;
                dos1.flush();
                }
                // bos.flush();
                dis2.close();
                dos1.close();
            } 
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    } // Class Manejador

    public ServidorWeb1() throws Exception {
        System.out.println("Iniciando Servidor.......");
        this.ss = new ServerSocket(PUERTO);
        System.out.println("Servidor iniciado:---OK");
        System.out.println("Esperando por Cliente....");
        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (;;) {
            Socket accept = ss.accept();
            // new Manejador(accept).start();
            pool.execute(new Manejador(accept));
        }
    }

    public static void main(String[] args) throws Exception {
        new ServidorWeb1();
    }
}
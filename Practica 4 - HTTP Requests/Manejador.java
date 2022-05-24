import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Base64;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Manejador extends Thread {

    protected Socket cl;
    protected DataOutputStream dos;
    protected Mime mime;
    protected DataInputStream dis;

    public Manejador(Socket cl) throws Exception {
        this.cl = cl;
        this.dos = new DataOutputStream(this.cl.getOutputStream());
        this.mime = new Mime();
        this.dis = new DataInputStream(this.cl.getInputStream());
    }

    public void eliminarRecurso(String arg, String headers) {
        try {
            System.out.println(arg);
            File f = new File(arg);

            if (f.exists()) {
                if (f.delete()) {
                    System.out.println("Archivo " + arg + " eliminado exitosamente\n");

                    String deleteOK = headers
                            + "<html><head><meta charset='UTF-8'><title>202 OK Recurso eliminado</title></head>"
                            + "<body><h1>202 OK Recurso eliminado exitosamente.</h1>"
                            + "<p>El recurso " + arg + " ha sido eliminado permanentemente del servidor.</p>"
                            + "</body></html>";

                    dos.write(deleteOK.getBytes());
                    dos.flush();
                    System.out.println("Respuesta DELETE: \n" + deleteOK);
                } else {
                    System.out.println("El archivo " + arg + " no pudo ser borrado\n");

                    String error404 = "HTTP/1.1 404 Not Found\n"
                            + "Date: " + new Date() + " \n"
                            + "Server: MarCarlos Server/1.0 \n"
                            + "Content-Type: text/html \n\n"
                            + "<html><head><meta charset='UTF-8'><title>404 Not found</title></head>"
                            + "<body><h1>404 Not found</h1>"
                            + "<p>Archivo " + arg + " no encontrado.</p>"
                            + "</body></html>";

                    dos.write(error404.getBytes());
                    dos.flush();
                    System.out.println("Respuesta DELETE - ERROR 404: \n" + error404);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void enviarRecurso(String arg, int bandera) {
        try {
            File f = new File(arg);
            String sb = "HTTP/1.1 200 OK\n";

            if (!f.exists()) {
                arg = "404.html";
                sb = "HTTP/1.1 404 Not Found\n";
            } else if (f.isDirectory()) {
                arg = "403.html";
                sb = "HTTP/1.1 403 Forbidden\n";
            }

            DataInputStream dis2 = new DataInputStream(new FileInputStream(arg));
            int tam = dis2.available();

            int pos = arg.indexOf(".");
            String extension = arg.substring(pos + 1, arg.length());
            sb = sb + "Date: " + new Date() + " \n"
                    + "Server: MarCarlos Server/1.0 \n"
                    + "Content-Type: " + mime.get(extension) + " \n"
                    + "Content-Length: " + tam + " \n\n";

            dos.write(sb.getBytes());
            dos.flush();

            String metodo = "HEAD";
            if (bandera == 1) {
                metodo = "GET";
                byte[] b = new byte[1024];
                long enviados = 0;
                int n = 0;

                while (enviados < tam) {
                    n = dis2.read(b);
                    dos.write(b, 0, n);
                    dos.flush();
                    enviados += n;
                }
            }
            System.out.println("Respuesta " + metodo + ": \n" + sb);
            dis2.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String obtenerNombreRecurso(String line) {
        int i = line.indexOf("/");
        int f = line.indexOf(" ", i);
        String resourceName = line.substring(i + 1, f);

        if (resourceName.compareTo("") == 0) {
            resourceName = "index.html";
        }

        return resourceName;
    }

    public String obtenerParametros(String line, String headers, int bandera) {
        String metodo = "POST";
        String request2 = line;

        if (bandera == 0) {
            metodo = "GET";
            StringTokenizer tokens = new StringTokenizer(line, "?");
            String request = tokens.nextToken();
            request = tokens.nextToken();
            StringTokenizer tokens2 = new StringTokenizer(request, " ");
            request2 = tokens2.nextToken();
        }

        StringTokenizer paramsTokens = new StringTokenizer(request2, "&");
        String html = headers
                + "<html><head><meta charset='UTF-8'><title>Metodo " + metodo + "\n"
                + "</title></head><body bgcolor='#AACCFF'><center><h2>Parametros obtenidos por medio de " + metodo + "</h2><br>\n";

        while (paramsTokens.hasMoreTokens()) {
            String parametros = paramsTokens.nextToken();
            StringTokenizer paramValue = new StringTokenizer(parametros, "=");
            String param = "";
            String value = "";

            if (paramValue.hasMoreTokens()) {
                param = paramValue.nextToken();
            }

            if (paramValue.hasMoreTokens()) {
                value = paramValue.nextToken();
            }

            html = html + "<b>" + param + "</b>&nbsp&nbsp-&nbsp&nbsp" + value + "<br>\n";
        }
        html = html + "</center></body></html>";
        return html;
    }

    @Override
    public void run() {
        String headers = "HTTP/1.1 200 OK\n"
                + "Date: " + new Date() + " \n"
                + "Server: MarCarlos Server/1.0 \n"
                + "Content-Type: text/html \n\n";
        try {
            String line = dis.readLine();
            
            if (line == null) {
                String vacia = "<html><head><title>Servidor WEB</title><body bgcolor='#AACCFF'>Linea Vacia</body></html>";
                dos.write(vacia.getBytes());
                dos.flush();
            } else {
                System.out.println("\nCliente Conectado desde: " + cl.getInetAddress() + "\n");
                System.out.println("Por el puerto: " + cl.getPort());
                System.out.println("Datos: " + line + "\n");

                if (line.toUpperCase().startsWith("GET")) {
                    if (line.indexOf("?") == -1) {
                        String fileName = obtenerNombreRecurso(line);
                        enviarRecurso(fileName, 1);
                    } else {
                        String respuesta = obtenerParametros(line, headers, 0);
                        dos.write(respuesta.getBytes());
                        dos.flush();
                        System.out.println("Respuesta GET: \n" + respuesta);
                    }
                } else if (line.toUpperCase().startsWith("HEAD")) {
                    if (line.indexOf("?") == -1) {
                        String fileName = obtenerNombreRecurso(line);
                        enviarRecurso(fileName, 0);
                    } else {
                        dos.write(headers.getBytes());
                        dos.flush();
                        System.out.println("Respuesta HEAD: \n" + headers);
                    }
                } else if (line.toUpperCase().startsWith("POST")) {
                    int tam = dis.available();
                    byte[] b = new byte[tam];
                    dis.read(b);
                    String request = new String(b, 0, tam);
                    String[] reqLineas = request.split("\n");
                    int ult = reqLineas.length - 1;
                    String respuesta = obtenerParametros(reqLineas[ult], headers, 1);
                    dos.write(respuesta.getBytes());
                    dos.flush();
                    System.out.println("Respuesta POST: \n" + respuesta);
                }
                else if (line.toUpperCase().startsWith("DELETE")) {
                    int tam = dis.available();
                    byte[] b = new byte[tam];
                    dis.read(b);
                    String request = new String(b, 0, tam);
                    String[] reqLineas = request.split("\n");
                    int ult = reqLineas.length - 1;
                    String request2 = reqLineas[ult];
                    StringTokenizer paramsTokens = new StringTokenizer(request2, "&");
                    String archivo = "";

                    while (paramsTokens.hasMoreTokens()) {
                        String parametros = paramsTokens.nextToken();
                        StringTokenizer paramValue = new StringTokenizer(parametros, "=");
                        
                        if (paramValue.hasMoreTokens()) {
                            archivo = paramValue.nextToken();
                        }
                    }
                    archivo = archivo + ".txt";
                    String ruta = "archivos/put/" + archivo;
                    eliminarRecurso(ruta, headers);
                } else if (line.toUpperCase().startsWith("PUT")) {
                    int tam = dis.available();
                    byte[] b = new byte[tam];
                    dis.read(b);
                    String request = new String(b, 0, tam);
                    String[] reqLineas = request.split("\n");
                    int ult = reqLineas.length - 1;
                    String request2 = reqLineas[ult];
                    StringTokenizer paramsTokens = new StringTokenizer(request2, "&");
                    String archivo = "";

                    while (paramsTokens.hasMoreTokens()) {
                        String parametros = paramsTokens.nextToken();
                        StringTokenizer paramValue = new StringTokenizer(parametros, "=");

                        if (paramValue.hasMoreTokens()) {
                            archivo = paramValue.nextToken();
                        }
                    }
                    System.out.println(archivo);
                    archivo = archivo + ".txt";
                    String ruta = "archivos/put/" + archivo;

                    try {
                        String contenido = "Archivo creado exitosamente";
                        File file = new File(ruta);
                        if (!file.exists()) {
                            file.createNewFile();
                        }else{
                            headers = "HTTP/1.1 204 OK\n" + "Date: " + new Date() + " \n" + "Server: MarCarlos Server/1.0 \n" + "Content-Type: text/html \n\n";
                        }
                        FileWriter fw = new FileWriter(file);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(contenido);
                        bw.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dos.write(headers.getBytes());
                    dos.flush();
                    System.out.println("Respuesta PUT: \n" + headers);
                }

            
            else {
                    String error501 = "HTTP/1.1 501 Not Implemented\n" +
                    				  "Date: " + new Date() + " \n" +
		              				  "Server: MarCarlos Server/1.0 \n" +
		              				  "Content-Type: text/html \n\n" +

	        						  "<html><head><meta charset='UTF-8'><title>Error 501</title></head>" +
	        						  "<body><h1>Error 501: No implementado.</h1>" +
	        						  "<p>El metodo HTTP o funcionalidad solicitada no esta implementada en el servidor.</p>" +
	        						  "</body></html>";

                    dos.write(error501.getBytes());
                    dos.flush();
                    System.out.println("Respuesta ERROR 501: \n" + error501);
                }
        }
        dis.close();
        dos.close();
        cl.close();
    }
    catch(Exception e) {
            e.printStackTrace();
    }
}
}

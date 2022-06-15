import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.*;

public class Interfaz extends JFrame {
    private static final long serialVersionUID = 2L;
    private static ImageCache image_cache;
    Emoji emoji =  new Emoji();

    public Interfaz(String host, int puerto, String nombre) {
        // -----------------Recibiendo Parametros------------------//
        this.host = host;
        this.puerto = puerto;
        this.nombre = nombre;
        // ----------------Creando Interfaz-------------------------//
        setBounds(325, 100, 800, 500);
        setTitle("Practica 3: " + nombre);
        setResizable(false);

        panelPrincipal = new JPanel();
        panelCentral = new JPanel();
        panelInferior = new JPanel();
        panelEmojis = new JPanel();
        panelFunciones = new JPanel();
        panelUsuarios = new JPanel();
        panelCombo = new JPanel();
        editor = new JEditorPane("text/html", null);
        editor.setEditable(false);
        areaMensaje = new JTextArea();
        areaMensaje.setLineWrap(true);
        botonesEmojis = new JButton[12];
        enviar = new JButton("Enviar");
        archivo = new JButton("Archivo");
        desconectar = new JButton("Desconectar");
        //seleccion = new JButton("Seleccionar");
        usuariosConectados = new JLabel("    Usuarios Conectados   ");
        //escuchaEmojis = new ManejoEmojis();
        usuarioConectado = new JComboBox<>();
        usuarioConectado.addItem("Todos");
        
        File aux = new File("");
        String rutaEmojis = aux.getAbsolutePath() + "\\Emojis\\";
        System.out.println(rutaEmojis);
        int j = 0;
        File carpetaEmojis = new File(rutaEmojis);
        String[] nomEmojis = new String[12];
        for(File file: carpetaEmojis.listFiles()){
            if(!file.isDirectory()){
                System.out.println(file.getName());
                nomEmojis[j] = file.getName();
                j++;
            }
        }
        
        for(int i=0; i<12; i++)
        {
            botonesEmojis[i] = new JButton();
            botonesEmojis[i].setBounds(400,480,20,20);
            ImageIcon icono = new ImageIcon(rutaEmojis + nomEmojis[i]);
            botonesEmojis[i].setIcon(new ImageIcon(icono.getImage().getScaledInstance(botonesEmojis[i].getWidth(), botonesEmojis[i].getHeight(), Image.SCALE_SMOOTH)));
            panelEmojis.add(botonesEmojis[i]);
        }
        
        desconectar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                miCliente.EliminarConexion(nombre);
                System.exit(0);
            }
        });

        botonesEmojis[0].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[0]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });
        botonesEmojis[1].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[1]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });
        botonesEmojis[2].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[2]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });
        botonesEmojis[3].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[3]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });
        botonesEmojis[4].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[4]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });
        botonesEmojis[5].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[5]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });
        botonesEmojis[6].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[6]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });
        botonesEmojis[7].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[7]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });
        botonesEmojis[8].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[8]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });
        botonesEmojis[9].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[9]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });
        botonesEmojis[10].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[10]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });
        botonesEmojis[11].addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 5;
                if(destino == "Todos"){
                    System.out.println("Seleccion "+destino);
                    destino = "";
                    tipo = 5;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 6;
                }
                URL imgurl = null;
                image_cache = new ImageCache();
                try{
                    imgurl = new File(rutaEmojis + nomEmojis[11]).toURI().toURL();
                    Image img = Toolkit.getDefaultToolkit().createImage(imgurl);
                    image_cache.put(imgurl,img);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            
                String mensajeEmo = "<html>" +
                "<body>"+
                "<img src=\"" + imgurl.toString() + "\"width='20' heigth='20'>" +
                "</body>" +
                "</html>";
                miCliente.enviar(new Mensaje(mensajeEmo,nombre,destino,tipo));
                areaMensaje.setText("");
            }
        });



        enviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 4;
                if(destino == "Todos"){
                    System.out.println("Selección "+destino);
                    destino = "";
                    tipo = 1;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 4;
                }
                    
                miCliente.enviar(new Mensaje("[" + nombre + "]: " + areaMensaje.getText(), nombre, destino, tipo));
                areaMensaje.setText("");
            }
        });

        archivo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 4;
                if(destino == "Todos"){
                    System.out.println("Selección "+destino);
                    destino = "";
                    tipo = 1;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 4;
                }
                JFileChooser jf = new JFileChooser();
                jf.requestFocus();
                int r = jf.showOpenDialog(Interfaz.this);
                if (r == JFileChooser.APPROVE_OPTION) {
                    //Archivo
                    miCliente.enviarArchivo(jf.getSelectedFile(),destino);
                    //Solo es el aviso
                    String mensaje = "El usuario [" + nombre + "] ha compartido un archivo";
                    miCliente.enviar(new Mensaje(mensaje, nombre, destino, tipo));
                }
            }
        });

 
        panelEmojis.setLayout(new GridLayout(4,3));
        panelPrincipal.setLayout(new BorderLayout(5, 5));
        panelCentral.setLayout(new BorderLayout(5, 5));
        panelInferior.setLayout(new BoxLayout(this.panelInferior, BoxLayout.Y_AXIS));
        panelFunciones.setLayout(new BoxLayout(this.panelFunciones, BoxLayout.X_AXIS));
        panelUsuarios.setLayout(new BorderLayout(5, 5));
        

        //colocarBotones();
        addWindowListener(new CorreCliente());
        panelCombo.add(usuarioConectado);
        panelUsuarios.add(usuariosConectados, BorderLayout.NORTH);
        usuariosConectados.setAlignmentX(SwingConstants.CENTER);
        panelUsuarios.add(panelCombo, BorderLayout.CENTER);
        //panelUsuarios.add(seleccion, BorderLayout.SOUTH);
        panelCentral.add(new JScrollPane(editor), BorderLayout.CENTER);
        panelCentral.add(panelUsuarios, BorderLayout.EAST);
        panelFunciones.add(new JScrollPane(areaMensaje));
        panelFunciones.add(enviar);
        panelFunciones.add(archivo);
        panelFunciones.add(desconectar);
        //panelInferior.add(panelEmojis);
        panelInferior.add(panelFunciones);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        panelUsuarios.add(panelEmojis, BorderLayout.SOUTH);
        add(panelPrincipal);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class CorreCliente extends WindowAdapter {
        public void windowOpened(WindowEvent we) {
            System.out.println("Ventana abierta");
            miCliente = new Cliente(nombre, host, puerto, editor, usuarioConectado);
            miCliente.saludo(nombre);
        }
    }

    static class ImageCache extends Hashtable {

        public Object get(Object key) {

            Object result = super.get(key);

            if (result == null){
                result = Toolkit.getDefaultToolkit().createImage((URL) key);
                put(key, result);
            }

            return result;
        }
    }


    private String host;
    private int puerto;
    private String nombre;
    private JPanel panelPrincipal;
    private JPanel panelCentral;
    private JPanel panelInferior;
    private JPanel panelFunciones;
    private JPanel panelUsuarios;
    private JPanel panelEmojis; 
    private JEditorPane editor;
    private JTextArea areaMensaje;
    private JButton enviar;
    private JButton archivo;
    private JButton desconectar;
    public static JComboBox<String> usuarioConectado;
    private JLabel usuariosConectados;
    private Cliente miCliente;
    private JPanel panelCombo;
    private JButton[] botonesEmojis;
}
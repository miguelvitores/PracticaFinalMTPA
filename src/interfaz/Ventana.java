package interfaz;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import javax.swing.*;
import practicafinalmtpa.*;

public class Ventana 
        extends JFrame
        implements ActionListener, Constants{
    private Container pane=null;
    private JMenuBar barra=null;
    private JMenu menuArchivo=null;
    private JMenuItem menuiImportar=null;
    private JMenuItem menuiEliminar=null;
    private JMenuItem menuiRename=null;
    private JMenuItem menuiEditConf=null;
    private JMenuItem menuiSalir=null;
    private JButton arrancar=null;
    private JButton parar=null;
    private JPanel panelBotonesArrPar=null;
    private JPanel panelArrancar=null;
    private JPanel panelParar=null;
    private JScrollPane scrollPane=null;
    private final int ANCHO_VENTANA=600;
    private final int ALTO_VENTANA=400;
    private PanelDirectorios panelDirectorios=null;
    private Thread hiloServidor=null;
    private MiServidor servidor=null;
    
    public Ventana() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | 
                IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setSize(ANCHO_VENTANA, ALTO_VENTANA);
        super.setResizable(false);
        pane = super.getContentPane();
        barra = new JMenuBar();
        menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic(KeyEvent.VK_A);
        barra.add(menuArchivo);
        menuiImportar = new JMenuItem("Importar Directorio");
        menuiImportar.addActionListener(this);
        menuiImportar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
        menuArchivo.add(menuiImportar);
        menuiEliminar = new JMenuItem("Eliminar Directorio");
        menuiEliminar.addActionListener(this);
        menuiEliminar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        menuArchivo.add(menuiEliminar);
        menuiRename = new JMenuItem("Renombrar Directorio");
        menuiRename.addActionListener(this);
        menuiRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        menuArchivo.add(menuiRename);
        menuiEditConf = new JMenuItem("Editar Ficheros Config");
        menuiEditConf.addActionListener(this);
        menuiEditConf.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        menuArchivo.add(menuiEditConf);
        menuArchivo.addSeparator();
        menuiSalir = new JMenuItem("Salir");
        menuiSalir.addActionListener(this);
        menuArchivo.add(menuiSalir);
        super.setJMenuBar(barra);
        pane.setLayout(new GridLayout(2, 1));
        panelBotonesArrPar = new JPanel(new GridLayout(1, 2));
        panelArrancar = new JPanel();
        panelArrancar.setLayout(new GridLayout(3, 1));
        panelArrancar.setBackground(new Color(46, 245, 24, 100));
        arrancar = new JButton("Arrancar");
        for(int i=0;i<3;i++){
            if(i==1)
                panelArrancar.add(arrancar);
            else
                panelArrancar.add(new Component(){});
        }
        panelParar = new JPanel();
        panelParar.setBackground(new Color(233, 11, 37, 100));
        parar = new JButton("Parar");
        panelParar.setLayout(new GridLayout(3, 1));
        for(int i=0;i<3;i++){
            if(i==1)
                panelParar.add(parar);
            else
                panelParar.add(new Component(){});
        }
        panelDirectorios = new PanelDirectorios();
        scrollPane = new JScrollPane(panelDirectorios, 
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelBotonesArrPar.add(panelArrancar);
        panelBotonesArrPar.add(panelParar);
        pane.add(panelBotonesArrPar);
        pane.add(scrollPane);
        arrancar.addActionListener(this);
        parar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==menuiImportar)
            importarDir();
        else if(e.getSource()==menuiEliminar)
            eliminarDir();
        else if(e.getSource()==menuiRename)
            renameDir();
        else if(e.getSource()==menuiEditConf)
            editarConf();
        else if(e.getSource()==arrancar)
            arrancar();
        else if(e.getSource()==parar)
            parar();
        else if(e.getSource()==menuiSalir)
            salir();
    }

    public void salir(){
        System.exit(0);
    }
    
    
    public void importarDir(){
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.setCurrentDirectory(new File("D:/"));
        f.setDialogTitle("Importar Directorio");
        int v = f.showOpenDialog(this);
        if(v == JFileChooser.APPROVE_OPTION){
            try {
                Path carpetaOrigen = f.getSelectedFile().toPath();
                String alias;
                do{
                    alias = (String)JOptionPane.showInputDialog(this, "Elija un alias");
                }while(Files.isDirectory(Paths.get(FILES_PATH+alias)));
                if(alias!=null){
                    Path carpetaDestino = Paths.get(FILES_PATH+alias);
                    copiarDirectorio(carpetaOrigen, carpetaDestino);
                    WebAppConfig config = pedirConfig(alias);
                    config.editConfig();
                    panelDirectorios.addDirectory(alias);
                    JOptionPane.showMessageDialog(this, "Directorio Importado con Éxito",
                            "Importar Directorio", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {ex.printStackTrace();}
        }
    }
    
    private void copiarDirectorio(Path carpetaOrigen, Path carpetaDestino) throws IOException{
        Files.createDirectory(carpetaDestino);
        Files.walkFileTree(carpetaOrigen, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String sufijo = file.toString().replace(carpetaOrigen.toString(), "");
                Files.copy(file, Paths.get(carpetaDestino.toString()+sufijo), StandardCopyOption.COPY_ATTRIBUTES);
                return super.visitFile(file, attrs);
            }
        });
    }
    
    private WebAppConfig pedirConfig(String alias){
        String index=" ";
        String errPage=" ";
        boolean gzip=false;
        boolean dirStruct=false;
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.FILES_ONLY);
        f.setCurrentDirectory(new File(FILES_PATH+alias));
        f.setDialogTitle("Selecciona un index en html");
        int v = f.showDialog(this, "Seleccionar");
        if(v == JFileChooser.APPROVE_OPTION){
            Path rutaIndex = f.getSelectedFile().toPath();
            index = rutaIndex.getName(rutaIndex.getNameCount()-1).toString();
        }
        if(index.equals(" "))
            index="-";
        f.setFileSelectionMode(JFileChooser.FILES_ONLY);
        f.setCurrentDirectory(new File(FILES_PATH+alias));
        f.setDialogTitle("Selecciona un error page");
        v = f.showDialog(this, "Seleccionar");
        if(v == JFileChooser.APPROVE_OPTION){
            Path rutaPagError = f.getSelectedFile().toPath();
            errPage = rutaPagError.getName(rutaPagError.getNameCount()-1).toString();
        }
        if(errPage.equals(" "))
            errPage="-";
        dirStruct = (JOptionPane.showOptionDialog(this, "¿Activar Arbol de Directorios?", "Elije", JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION );
        gzip = (JOptionPane.showOptionDialog(this, "¿Generar Respuesta en Gzip?", "Elije", JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION );
        return new WebAppConfig(index, errPage, gzip, dirStruct, alias);
    }
    
    private void editarConf(){
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.setCurrentDirectory(new File(FILES_PATH));
        f.setDialogTitle("Editar Configuracion");
        int v = f.showDialog(this, "Config");
        if(v == JFileChooser.APPROVE_OPTION){
            Path carpeta = f.getSelectedFile().toPath();
            String alias = carpeta.getName(carpeta.getNameCount()-1).toString();
            WebAppConfig config = pedirConfig(alias);
            config.editConfig();
        }
    }
    
    public boolean eliminarDir(){
        try{
            JFileChooser f = new JFileChooser();
            f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            f.setCurrentDirectory(new File(FILES_PATH));
            f.setDialogTitle("Eliminar Directorio");
            int v = f.showDialog(this, "Eliminar");
            if(v == JFileChooser.APPROVE_OPTION){
                Path carpeta = f.getSelectedFile().toPath();
                String alias = carpeta.getName(carpeta.getNameCount()-1).toString();
                eliminarDirectorio(carpeta);
                panelDirectorios.removeDirectory(alias);
                JOptionPane.showMessageDialog(this, "Directorio Eliminado con Éxito",
                        "Eliminar Directorio", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    
    private void eliminarDirectorio(Path carpeta) throws IOException{
        Files.walkFileTree(carpeta, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.deleteIfExists(file);
                return super.visitFile(file, attrs);
            }
        });
        Files.deleteIfExists(carpeta);
    }
    
    public void renameDir(){
        try{
            JFileChooser f = new JFileChooser();
            f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            File files = new File(FILES_PATH);
            f.setCurrentDirectory(files);
            f.setDialogTitle("Renombrar Directorio");
            int v = f.showDialog(this, "Renombrar");
            if(v == JFileChooser.APPROVE_OPTION){
                Path carpetaOrigen = f.getSelectedFile().toPath();
                String aliasAntiguo = carpetaOrigen.getName(carpetaOrigen.getNameCount()-1).toString();
                String aliasNuevo = (String)JOptionPane.showInputDialog(this, "Elija un nuevo nombre del alias seleccionado");
                Path carpetaDestino = Paths.get(FILES_PATH+aliasNuevo);
                copiarDirectorio(carpetaOrigen, carpetaDestino);
                Files.copy(Paths.get(FILES_PATH+aliasAntiguo+aliasAntiguo+".conf"), 
                        Paths.get(FILES_PATH+aliasNuevo+aliasNuevo+".conf"));
                eliminarDirectorio(carpetaOrigen);
                panelDirectorios.renameDirectory(aliasAntiguo, aliasNuevo);
                JOptionPane.showMessageDialog(this, "Directorio Renombrado con Éxito",
                    "Renombrar Directorio", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void arrancar(){
        if(servidor==null)
            servidor = new MiServidor();
        if( servidor.isParado() ){
            servidor.setParado(false);
            hiloServidor = new Thread(servidor);
            hiloServidor.start();
        }
    }
    
    public void parar(){
        if(servidor!=null){
            if( ! servidor.isParado() )
                servidor.setParado(true);
        }
    }
   
    
}


package practicafinalmtpa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

public class MiServidor
        implements Runnable, Constants{
    
    private static int numPeticiones;
    private boolean parado=true;
    private ArrayList<WebAppConfig> configuraciones;
    private ThreadPoolExecutor threadPool;
    private final int MAX_HILOS=10;

    @Override
    public void run() {
        try {
            int puerto = 80;
            ServerSocket serverSocket = new ServerSocket(puerto);
            serverSocket.setSoTimeout(500);
            System.out.println("Arrancando servidor en el puerto:" + puerto);
            System.out.println("Esperando peticiones...");
            threadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(MAX_HILOS);
            Socket s = null;
            
            //Cargar configuraciones de los directorios
            this.loadConfs();
            
            
            while ( ! parado ){
                try{
                    s = serverSocket.accept();
                    if(isIPBanned(s.getInetAddress())){
                        s.close();
                    }else{
                        numPeticiones++;
                        AdministradorPeticion ap = new AdministradorPeticion(s,configuraciones);
                        threadPool.execute(ap);
                        System.out.println("Hilos activos: "+threadPool.getActiveCount());                        
                    }
                } catch (SocketTimeoutException ex) {
                }
            }
            serverSocket.close();
            s = null;
            System.out.println("Servidor cerrado");
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        
        
        
    }
    private Boolean isIPBanned(InetAddress ip){
        try{
            
        File f1 = new File(IPS_BL);
        BufferedReader br = new BufferedReader(new FileReader(f1));
        String linea;
        while((linea=br.readLine())!= null){            
            if(ip.getHostAddress().equals(linea)){
                return true;
            }
        }
        
        }catch(IOException  ex){System.out.println(ex.getMessage());}
        
        return false;
    }
    public static int getNumPeticiones() {
        return numPeticiones;
    }

    public boolean isParado() {
        return parado;
    }

    public void setParado(boolean parado) {
        this.parado = parado;
    }
    
    private void loadConfs(){
        try{   
            configuraciones = new ArrayList<>();
            Stream<Path> dirsPaths = Files.walk(Paths.get(FILES_PATH), 1);
            for(Object o : dirsPaths.toArray()){    
                Path oPath = (Path)o;
                    if( ! oPath.equals(Paths.get(FILES_PATH)) && Files.isDirectory(oPath)){
                        String alias = oPath.toString().replace(FILES_PATH, "");
                        configuraciones.add(WebAppConfig.leerConf(alias));            
                    }   
            }
        }catch(IOException ex){System.out.println(ex.getMessage());}
                
    }
    
}
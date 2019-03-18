package practicafinalmtpa;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import static java.nio.file.StandardOpenOption.*;
import java.util.ArrayList;
import java.util.stream.Stream;

public class AdministradorPeticion implements Runnable, HTTPProcesamiento, Constants{

    private LogPeticionUsuario log;
    private static String currentAlias = "";
    private Socket cliente;
    private InputStream is;
    private OutputStream os;
    private WebAppConfig wConfig;
    private ArrayList<WebAppConfig> appConfs;
    private static WebAppConfig currentConfig;
    public AdministradorPeticion(Socket cliente, ArrayList<WebAppConfig> configs) {
        try {
            this.cliente = cliente;
            os = this.cliente.getOutputStream();
            is = this.cliente.getInputStream();
            this.appConfs = configs;
        } catch (Exception ex) {
            System.out.println("Error: "+ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            ByteArrayOutputStream mensaje = leerPeticion();
            if( ! (mensaje.size()==0)){
                PeticionHTTP pet = new PeticionHTTP(mensaje);
                synchronized(this){
                    if(currentConfig == null)
                        currentConfig = new WebAppConfig();
                    if(!(wConfig = selectCurrentConf(pet)).getAlias().equals("")) 
                        currentConfig = wConfig;
                }
                    
                procesarPeticion(pet);
            }
            cliente.close();
        } catch (Exception ex) {
            //System.out.println("Error: "+ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public ByteArrayOutputStream leerPeticion() throws Exception {
        int size = 64000;
        byte[] buffer = new byte[size];
        int nb = -1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        do{
            nb = is.read(buffer, 0, size);
            if(nb>0)
                baos.write(buffer, 0, nb);
        }while(nb==size);
        return baos;
    }

    @Override
    public void procesarPeticion(PeticionHTTP pet) throws IOException{
        switch (pet.verbo) {
            case "GET":
                procesarGet(pet);
                break;
            case "DELETE":
                procesarDelete(pet);
                break;
            case "PUT":
                procesarPut(pet);
                break;
            case "POST":
                procesarPost(pet);
                break;
            default:
                os.write(new RespuestaHTTP(501).getData());
                System.out.println("Verbo no implementado");
                break;
        }

    }

    @Override
    public void procesarGet(PeticionHTTP pet) throws IOException {
        Path ruta = null;
        if( pet.url.equals("/") ){            
            ruta = Paths.get(FILES_PATH+INDEX_PATH);
            log = new LogPeticionUsuario(cliente.getInetAddress().getHostName(), pet.url);
        }else if(currentConfig.getAlias().equals("")){
            
                ruta = Paths.get(FILES_PATH+pet.url);
            
            
        }else if( Files.isDirectory(Paths.get(FILES_PATH+pet.url)) ){
            currentAlias = pet.url;
            log = new LogPeticionUsuario(cliente.getInetAddress().getHostName(), currentAlias);
            if(currentConfig.getIndex().equals("") && currentConfig.getDirStruct()==true){
                ruta = arbolDirectorios(currentConfig.getAlias());
            }else{
                ruta = Paths.get(FILES_PATH+currentConfig.getAlias()+"\\"+currentConfig.getIndex());
            }                
        }
        else{
            
            ruta = Paths.get(FILES_PATH+currentConfig.getAlias()+"\\"+pet.url);
        }
        byte[] datosFichero = leerFichero(ruta);
        if(datosFichero.length > 0){
            RespuestaHTTP resp = new RespuestaHTTP(200, ruta.toString(), datosFichero, currentConfig);
            os.write(resp.getData());
        }else
            os.write(new RespuestaHTTP(404, currentConfig).getData());

    }

    public byte[] leerFichero(Path ruta) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if( Files.isRegularFile(ruta, LinkOption.NOFOLLOW_LINKS) )
            baos.write(Files.readAllBytes(ruta), 0, (int)Files.size(ruta));
        else
            os.write(new RespuestaHTTP(404).getData());
        return baos.toByteArray();
    }

    @Override
    public void procesarDelete(PeticionHTTP pet)throws IOException{
        if(borrarFichero(pet))
            os.write(new RespuestaHTTP(202).getData());
        else
            os.write(new RespuestaHTTP(404).getData());
    }

    public boolean borrarFichero(PeticionHTTP pet) throws IOException{
        Path ruta = Paths.get(FILES_PATH+pet.url);
        Path rutaPapelera = Paths.get(RECYCLE_BIN_PATH+pet.url);
        if( ! Files.isDirectory(Paths.get(RECYCLE_BIN_PATH), LinkOption.NOFOLLOW_LINKS))
            Files.createDirectory(Paths.get(RECYCLE_BIN_PATH));
        if( Files.isRegularFile(ruta, LinkOption.NOFOLLOW_LINKS) &&
                ! Files.isRegularFile(rutaPapelera, LinkOption.NOFOLLOW_LINKS)){
            Files.move(ruta, rutaPapelera);
            return true;
        }else{
            System.out.println(ruta.toString()+" no es un fichero o no se pudo borrar");
            return false;
        }
    }
    @Override
    public void procesarPut(PeticionHTTP pet) throws IOException{
        Path rutaPUT = Paths.get(FILES_PATH+pet.url);
        if(Files.notExists(rutaPUT, LinkOption.NOFOLLOW_LINKS)){
            if(crearFicheroPUT(pet))
                os.write(new RespuestaHTTP(201).getData());
            else {
                String err = "File cant be created at origin";
                os.write(new RespuestaHTTP(400, null, err.getBytes("UTF-8")).getData());
            }
        }else{
            if(crearFicheroPUT(pet))
                os.write(new RespuestaHTTP(200).getData());
            else {
                
                os.write(new RespuestaHTTP(501).getData());
            }
        }
    }
    private boolean crearFicheroPUT(PeticionHTTP pet){
        Path rutaPUT = Paths.get(FILES_PATH+pet.url);
        try{
            Files.deleteIfExists(rutaPUT);
            Files.createFile(rutaPUT);
            Files.write(rutaPUT, pet.body, CREATE);
            return true;
        }catch(IOException e){return false;}
    }

    @Override
    public void procesarPost(PeticionHTTP pet) throws IOException{
        String cuerpo = new String(pet.body, "UTF-8");
        ArrayList<String> nombresVariables = new ArrayList<>();
        ArrayList<String> variables = new ArrayList<>();
        String[] form = cuerpo.split("&");
        for(int i=0;i<form.length-1;i++){
            nombresVariables.add(form[i].split("=")[0]);
            variables.add(form[i].split("=")[1]);
        }
        escribirFormFichero(nombresVariables, variables);
        os.write(new RespuestaHTTP(301, pet).getData());
    }
    
    public void escribirFormFichero(ArrayList<String> nombresVariables, ArrayList<String> variables) throws IOException{
        Path rutaForm = Paths.get(BD_PATH+"form.dat");
        Path rutaBd = Paths.get(BD_PATH);
        if( ! Files.isDirectory(rutaBd, LinkOption.NOFOLLOW_LINKS))
                Files.createDirectory(rutaBd);
        if( ! Files.isRegularFile(rutaForm, LinkOption.NOFOLLOW_LINKS))
            Files.createFile(rutaForm);
        for(int i=0; i<nombresVariables.size(); i++){
            String linea = nombresVariables.get(i)+":"+variables.get(i)+"\n";
            Files.write(rutaForm, linea.getBytes("UTF-8"), StandardOpenOption.APPEND);
        }
    }
    
    
   private WebAppConfig selectCurrentConf(PeticionHTTP pet){
       if(pet.url.equals("/")){
           return new WebAppConfig();
       }
       String candidate = pet.url.split("/")[1];
       if(Files.isDirectory(Paths.get(FILES_PATH+candidate))){
           for(WebAppConfig wA : appConfs){
               if(wA.getAlias().equals(candidate)){                   
                   return wA;
               }
           }
       }
       
       return new WebAppConfig();
   }
    private Path arbolDirectorios(String alias){
            String fileRoute = FILES_PATH+alias+"\\"+"\\."+"index"+alias+".html";
            StringBuilder sb = new StringBuilder();
            if(Files.isRegularFile(Paths.get(fileRoute), LinkOption.NOFOLLOW_LINKS)){
                Path filePath = Paths.get(fileRoute);
                return filePath;
            }
        try{
            Stream<Path> paths = Files.walk(Paths.get(FILES_PATH+alias), 1);
            Path flPth = Files.createFile(Paths.get(fileRoute));
            File file = flPth.toFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            for(Object o : paths.toArray()){
                String rawResource = o.toString();
                String trimmedRes = rawResource.replace(".\\ficheros\\"+alias, "");
                String trimmedResv2 = rawResource.replace(".\\ficheros\\"+alias+"\\", "");
                sb.append("<p>").append("<a href=").append(trimmedRes).append(">").append(trimmedResv2)
                        .append("</a>").append("</p>").append(System.getProperty("line.separator"));                
            }
            bw.write(sb.toString());
            bw.close();
            sb.setLength(0);
            return flPth;
        }catch(IOException ex){System.out.println(ex.getMessage());}
        
        return null;
    }
}

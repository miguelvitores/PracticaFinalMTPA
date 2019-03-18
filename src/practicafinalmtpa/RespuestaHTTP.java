package practicafinalmtpa;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;


public class RespuestaHTTP 
        implements Constants{
    
    public int codigo;
    public Date fecha;
    public String tipoContenido;
    public int longContenido;
    public byte[] cuerpo;
    public PeticionHTTP pet;
    public WebAppConfig currentWCF;
    
    public RespuestaHTTP(int codigo, String ruta, byte[] cuerpo) {
        this.codigo = codigo;
        this.cuerpo = cuerpo;
        try{
            this.tipoContenido = getTipoContenido(getExtension(ruta));
        }catch (Exception ex){System.out.println(ex.toString());}
        fecha = new Date(System.currentTimeMillis());
        this.longContenido = cuerpo.length;
    }
    public RespuestaHTTP(int codigo, String ruta, byte[] cuerpo, WebAppConfig wCnf) {
        this.codigo = codigo;
        this.cuerpo = cuerpo;
        try{
            this.tipoContenido = getTipoContenido(getExtension(ruta));
        }catch (Exception ex){System.out.println(ex.toString());}
        fecha = new Date(System.currentTimeMillis());
        this.longContenido = cuerpo.length;
        this.currentWCF = wCnf;
    }
    public RespuestaHTTP(int codigo, WebAppConfig wConf){
        this.currentWCF = wConf;
        this.codigo = codigo;
        this.fecha = new Date(System.currentTimeMillis());
    }
    
    public RespuestaHTTP(int codigo, PeticionHTTP pet) {
        this.codigo = codigo;
        this.pet = pet;
        fecha = new Date(System.currentTimeMillis());
    }
    
    public RespuestaHTTP(int codigo){
        this.codigo = codigo;
        fecha = new Date(System.currentTimeMillis());
    }

   
    private String getExtension(String ruta){
        return ruta.substring(ruta.lastIndexOf(".")).replace(".", "");
    }
    

    private String getTipoContenido(String ext) throws FileNotFoundException, IOException{
        File mimedb = new File(MIME_DB);
        BufferedReader fr = new BufferedReader(new FileReader(mimedb));
        String line;
        
        while((line=fr.readLine())!=null){
            
            
            String exts = line.substring(line.indexOf("=")).replace("=", "");
            if(exts.contains("&")){
                for(String ex : exts.split("&")){
                    if(ext.equals(ex))
                        return line.split("=")[0];
                }                    
            }else{
                if(ext.equals(exts))
                    return line.split("=")[0];
            }
        }
        
        return "not-supported";
    }
    public byte[] encodeGZIP(byte[] pet){
        ByteArrayOutputStream petArr = new ByteArrayOutputStream();        
        try{
            
        GZIPOutputStream gzip = new GZIPOutputStream(petArr);
        gzip.write(pet);
        gzip.flush();
        gzip.close();
        }catch(IOException ex){System.out.println(ex.getMessage());}
        return petArr.toByteArray();
    }
    public byte[] getData() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(this.toString().getBytes());
        if(cuerpo!=null)
            baos.write(cuerpo);
        if(this.currentWCF!=null && this.currentWCF.getGzip()){
            return encodeGZIP(baos.toByteArray());
        }
        return baos.toByteArray();
    }

    @Override
    public String toString() {
        String ok=null;
        
        if(codigo == 200)
            ok="OK";
        else if(codigo == 404)
            ok = "Not Found";
        else if(codigo == 202){
            ok = "Accepted";
        }else if(codigo==201)
            ok = "Created";
        else if(codigo==400)
            ok = "Bad Request";
        else if(codigo==501)
            ok = "Not Implemented";
        else if(codigo==301)
            ok = "Moved Permanently";
            
        
        String total = "HTTP/1.1 "+codigo+" "+ok;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        total = total.concat("\nDate: "+format.format(fecha));
        total = total.concat("\nContent-Type: "+tipoContenido);
        total = total.concat("\nContent-Lenght: "+longContenido);
        if(this.currentWCF!=null){
            if(this.currentWCF.getGzip())
                total = total.concat("\nContent-Encoding: gzip");
            if(this.codigo/100 >= 4 && !currentWCF.getAlias().isEmpty() && !currentWCF.getErrPage().isEmpty())
                total = total.concat("\nLocation: "+"http://localhost/"+this.currentWCF.getAlias()+currentWCF.getErrPage());
        }
        if(this.pet!=null && this.pet.verbo.equals("POST"))
            total = total.concat("\nLocation: "+"http://localhost");
        total = total.concat("\n\n");
        return total;
    }
    
    
}

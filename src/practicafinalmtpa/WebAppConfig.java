package practicafinalmtpa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;




public class WebAppConfig 
        implements Constants{
    
    private String alias;
    private String index;
    private String errPage;
    private boolean gzip;
    private boolean dirStruct;

    public WebAppConfig() {
        this.index = "";
        this.errPage = "";
        this.alias="";
        this.gzip = false;
        this.dirStruct = false;
    }
    public WebAppConfig(String index, String errPage, boolean gzip, boolean dirStruct, String alias) {
        this.index = index;
        this.errPage = errPage;
        this.gzip = gzip;
        this.dirStruct = dirStruct;
        this.alias=alias;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getErrPage() {
        return errPage;
    }

    public void setErrPage(String errPage) {
        this.errPage = errPage;
    }

    public boolean getGzip() {
        return gzip;
    }

    public void setGzip(boolean gzip) {
        this.gzip = gzip;
    }

    public boolean getDirStruct() {
        return dirStruct;
    }

    public void setDirStruct(boolean dirStruct) {
        this.dirStruct = dirStruct;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public void editConfig(){
        Path ruta = Paths.get(FILES_PATH+alias+"\\"+alias+".conf");
        try{
            Files.deleteIfExists(ruta);
            Files.createFile(ruta);
            String configuracion = "index:"+index+"\n"+"ErrPage:"+errPage+"\n"+
                  "DirTree:"+dirStruct+"\n"+"GZipResponse:"+gzip;  
            Files.write(ruta, configuracion.getBytes("UTF-8"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public static WebAppConfig leerConf(String alias){
        String errPage = "";
        String index = "";
        boolean gzip = false;
        boolean treeDir = false;
        String line;
        String[] opt= new String[2];
        try{
            
        File f = new File(FILES_PATH+alias+"\\"+alias+".conf");
        BufferedReader br = new BufferedReader(new FileReader(f));
        
        
        while((line = br.readLine())!=null){
            opt[0] = line.split(":")[0];
            opt[1] = line.split(":")[1];
            if(opt[0].compareTo("index")==0){
                if(Files.isRegularFile(Paths.get(FILES_PATH+alias+"\\"+opt[1]), LinkOption.NOFOLLOW_LINKS)){
                    index = opt[1];                    
                }else{
                    treeDir = true;
                }
            }else if(opt[0].compareTo("DirTree")==0){
                if(opt[1].compareTo("true")==0)
                    treeDir=true;
                else
                    treeDir=false;
                
            }else if(opt[0].compareTo("GzipResponse")==0){
                if(opt[1].compareTo("true")==0){
                    gzip=true;
                }                  
                
            }else if(opt[0].compareTo("ErrPage")==0){
                if(Files.isRegularFile(Paths.get(FILES_PATH+alias+"\\"+opt[1]), LinkOption.NOFOLLOW_LINKS)){
                    errPage = opt[1];                    
                }
            }
        }
            
        }catch(IOException ex){System.out.println(ex.getMessage());}
            
        return new WebAppConfig(index, errPage, gzip, treeDir, alias); 
    }
    
}

package practicafinalmtpa;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogPeticionUsuario 
        implements Constants{
    private Date fecha;
    private String ipAddress;
    private String alias;

    public LogPeticionUsuario(String ipAddress, String alias) {
        fecha = new Date(System.currentTimeMillis());
        this.ipAddress = ipAddress;
        if(alias.equals("/"))
            this.alias = "PaginaPrincipal";
        else
            this.alias = alias;
        generarLog();
    }
    
    private void generarLog() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss aa");
        String log = sdf.format(fecha)+" "+alias+" solicitado por ip "+ipAddress+"\r\n";
        try {
            if(! Files.isDirectory(Paths.get(LOG_PATH)))
                Files.createDirectory(Paths.get(LOG_PATH));
            if(! Files.isRegularFile(Paths.get(PET_USERS)))
                Files.createFile(Paths.get(PET_USERS));
            Files.write(Paths.get(PET_USERS), log.getBytes("UTF-8"), StandardOpenOption.APPEND);
        } catch (Exception ex) {ex.printStackTrace();
        }
    }
    
}

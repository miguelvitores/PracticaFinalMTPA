package practicafinalmtpa;

import java.io.ByteArrayOutputStream;

public class PeticionHTTP {
    public String verbo;
    public String url;
    public String headers;
    public String cabecera;
    public int  contentLength;
    public byte[] body;
    
    
    
    public PeticionHTTP(ByteArrayOutputStream mensaje) {
        requestBreakDown(mensaje);
        this.contentLength = body.length;
        String linea1 = cabecera.split("\n")[0];            
        this.headers = cabecera.replace(linea1, "").replaceFirst("\n", "");            
        this.verbo = linea1.split(" ")[0];
        this.url = linea1.split(" ")[1];
        
    }



    
    private boolean requestBreakDown(ByteArrayOutputStream mensaje){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        byte[] msg = mensaje.toByteArray();
        int index=0;
        byte[] separadorCuerpo = {13, 10, 13, 10};
        for(int i=0; i<msg.length;i++){
            byte unByte = msg[i];
            if(unByte==separadorCuerpo[index])
                index++;
            else if(index!=0)
                index=0;
            if(index==4){
                try {
                    baos.write(msg, 0, i-3);
                    this.cabecera = new String(baos.toByteArray(), "UTF-8");
                    baos2.write(msg, i+1, msg.length-i-1);
                    this.body = baos2.toByteArray();
                    return true;
                } catch (Exception ex) {System.out.println(ex.getMessage());}
            }
        }
        return false;
    }

}

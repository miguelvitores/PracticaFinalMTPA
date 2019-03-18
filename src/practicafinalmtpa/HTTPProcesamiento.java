
package practicafinalmtpa;

import java.io.ByteArrayOutputStream;



public interface HTTPProcesamiento {
    public ByteArrayOutputStream leerPeticion()throws Exception;
    public void procesarPeticion(PeticionHTTP pet)throws Exception;
    public void procesarGet(PeticionHTTP pet)throws Exception;
    public void procesarDelete(PeticionHTTP pet)throws Exception;
    public void procesarPut(PeticionHTTP pet)throws Exception;
    public void procesarPost(PeticionHTTP pet)throws Exception;
}

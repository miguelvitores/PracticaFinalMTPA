package interfaz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import javax.swing.BoxLayout;
import javax.swing.JTextPane;
import practicafinalmtpa.*;

public class PanelDirectorios 
        extends JTextPane 
        implements Constants{
    private String directorios="";

    public PanelDirectorios() {
        try {
            super.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            getDirectories();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
    private void getDirectories() throws IOException{
        String files = FILES_PATH;
        Stream<Path> stream = Files.walk(Paths.get(files), 1);
        for(Object o : stream.toArray()){
            Path path = (Path)o;
            if( ! path.equals(Paths.get(files)) && Files.isDirectory(path)){
                String alias = path.toString().replace(files, "");
                if(directorios.isEmpty())
                    directorios = alias+"\n";
                else
                    directorios = directorios.concat(alias+"\n");
            }
        }
        super.setText(directorios);
    }
    
    public void addDirectory(String alias){
        directorios = directorios.concat(alias+"\n");
        super.setText(directorios);
        super.revalidate();
        super.repaint();
    }
    
    public void removeDirectory(String alias){
        directorios = directorios.replace(alias+"\n", "");
        super.setText(directorios);
        super.revalidate();
        super.repaint();
    }
    
    public void renameDirectory(String aliasAntiguo, String aliasNuevo){
        directorios = directorios.replace(aliasAntiguo, aliasNuevo);
        super.setText(directorios);
        super.revalidate();
        super.repaint();
    }
    
    
    
    
}

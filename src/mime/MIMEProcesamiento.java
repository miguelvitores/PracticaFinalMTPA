package mime;

public interface MIMEProcesamiento {
    
    public static String[] IMAGES = {"png", "jpeg", "jpg:jpeg", "tiff", "tif:tiff", 
        "gif", "ico:x-icon", "svg:svg+xml", "webp"};
    public static String[] TEXTS = {"css", "csv", "html", "htm:html", "ics:calendar"};

    public boolean isImagen(String ext);
    public boolean isTexto(String ext);
    
}

package id.co.dapenbi.core.util;

import javax.servlet.ServletContext;

import org.springframework.http.MediaType;

public class MediaTypeUtil {
	 
    public static MediaType getMediaTypeForFileName(ServletContext servletContext, String filename) {

        String mineType = servletContext.getMimeType(filename);
        try {
            MediaType mediaType = MediaType.parseMediaType(mineType);
            return mediaType;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
     
}
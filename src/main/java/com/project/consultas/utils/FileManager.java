package com.project.consultas.utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

import com.project.consultas.dto.ImageFileDTO;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileManager {


    public static String readMessageFromTemplate(String path) throws IOException {
        String result = null;
        try (DataInputStream reader = new DataInputStream(new FileInputStream(path))) {
            int nBytesToRead = reader.available();
            if(nBytesToRead > 0) {
                byte[] bytes = new byte[nBytesToRead];
                reader.read(bytes);
                result = new String(bytes);
            }
        }
        return result;
    }
    
	public void saveImage(ImageFileDTO imageFile, String pathStr) throws IOException {
		if(!Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(), ContentType.IMAGE_PNG.getMimeType()).contains(imageFile.getImageType())) {
			throw new IllegalStateException("not supported type");
		}
        byte[] bytes = Base64.getDecoder().decode(imageFile.getBase64Image());
        Path path = Paths.get(pathStr);
        File f = new File(pathStr);
        f.getParentFile().mkdirs();
        Files.write(path, bytes);
	}
	
	public byte[] readImage(String path) throws IOException {
        byte[] bytes = null;
        try (DataInputStream reader = new DataInputStream(new FileInputStream(path))) {
            int nBytesToRead = reader.available();
            if(nBytesToRead > 0) {
                bytes = new byte[nBytesToRead];
                reader.read(bytes);
            }
        }
        return bytes;
	}
	
	public void deleteImage(String path) throws IOException {
		Path dir = Paths.get(path);
		Files.delete(dir);
	}
}

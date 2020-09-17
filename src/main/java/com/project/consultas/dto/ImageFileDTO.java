package com.project.consultas.dto;


public class ImageFileDTO {

    private String base64Image;
    private String imageType;
    private String fileName;

    public String getBase64Image() {
        return base64Image;
    }

    public ImageFileDTO() {
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

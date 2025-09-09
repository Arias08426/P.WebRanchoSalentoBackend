package com.example.PaginaWebRanchoSalento.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.example.PaginaWebRanchoSalento.exception.OurException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    @Value("${firebase.bucket.name}")
    private String bucketName;

    @Value("${firebase.credentials.path}")
    private String credentialsPath;

    private Storage storage;

    // Inicialización lazy del cliente Firebase
    private Storage getStorageInstance() {
        if (storage == null) {
            try {
                GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new FileInputStream(credentialsPath)
                );

                storage = StorageOptions.newBuilder()
                        .setCredentials(credentials)
                        .build()
                        .getService();

            } catch (IOException e) {
                throw new OurException("Error al inicializar Firebase Storage: " + e.getMessage());
            }
        }
        return storage;
    }

    public String saveImageToFirebase(MultipartFile photo) {
        try {
            // Generar nombre único para evitar conflictos
            String originalFilename = photo.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // Crear path en Firebase Storage
            String filePath = "images/" + uniqueFilename;

            // Crear BlobId y BlobInfo
            BlobId blobId = BlobId.of(bucketName, filePath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(photo.getContentType())
                    .build();

            // Subir archivo
            Storage storageInstance = getStorageInstance();
            Blob blob = storageInstance.create(blobInfo, photo.getBytes());

            // Retornar URL pública
            return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucketName, filePath.replace("/", "%2F"));

        } catch (Exception e) {
            e.printStackTrace();
            throw new OurException("No se pudo subir la imagen a Firebase Storage: " + e.getMessage());
        }
    }

    // Método adicional para eliminar archivos
    public boolean deleteImageFromFirebase(String fileName) {
        try {
            String filePath = "images/" + fileName;
            BlobId blobId = BlobId.of(bucketName, filePath);

            Storage storageInstance = getStorageInstance();
            return storageInstance.delete(blobId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new OurException("No se pudo eliminar la imagen de Firebase Storage: " + e.getMessage());
        }
    }

    // Método adicional para obtener URL de un archivo existente
    public String getImageUrl(String fileName) {
        String filePath = "images/" + fileName;
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName, filePath.replace("/", "%2F"));
    }
}
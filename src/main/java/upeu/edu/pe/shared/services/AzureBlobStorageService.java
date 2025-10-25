package upeu.edu.pe.shared.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.util.UUID;

@ApplicationScoped
public class AzureBlobStorageService {

    @ConfigProperty(name = "azure.storage.connection-string")
    String connectionString;

    @ConfigProperty(name = "azure.storage.container-name", defaultValue = "images")
    String containerName;

    /**
     * Sube un archivo a Azure Blob Storage y retorna la URL pública
     * 
     * @param inputStream Stream del archivo a subir
     * @param fileName Nombre original del archivo
     * @param contentType Tipo MIME del archivo (image/jpeg, image/png, etc.)
     * @return URL pública del archivo subido
     */
    public String uploadFile(InputStream inputStream, String fileName, String contentType) {
        try {
            // Crear cliente de blob service
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();

            // Obtener o crear el contenedor
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            if (!containerClient.exists()) {
                containerClient.create();
                // Hacer el contenedor público para que las URLs sean accesibles
                containerClient.setAccessPolicy(
                    com.azure.storage.blob.models.PublicAccessType.BLOB, 
                    null
                );
            }

            // Generar nombre único para el archivo
            String uniqueFileName = generateUniqueFileName(fileName);

            // Crear cliente del blob
            BlobClient blobClient = containerClient.getBlobClient(uniqueFileName);

            // Subir el archivo
            blobClient.upload(inputStream, inputStream.available(), true);

            // Establecer content type
            if (contentType != null && !contentType.isEmpty()) {
                blobClient.setHttpHeaders(
                    new com.azure.storage.blob.models.BlobHttpHeaders()
                        .setContentType(contentType)
                );
            }

            // Retornar la URL pública
            return blobClient.getBlobUrl();

        } catch (Exception e) {
            throw new RuntimeException("Error al subir archivo a Azure Blob Storage: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un archivo de Azure Blob Storage dado su URL
     * 
     * @param blobUrl URL del blob a eliminar
     */
    public void deleteFile(String blobUrl) {
        try {
            // Extraer el nombre del blob de la URL
            String blobName = extractBlobNameFromUrl(blobUrl);

            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            if (blobClient.exists()) {
                blobClient.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar archivo de Azure Blob Storage: " + e.getMessage(), e);
        }
    }

    /**
     * Genera un nombre único para el archivo manteniendo la extensión original
     */
    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        int lastDot = originalFileName.lastIndexOf('.');
        if (lastDot > 0) {
            extension = originalFileName.substring(lastDot);
        }
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * Extrae el nombre del blob de una URL completa
     */
    private String extractBlobNameFromUrl(String blobUrl) {
        // URL ejemplo: https://account.blob.core.windows.net/container/blobname.jpg
        String[] parts = blobUrl.split("/");
        return parts[parts.length - 1];
    }

    /**
     * Valida si un tipo de contenido es una imagen permitida
     */
    public boolean isValidImageType(String contentType) {
        if (contentType == null) return false;
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/webp");
    }
}

package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ImageMetaData;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageService {
    private final String imagesPath = "images";

    public String saveImageToStorage(MultipartFile imageFile, ImageMetaData imageMetaData) throws IOException {
        String uniqueFileName = UUID.randomUUID().toString();
        Path uploadPath = Path.of(imagesPath, imageMetaData.getContentType(), imageMetaData.getContentId().toString());
        Path filePath = uploadPath.resolve(uniqueFileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    public Optional<byte[]> getImage(String imageName, ImageMetaData imageMetaData) throws IOException {
        Path imagePath = Path.of(imagesPath, imageMetaData.getContentType(), imageMetaData.getContentId().toString(), imageName);

        if (Files.exists(imagePath)) {
            return Optional.of(Files.readAllBytes(imagePath));
        } else {
            return Optional.empty();
        }
    }
    public String deleteImage(String imageName, ImageMetaData imageMetaData) throws IOException {
        Path imagePath = Path.of(imagesPath, imageMetaData.getContentType(), imageMetaData.getContentId().toString(), imageName);
        System.out.println("Image path: " + imagePath);
        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
            return "Success";
        } else {
            return "Failed";
        }
    }

    public void moveFile(String imageName, ImageMetaData oldImageMetaData, ImageMetaData newImageMetaData) throws IOException {
        Path sourcePath = Path.of(imagesPath, oldImageMetaData.getContentType(), oldImageMetaData.getContentId().toString(), imageName);
        Path destinationPath = Path.of(imagesPath, newImageMetaData.getContentType(), newImageMetaData.getContentId().toString(), imageName);
        Files.createDirectories(destinationPath.getParent());
        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        this.deleteImage(imageName, oldImageMetaData);
    }
}



package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    private final String uploadDir = "uploads/profile-images/";

    public String storeProfileImage(MultipartFile file, Long userId) {
        try {
            Files.createDirectories(Paths.get(uploadDir));

            String filename =
                    "user_" + userId + "_" + System.currentTimeMillis()
                            + "_" + file.getOriginalFilename();

            Path path = Paths.get(uploadDir, filename);
            Files.write(path, file.getBytes());

            return "/uploads/profile-images/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store profile image", e);
        }
    }
}

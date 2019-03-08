package com.codimen.lendit.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

@Slf4j
@Component
public class FileUploadUtil {

    private static final char EXTENSION_SEPARATOR = '.';
    private static final char DIRECTORY_SEPARATOR = '/';
    private static final char HYPHEN = '-';

    @Value("${app.lendit.profile-pic.extension.name}")
    private String supportedProfilePicExtensions;

    @Value("${app.lendit.profile-pic.upload-path}")
    private String profilePicUploadPath;

    @Getter
    @Value("${app.lendit.profile-pic.multipart.maxFileSize}")
    private Long profilePicMaxFileSize;

    @Value("${app.lendit.profile-pic.file.name.prefix}")
    private String profilePicFileNamePrefix;

    @Value("${api.lendit.profile-pic.image.web.url}")
    private String profilePicWebUrl;

    public String getExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int index = indexOfExtension(filename);

        if (index == -1) {
            return null;
        } else {
            return filename.substring(index);
        }
    }

    public boolean matchProfilePicExtension(String extensionName) {
        if (extensionName == null) {
            return false;
        }
        return supportedProfilePicExtensions.contains(extensionName);
    }

    public int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        // Check that no directory separator appears after the
        // EXTENSION_SEPARATOR
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);

        int lastDirSeparator = filename.lastIndexOf(DIRECTORY_SEPARATOR);

        if (lastDirSeparator > extensionPos) {
            log.error("A directory separator appears after the file extension, " +
                    "assuming there is no file extension");
            return -1;
        }

        return extensionPos;
    }
    public void deleteFile(String absoluteFilePath) {
        File file = new File(absoluteFilePath);
        if(file.exists()){
            file.delete();
            log.info("Deleted the file " + absoluteFilePath);
        }else {
            log.info("File Does not exist " + absoluteFilePath);
        }
    }

    public void deleteOldAllFiles(Path dir, String filename) {
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                // used to delete files under directory
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    String name = new File(file.toString()).getName();
                    if (name.startsWith(filename)) {
                        Files.delete(file);
                        log.info("Deleted the file " + name);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (NoSuchFileException e) {
            //ignore
        } catch (IOException e) {
            log.error("Failed to delete old files!", e);
        }
    }

    public String getUserProfilePicFileName(String receivedFilename, String firstName, Long userId) {
        String ext = this.getExtension(receivedFilename);
        String userProfilePicFileName = profilePicFileNamePrefix+HYPHEN+
                UUID.randomUUID().toString()+ext;
        return userProfilePicFileName;
    }

    public String getUserOldProfilePicFileName(String url) {
        if(url != null){
            String [] arr = url.split("/");
            return arr[arr.length-1];
        } else {
          return "";
        }
    }

    public boolean doesProfileFileSizeLessThenMaxSize(MultipartFile multipartFile) {
        return multipartFile.getSize() <= profilePicMaxFileSize;
    }

    public String getProfilePicUploadPath(String finalName){
        return profilePicUploadPath+finalName;
    }
    public void createProfilePicUploadDir(){
        File file = new File(profilePicUploadPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void saveUserProfilePic(MultipartFile multipartFile, String profilePicUploadPath) throws IOException {
        File f = new File(profilePicUploadPath);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(profilePicUploadPath)));
        stream.write(multipartFile.getBytes());
        log.info("User Profile Pic Upload Completed");
        if (stream != null) {
            stream.close();
        }
    }

    public String getProfilePicUrl(String finalUserProfilePicFileName) {
        return profilePicWebUrl+finalUserProfilePicFileName;
    }
}

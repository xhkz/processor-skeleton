package au.net.metropolis.models;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CompressFile {
    private Logger logger = Logger.getLogger(getClass().getName());
    private String path;
    private String name;
    private String ext;

    public CompressFile(String path) {
        this.path = path;
        this.name = FilenameUtils.getBaseName(path);
        this.ext = FilenameUtils.getExtension(path);
    }

    public Map<String, String> getFileMap() {
        Map<String, String> fileMap = new HashMap<>();

        File f = new File(this.path);

        if (f.exists() && !f.isDirectory()) {
            switch (this.ext.toLowerCase()) {
                case "zip":
                    try {
                        ZipFile zipFile = new ZipFile(this.path);
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();

                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            String name = entry.getName();
                            String content = IOUtils.toString(zipFile.getInputStream(entry));
                            fileMap.put(name, content);
                        }
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, e.getMessage());
                    }
                    break;
                case "tar":
                    try {
                        TarArchiveInputStream tis = new TarArchiveInputStream(new FileInputStream(this.path));
                        TarArchiveEntry entry;
                        while (null != (entry = tis.getNextTarEntry())) {
                            String name = entry.getName();
                            String content = FileUtils.readFileToString(entry.getFile());
                            fileMap.put(name, content);
                        }
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, e.getMessage());
                    }
                case "xml":
                    try {
                        fileMap.put(FilenameUtils.getName(this.path), FileUtils.readFileToString(new File(this.path)));
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, e.getMessage());
                    }
                    break;
                default:
                    logger.log(Level.SEVERE, "Invalid file extension");
            }
        } else {
            logger.log(Level.SEVERE, "Invalid file： " + this.path);
        }

        return fileMap;
    }

    public String getPath() {
        return path;
    }
}

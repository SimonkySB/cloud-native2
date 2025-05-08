package com.cloudnative;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WalletLoader {
    private static final String WALLET_DIR = System.getenv("WALLET_DIR");


    public static void downloadAndUnzipWallet() throws IOException {
        String url = WALLET_DIR;

        // Descargar wallet.zip
        Path zipPath = Paths.get("/tmp/wallet.zip");
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, zipPath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        
        // Descomprimir en /tmp/wallet
        Path outputDir = Paths.get("/tmp");
        unzip(zipPath.toString(), outputDir.toString());

        // Configurar propiedad oracle
        Path walletDir = Paths.get("/tmp/wallet");

        System.setProperty("oracle.net.tns_admin", walletDir.toString());
    }


    private static void unzip(String zipFilePath, String destDir) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdirs();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}

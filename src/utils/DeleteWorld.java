package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * https://github.com/msalihov/MapReset/blob/master/src/main/java/li/maxsa/java/mapreset/FileUtil.java
 */
public class DeleteWorld {

	
	private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        directory.delete();
    }

    public static void deleteWorld(String worldname) {
        File dir = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath());
        String[] folders = dir.list();
        for (String folder : folders) {
            if (folder.contains(worldname)) {
                //Bukkit.getServer().unloadWorld(folder, true);
                File folderfile = new File(folder);
                deleteDirectory(folderfile);
            }
        }
    }

    public static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }
        } else {
            OutputStream out;
            try (InputStream in = new FileInputStream(src)) {
                out = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
            out.close();
        }
    }
	
	
}

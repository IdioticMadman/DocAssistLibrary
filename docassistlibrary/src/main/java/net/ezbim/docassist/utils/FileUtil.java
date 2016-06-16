package net.ezbim.docassist.utils;

import android.content.Context;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    public static boolean copyAsset(Context ctx, String assetName, String destinationPath) throws IOException {
        InputStream in = ctx.getAssets().open(assetName);
        File f = new File(destinationPath);
        f.createNewFile();
        OutputStream out = new FileOutputStream(new File(destinationPath));

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();

        return true;
    }


    public static String extractFileNameFromURL(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public static String getFileIncode(File file) {
        if (!file.exists()) {
            System.err.println("getFileIncode: file not exists!");
            return null;
        }
        byte[] buf = new byte[4096];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            // (1)
            UniversalDetector detector = new UniversalDetector(null);
            // (2)
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();
            // (4)
            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                System.out.println("Detected encoding = " + encoding);
            } else {
                System.out.println("No encoding detected.");
            }
            // (5)
            detector.reset();
            fis.close();
            return encoding;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
package net.ezbim.docassist.txt;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author robert
 * @version 1.0
 * @time 2016/6/16.
 * @description
 */
public class TxTUtil {
    public static String getText(Context context, String txtPath, String fileIncode) {
        try {
            InputStream is = new FileInputStream(txtPath);
            InputStreamReader isr = new InputStreamReader(is, fileIncode);
            BufferedReader br = new BufferedReader(isr, 16 * 1024);//128K缓冲读取
            StringBuilder sb = new StringBuilder();
            String line;
            int index = 0;
            while (index < 50 && (line = br.readLine()) != null) {
                sb.append(line).append("\n");
                index++;
            }
            br.close();
            isr.close();
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package net.ezbim.sample;

import android.os.Environment;

import java.io.File;

/**
 * @author robert
 * @version 1.0
 * @time 2016/6/16.
 * @description
 */
public class Common {
    public static String pdfPathFolder = Environment.getExternalStorageDirectory() + File.separator + "sample" + File.separator + "pdf";
    public static String picPathFolder = Environment.getExternalStorageDirectory() + File.separator + "sample" + File.separator + "pic";
    public static String txtPathFolder = Environment.getExternalStorageDirectory() + File.separator + "sample" + File.separator + "txt";
    public static String officePathFolder = Environment.getExternalStorageDirectory() + File.separator + "sample" + File.separator + "office";
}

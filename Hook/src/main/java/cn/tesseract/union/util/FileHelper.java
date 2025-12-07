package cn.tesseract.union.util;

import com.corrodinggames.rts.union.gameFramework.e.class_899;
import net.rwhps.server.util.file.FileUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHelper {
    public static InputStream getInputStream(String path) {
        try {
            return new FileInputStream(FileUtils.getFile("data/" + path).getFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static OutputStream getOutputStream(String path) {
        try {
            return new FileOutputStream(FileUtils.getFile("data/" + path).getFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean exists(String path) {
        return FileUtils.getFile("data/" + path).exists();
    }

    public static boolean dirExists(String path) {
        return FileUtils.getFolder("data/" + path).exists();
    }

    public static boolean mkdir(String path) {
        FileUtils.getFolder("data/" + path).mkdir();
        return true;
    }

    public static boolean renameFile(String from, String to, String dir) {
        return false;
    }

    public static String read(InputStream is) throws IOException {
        if (is == null) {
            return "";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                if (sb.length() == 0) {
                    sb.append("\n");
                }
                sb.append(line);
            } else {
                return sb.toString();
            }
        }
    }

    public static void write(OutputStream os, byte[] content) throws IOException {
        os.write(content);
        os.close();
    }

    public static void write(OutputStream os, String content) throws IOException {
        write(os, content.getBytes());
    }

    public static String[] listFiles(String path) {
        var f = FileUtils.getFolder("data/" + path).getFileList();
        var s = new String[f.size()];
        for (int i = 0; i < f.size(); i++) s[i] = f.get(i).getName();
        return s;
    }

    public static void log(String log) {
        PrintWriter writer = new PrintWriter(class_899.method_2164(new File(class_899.method_2178("/SD/rustedWarfare/union.log")), true));
        writer.write("\r\n" + log + "\n (at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "\n");
        writer.close();
    }
}
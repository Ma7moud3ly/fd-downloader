package com.ma7moud3ly.fd.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.ma7moud3ly.fd.App;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class MyFiles {


    public static boolean delete(String name) {
        File file = new File(name);
        if (!file.exists()) return false;
        try {
            file.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void rename(String name, String new_name) {
        File file = new File(name);
        File new_file = new File(new_name);
        if (!file.exists()) return;
        try {
            file.renameTo(new_file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String read(String name) {
        File file = new File(name);
        if (!file.exists())
            return "not found";
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            byte[] byt = new byte[dis.available()];
            dis.readFully(byt);
            dis.close();
            String content = new String(byt, 0, byt.length);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getStackTrace().toString();
        }
    }


    public static boolean write(String name, String data) {
        File file = new File(name);
        try {
            if (!file.exists())
                file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.append(data);
            writer.flush();
            writer.close();
            out.close();
            return true;
        } catch (Exception e) {
            file.delete();
            e.printStackTrace();
            return false;
        }
    }

    public static void saveImage(Bitmap bitmap, String path) {
        File file = new File(path);
        File dir = file.getParentFile();
        if (!dir.exists())
            dir.mkdirs();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readJsonString(int resource, Context context) {
        InputStream inputStream = context.getResources().openRawResource(resource);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int pointer;
            while ((pointer = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, pointer);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        String json = writer.toString();
        return json;
    }
}

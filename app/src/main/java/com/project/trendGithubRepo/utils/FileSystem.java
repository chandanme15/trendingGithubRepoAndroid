package com.project.trendGithubRepo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.project.trendGithubRepo.Application;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileSystem {

    public static boolean WriteToFile(Context context, String strFileName, String strJSONData) {
        boolean bWriteSuccess = true;
        try {
            FileOutputStream fileout= context.openFileOutput(strFileName, context.MODE_PRIVATE);
            fileout.write(strJSONData.getBytes());
            FileDescriptor fileDescriptor = fileout.getFD();
            fileDescriptor.sync();
            fileout.flush();
            fileout.close();
        } catch (Exception e) {
            bWriteSuccess = false;
        }
        return bWriteSuccess;
    }

    public static String ReadFromFile(Context context,String strFileName) {
        String strRet = "";
        if(!FileSystem.IsFileExist(context,strFileName))
        {
            return strRet;
        }

        try {
            FileInputStream fileIn = context.openFileInput(strFileName);
            InputStreamReader InputRead= new InputStreamReader(fileIn);
            BufferedReader bufferedReader = new BufferedReader(InputRead);

            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }
            strRet = stringBuilder.toString();
            bufferedReader.close();
            InputRead.close();
            fileIn.close();
        } catch (Exception e) {
        }
        return strRet;
    }

    public static <T> List<T> ReadFile(Context context, final Class<T[]> clazz, String strFileName) {

        String jsonRead = ReadFromFile(context, strFileName);
        List<T> listRet = null;

        try {
            if (jsonRead != "") {
                final T[] jsonToObject = JSON.parseObject(jsonRead, clazz);
                listRet = new ArrayList<>(Arrays.asList(jsonToObject));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listRet;
    }

    public static <T> boolean ReWriteFile(Context context, String strFileName, List<T> list) {
        boolean bRetval = false;
        try {
            String json = JSON.toJSONString(list, true);
            bRetval = WriteToFile(context, strFileName, json);
        } catch (Exception e) {
        }
        return bRetval;
    }

    public static boolean DeleteFile(Context context, String strFileName) {
        boolean bRetval = false;
        try {
            if (FileSystem.IsFileExist(context, strFileName)) {
                bRetval = context.deleteFile(strFileName);
            }
        } catch (Exception e) {
        }
        return bRetval;
    }

    public static boolean IsFileExist(Context context, final String strFileName)
    {
        boolean bFileExists = false;
        try
        {
            File file = new File(context.getFilesDir() + "/" + strFileName);
            if(file.exists()) {
                bFileExists = true;
            }
        }
        catch (Exception ex)
        {
        }
        return bFileExists;
    }
}

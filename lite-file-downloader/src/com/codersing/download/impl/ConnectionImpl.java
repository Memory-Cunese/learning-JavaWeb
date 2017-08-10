package com.codersing.download.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import com.codersing.download.api.Connection;
import com.codersing.download.api.ConnectionException;

class ConnectionImpl implements Connection{

    URL url;
    static final int BUFFER_SIZE = 1024;
    
    ConnectionImpl(String _url) throws ConnectionException  {
        try {
            url = new URL(_url);
        } catch (MalformedURLException e) {
            throw new ConnectionException(e);
        }
    }
    @Override
    public byte[] read(int startPos, int endPos) throws IOException {
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
        InputStream is = httpConn.getInputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int totalSize = endPos - startPos +1;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while(baos.size() < totalSize) {
            int len = is.read(buffer);
            if(len < 0) {
                break;
            }
            
            baos.write(buffer, 0, len);
        }
        if(baos.size() > totalSize) {
            byte[] data = baos.toByteArray();
            return Arrays.copyOf(data, totalSize);
        }
        return baos.toByteArray();
    }

    @Override
    public int getContentLength() {
        URLConnection con;
        try {
            con = url.openConnection();
            return con.getContentLength();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void close() {
        
    }

}

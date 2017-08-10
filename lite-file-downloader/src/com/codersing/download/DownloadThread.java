package com.codersing.download;

import java.io.RandomAccessFile;
import java.util.concurrent.CyclicBarrier;

import com.codersing.download.api.Connection;

public class DownloadThread extends Thread{

    Connection conn;
    int startPos;
    int endPos;
    CyclicBarrier barrier;
    String localFile;
    public DownloadThread(Connection conn, int startPos, int endPos, String localFile, CyclicBarrier barrier) {
        super();
        this.conn = conn;
        this.startPos = startPos;
        this.endPos = endPos;
        this.barrier = barrier;
        this.localFile = localFile;
    }
    
    @Override
    public void run() {
        try{
            System.out.println("Begin to read [" + startPos + "-" + endPos + "]");
            byte[] data = conn.read(startPos, endPos);
            
            RandomAccessFile file = new RandomAccessFile(localFile, "rw");
            file.seek(startPos);
            file.write(data);
            file.close();
            conn.close();
            barrier.await();  //等待别的线程完成
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

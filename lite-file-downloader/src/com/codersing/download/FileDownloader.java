package com.codersing.download;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.CyclicBarrier;

import com.codersing.download.api.Connection;
import com.codersing.download.api.ConnectionManager;
import com.codersing.download.api.DownloaderListener;

public class FileDownloader {

    private String url;
    private String localfile;
    
    DownloaderListener listener;
    
    ConnectionManager cm;
    
    private static final int DOWMLOAD_THREAD_NUM = 3;
    
    public FileDownloader(String _url, String localfile) {
        this.url = _url;
        this.localfile = localfile;
    }
    
    public void execute(){
         // 在这里实现你的代码， 注意： 需要用多线程实现下载
        // 这个类依赖于其他几个接口, 你需要写这几个接口的实现代码
        // (1) ConnectionManager , 可以打开一个连接，通过Connection可以读取其中的一段（用startPos, endPos来指定）
        // (2) DownloadListener, 由于是多线程下载， 调用这个类的客户端不知道什么时候结束，所以你需要实现当所有
        //     线程都执行完以后， 调用listener的notifiedFinished方法， 这样客户端就能收到通知。
        // 具体的实现思路：
        // 1. 需要调用ConnectionManager的open方法打开连接， 然后通过Connection.getContentLength方法获得文件的长度
        // 2. 至少启动3个线程下载，  注意每个线程需要先调用ConnectionManager的open方法
        // 然后调用read方法， read方法中有读取文件的开始位置和结束位置的参数， 返回值是byte[]数组
        // 3. 把byte数组写入到文件中
        // 4. 所有的线程都下载完成以后， 需要调用listener的notifiedFinished方法
        
        // 下面的代码是示例代码， 也就是说只有一个线程， 你需要改造成多线程的。
        //栅栏类，可以理解为障碍，当线程都完成的时候，继续下面的任务
        CyclicBarrier barrier = new CyclicBarrier(DOWMLOAD_THREAD_NUM, new Runnable() {
            
            @Override
            public void run() {
                listener.notifyFinished();          
            }
        });
        
        Connection conn = null;
        try{
            conn = cm.open(this.url);
            int length = conn.getContentLength();
            //获取网络文件后，现在硬盘上占位置
            createPlaceHolderFile(this.localfile, length);
            //给每个线程分配应该下载的文件长度
            int[][] range = allocateDownloadRange(DOWMLOAD_THREAD_NUM, length);
            
            for(int i = 0; i<DOWMLOAD_THREAD_NUM; i++) {
                DownloadThread thread = new DownloadThread(
                        cm.open(url), 
                        range[i][0], 
                        range[i][1], 
                        localfile, 
                        barrier);
                thread.start();
            }
        } catch (Exception e) {
            if(conn != null) {
                conn.close();
            }
        }
    }

    private int[][] allocateDownloadRange(int dowmloadThreadNum, int length) {
        int[][] ranges = new int[dowmloadThreadNum][2];
        int eachThreadSize = length / dowmloadThreadNum;  //每个线程需要下载的文件大小
        int left = length % dowmloadThreadNum;            //剩下的归最后一个线程处理
        
        for(int i = 0; i < dowmloadThreadNum; i++) {
            int startPos = i * eachThreadSize;
            int endPos = (i + 1) * eachThreadSize -1;
            if(i == (dowmloadThreadNum -1)) {
                endPos += left;
            }
            
            ranges[i][0] = startPos;
            ranges[i][1] = endPos;
        }
        return ranges;
    }

    private void createPlaceHolderFile(String fileName, int contentLength) throws IOException {
        
        RandomAccessFile file = new RandomAccessFile(fileName, "rw");
        
        for(int i= 0; i < contentLength; i++) {
            file.write(0);
        }
        file.close();
    }
    
    public void setListener(DownloaderListener listener) {
        this.listener = listener;
    }

    
    
    public void setConnectionManager(ConnectionManager ucm){
        this.cm = ucm;
    }
    
    public DownloaderListener getListener(){
        return this.listener;
    }
}

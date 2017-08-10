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
         // ������ʵ����Ĵ��룬 ע�⣺ ��Ҫ�ö��߳�ʵ������
        // ��������������������ӿ�, ����Ҫд�⼸���ӿڵ�ʵ�ִ���
        // (1) ConnectionManager , ���Դ�һ�����ӣ�ͨ��Connection���Զ�ȡ���е�һ�Σ���startPos, endPos��ָ����
        // (2) DownloadListener, �����Ƕ��߳����أ� ���������Ŀͻ��˲�֪��ʲôʱ���������������Ҫʵ�ֵ�����
        //     �̶߳�ִ�����Ժ� ����listener��notifiedFinished������ �����ͻ��˾����յ�֪ͨ��
        // �����ʵ��˼·��
        // 1. ��Ҫ����ConnectionManager��open���������ӣ� Ȼ��ͨ��Connection.getContentLength��������ļ��ĳ���
        // 2. ��������3���߳����أ�  ע��ÿ���߳���Ҫ�ȵ���ConnectionManager��open����
        // Ȼ�����read������ read�������ж�ȡ�ļ��Ŀ�ʼλ�úͽ���λ�õĲ����� ����ֵ��byte[]����
        // 3. ��byte����д�뵽�ļ���
        // 4. ���е��̶߳���������Ժ� ��Ҫ����listener��notifiedFinished����
        
        // ����Ĵ�����ʾ�����룬 Ҳ����˵ֻ��һ���̣߳� ����Ҫ����ɶ��̵߳ġ�
        //դ���࣬�������Ϊ�ϰ������̶߳���ɵ�ʱ�򣬼������������
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
            //��ȡ�����ļ�������Ӳ����ռλ��
            createPlaceHolderFile(this.localfile, length);
            //��ÿ���̷߳���Ӧ�����ص��ļ�����
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
        int eachThreadSize = length / dowmloadThreadNum;  //ÿ���߳���Ҫ���ص��ļ���С
        int left = length % dowmloadThreadNum;            //ʣ�µĹ����һ���̴߳���
        
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

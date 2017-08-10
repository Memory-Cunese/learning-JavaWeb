package com.codersing.download.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.codersing.download.FileDownloader;
import com.codersing.download.api.ConnectionManager;
import com.codersing.download.api.DownloaderListener;
import com.codersing.download.impl.ConnectionManagerImpl;

public class FileDownloaderTest {

    boolean downloaderFinished = false;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDownload() {
        
        String url = "http://images2015.cnblogs.com/blog/610238/201604/610238-20160421154632101-286208268.png";
        FileDownloader downloader = new FileDownloader(url, "f:\\test.jpg");
        
        ConnectionManager cm = new ConnectionManagerImpl();
        downloader.setConnectionManager(cm);
        
        downloader.setListener(new DownloaderListener() {
            
            @Override
            public void notifyFinished() {
                
                downloaderFinished = true;
            }
        });
        
        downloader.execute();
        
        //�ȴ����߳����س���ִ�����
        while (!downloaderFinished) {
            
            try{
                System.out.println("��û��������ɣ���������");
                //����5��
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("�������");
    }

}

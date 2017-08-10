package com.codersing.download.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.codersing.download.api.Connection;
import com.codersing.download.api.ConnectionException;
import com.codersing.download.api.ConnectionManager;
import com.codersing.download.impl.ConnectionManagerImpl;

public class ConnectionTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testContentLength() {
        
        ConnectionManager connectionManager = new ConnectionManagerImpl();
        Connection connection = null;
        try {
            connection = connectionManager.open("http://www.hinews.cn/pic/0/13/91/26/13912621_821796.jpg");
            int len = connection.getContentLength();
            System.out.println(len);
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

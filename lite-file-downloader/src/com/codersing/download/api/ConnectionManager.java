package com.codersing.download.api;


public interface ConnectionManager {

    /**
     * ����һ��url����һ������
     * 
     */
    
    public Connection open(String url) throws ConnectionException;
}

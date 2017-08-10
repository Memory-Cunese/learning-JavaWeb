package com.codersing.download.api;


public interface ConnectionManager {

    /**
     * 给定一个url，打开一个连接
     * 
     */
    
    public Connection open(String url) throws ConnectionException;
}

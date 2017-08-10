package com.codersing.download.impl;

import com.codersing.download.api.Connection;
import com.codersing.download.api.ConnectionException;
import com.codersing.download.api.ConnectionManager;

public class ConnectionManagerImpl implements ConnectionManager{

    @Override
    public Connection open(String url) throws ConnectionException {
        // TODO Auto-generated method stub
        return new ConnectionImpl(url);
    }

}

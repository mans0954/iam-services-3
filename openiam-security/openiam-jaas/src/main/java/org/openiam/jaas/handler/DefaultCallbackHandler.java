package org.openiam.jaas.handler;


public class DefaultCallbackHandler extends AbstractCalbackHandler{
    private String userName;
    private String password;
    private String token;

    @Override
    protected String getUserName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected char[] getPassword() {
        return new char[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected char[] getToken() {
        return new char[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}

package com.filecoinj.config;

public class Args {
    private Args(){}
    private static final Args INSTANCE = new Args();

    /**
     * 调用url
     */
    private String url;
    /**
     * 区块链节点调用权限 token
     */
    private String nodeAuthorization;

    public String getNodeAuthorization() {
        return nodeAuthorization;
    }

    public void setNodeAuthorization(String nodeAuthorization) {
        this.nodeAuthorization = nodeAuthorization;
    }

    public static Args getINSTANCE() {
        return INSTANCE;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

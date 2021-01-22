package com.filecoinj.config;

public class Args {
    private Args(){}
    private static final Args INSTANCE = new Args();

    /**
     * 节点类型（infura 或者 private）
     */
    private String nodeType;

    /**
     * 调用url
     */
    private String url;
    /**
     * 区块链节点调用权限 token
     */
    private String nodeAuthorization;

    /**
     * rpc用户名(infura节点使用)
     */
    private String rpcUserName;

    /**
     * rpc SECRET(infura节点使用)
     */
    private String rpcSecret;

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

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getRpcUserName() {
        return rpcUserName;
    }

    public void setRpcUserName(String rpcUserName) {
        this.rpcUserName = rpcUserName;
    }

    public String getRpcSecret() {
        return rpcSecret;
    }

    public void setRpcSecret(String rpcSecret) {
        this.rpcSecret = rpcSecret;
    }
}

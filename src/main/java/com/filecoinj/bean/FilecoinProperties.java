package com.filecoinj.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * filecoin节点配置
 */
@Data
@ConfigurationProperties(prefix = "filecoin")
public class FilecoinProperties {

    /**
     * 节点类型（infura 或者 private）
     */
    private String nodeType;
    /**
     * Filecoin node rpc url
     */
    private String rpcUrl;

    /**
     * node rpc token
     */
    private String rpcToken;

    /**
     * rpc用户名
     */
    private String rpcUsername;

    /**
     * rpc SECRET
     */
    private String rpcSecret;

}

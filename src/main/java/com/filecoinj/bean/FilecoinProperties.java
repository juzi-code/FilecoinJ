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
     * Filecoin node rpc url
     */
    private String rpcUrl;

    /**
     * node rpc token
     */
    private String rpcToken;

}

package com.filecoinj.model.result;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WalletResult implements Serializable {
    private String address;
    //短私钥，程序发起转账使用
    private String privatekey;
    //Lotus内生成的格式 长私钥 可导入imtoken
    private String lotusPrivateKey;
}

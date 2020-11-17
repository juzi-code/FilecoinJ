package com.filecoinj.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction implements Serializable {
    /**
     * 收款地址
     */
    private String to;
    /**
     * 转账方地址
     */
    private String from;
    /**
     * nonce值
     */
    private Long nonce;
    /**
     * 转账金额
     */
    private String value;
    /**
     * 该笔交易能消耗的最大Gas量
     */
    private Long gasLimit;
    /**
     * 用户选择支付的总手续费率
     */
    private String gasFeeCap;
    /**
     * 用户选择支付给矿工的手续费率
     */
    private String gasPremium;
    private Long method;
    private String params;
}

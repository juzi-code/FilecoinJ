package com.filecoinj.model.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagesResult {

    private  String cid;

    private String from;

    private String to;

    private BigDecimal value;

    private Integer nonce;

    private Integer gasLimit;

    private BigInteger gasFeeCap;

    private BigInteger gasPremium;

    private Integer method;

    private String params;

    private Integer version;
}

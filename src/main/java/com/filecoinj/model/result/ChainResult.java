package com.filecoinj.model.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

/**
 * 一个链信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChainResult {

    private BigInteger height;

    private List<String> blockCidList;

    private List<String> parentBlockCidList;
}

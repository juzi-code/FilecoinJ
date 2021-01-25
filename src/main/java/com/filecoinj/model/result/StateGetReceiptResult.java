package com.filecoinj.model.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateGetReceiptResult {
    private Integer exitCode;

    private String messageReturn;

    private BigInteger gasUsed;
}

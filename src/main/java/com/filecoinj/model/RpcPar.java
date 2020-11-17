package com.filecoinj.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RpcPar implements Serializable {
    private String jsonrpc;
    private Integer id;
    private String method;
    private List<Object> params;
}

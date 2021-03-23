package com.filecoinj.model.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChainMessagesResult {
    private List<String> blockCidList;

    private List<String> parentBlockCidList;

    private List<MessagesResult> messageList;
}

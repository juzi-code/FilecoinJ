package com.filecoinj.constant;

import java.math.BigDecimal;

public class FilecoinCnt {

    /**
     * 节点类型-私有节点
     */
    public static final String PRIVATE_NODE = "private";

    /**
     * infura节点
     */
    public static final String INFURA_NODE = "infura";

    /**
     *
     */
    public static final int MajUnsignedInt  = 0;
    /**
     *
     */
    public static final int MajNegativeInt  = 1;
    /**
     *
     */
    public static final int MajByteString  = 2;
    /**
     *
     */
    public static final int MajTextString  = 3;
    /**
     *
     */
    public static final int MajArray  = 4;
    /**
     *
     */
    public static final int MajMap  = 5;
    /**
     *
     */
    public static final int MajTag  = 6;
    /**
     *
     */
    public static final int MajOther  = 7;

    /**
     * 获取gas
     */
    public static final String GET_GAS = "Filecoin.GasEstimateMessageGas";
    /**
     * 获取 nonce值
     */
    public static final String GET_NONCE = "Filecoin.MpoolGetNonce";
    /**
     * 获取 余额
     */
    public static final String GET_BALANCE = "Filecoin.WalletBalance";
    /**
     * 广播交易
     */
    public static final String BOARD_TRANSACTION = "Filecoin.MpoolPush";

    /**
     * 获取一个新的钱包地址
     */
    public static final String NEW_WALLET_ADDRESS = "Filecoin.WalletNew";

    /**
     * 获取钱包默认地址
     */
    public static final String GET_WALLET_DEFAULT_ADDRESS = "Filecoin.WalletDefaultAddress";

    /**
     * 校验钱包地址
     */
    public static final String WALLET_VALIDATE_ADDRESS = "Filecoin.WalletValidateAddress";


    /**
     * 获取最新区块信息
     */
    public static final String CHAIN_HEAD = "Filecoin.ChainHead";

    /**
     * 根据区块链高度获取区块链信息
     */
    public static final String CHAIN_GET_TIP_SET_BY_HEIGHT = "Filecoin.ChainGetTipSetByHeight";


    /**
     * 根据区块cid获取区块内的所有消息
     */
    public static final String CHAIN_GET_BLOCK_MESSAGES = "Filecoin.ChainGetBlockMessages";


    /**
     * 根据消息cid获取消息详情
     */
    public static final String CHAIN_GET_MESSAGE = "Filecoin.ChainGetMessage";

    /**
     * 获取交易收据
     */
    public static final String STATE_GET_RECEIPT = "Filecoin.StateGetReceipt";

    /**
     * 根据CID获取区块的父链头集合中存储的消息
     */
    public static final String CHAIN_GET_PARENT_MESSAGES = "Filecoin.ChainGetParentMessages";

    /**
     * 根据区块cid获取区块的父链头集合中所有消息的收据
     */
    public static final String CHAIN_GET_PARENT_RECEIPTS = "Filecoin.ChainGetParentReceipts";

    /**
     * 查询消息的收据和tipset
     */
    public static final String STATE_SEARCH_MSG = "Filecoin.StateSearchMsg";




    /**
     * 默认超时时间
     */
    public static final int DEFAULT_TIMEOUT = 30000;


    /**
     * fil单位
     */
    public static enum Unit {
        DEFAULT("default", 0),
        ETHER("fil", 18),
        ;

        private String name;
        private BigDecimal unitFactor;

        private Unit(String name, int factor) {
            this.name = name;
            this.unitFactor = BigDecimal.TEN.pow(factor);
        }

        public BigDecimal getWeiFactor() {
            return this.unitFactor;
        }

        public String toString() {
            return this.name;
        }

        public static Unit fromString(String name) {
            if (name != null) {
                for (Unit value : values()) {
                    if (value.name.equalsIgnoreCase(name)){
                        return value;
                    }
                }
            }
            return valueOf(name);
        }
    }
}

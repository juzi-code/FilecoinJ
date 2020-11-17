package com.filecoinj.constant;

public class FilecoinCnt {
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
     * 默认超时时间
     */
    public static final int DEFAULT_TIMEOUT = 10000;

}

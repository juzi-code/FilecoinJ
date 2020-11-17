package com.filecoinj;

import cn.hutool.core.util.HexUtil;
import com.filecoinj.handler.TransactionHandler;
import com.filecoinj.model.EasySend;
import com.filecoinj.model.GetGas;
import com.filecoinj.model.Transaction;
import com.filecoinj.model.result.BalanceResult;
import com.filecoinj.model.result.GasResult;
import com.filecoinj.model.result.SendResult;
import com.filecoinj.model.result.WalletResult;

import java.math.BigInteger;

public class Test {
    private static Filecoin filecoin = new Filecoin("http://192.168.2.21:1234/rpc/v0",
            "Bearer eyJhbGciOiJIUzI1aiIsInR5cCI6IkpXVCJ1.eyJBbGxvdyI6WyJyZWFkIiwid3JpdGUiLCJzaWduIiwiYWRtaW4iXX0.ZHABMIbiRejFWLSr8gH0NTnloxhSKU-ffwenHXevNH8");

    public static void main(String[] agrs) throws Exception {
        easySend();
    }

    public static void createWalletTest() throws Exception {
        WalletResult wallet = filecoin.createWallet();
        System.out.println(wallet);
    }

    public static void importWalletTest() throws Exception {
        WalletResult result = filecoin.importWallet("私钥");
        System.out.println(result);
    }

    public static void balanceOfTest() throws Exception {
        BalanceResult result = filecoin.balanceOf("地址");
        System.out.println(result);
    }

    public static void getGas() throws Exception {
        GasResult result = filecoin.getGas(GetGas.builder().from("地址")
                .to("地址")
                .value(BigInteger.valueOf(1000000000000L)).build());
        System.out.println(result);
    }

    public static void getNonce() throws Exception {
        int nonce = filecoin.getNonce("地址");
        System.out.println(nonce);
    }
    //0.0000001
    public static void easySend() throws Exception {
        SendResult sendResult = filecoin.easySend(EasySend.builder().from("地址")
                .to("地址")
                .value(BigInteger.valueOf(100000000000L))
                .privatekey("私钥").build());
        System.out.println(sendResult);
    }

    public static void transactionSerializeTest() throws Exception{
        Transaction build = Transaction.builder()
                .from("地址")
                .to("地址")
                .nonce(22L)
                .method(0L)
                .params("")
                .gasPremium("100053")
                .gasLimit(1078170L)
                .gasFeeCap("7580780488")
                .value("100000000000").build();
        TransactionHandler transactionHandler = new TransactionHandler();
        byte[] cidHash = transactionHandler.transactionSerialize(build);
        System.out.println("cidHash: "+HexUtil.encodeHexStr(cidHash));
    }

}

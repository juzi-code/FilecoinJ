package com.filecoinj;

import cn.hutool.core.util.HexUtil;
import com.filecoinj.handler.TransactionHandler;
import com.filecoinj.model.EasySend;
import com.filecoinj.model.GetGas;
import com.filecoinj.model.Transaction;
import com.filecoinj.model.result.*;

import java.math.BigInteger;

public class Test {
    private static Filecoin filecoin = new Filecoin("http://172.16.13.81:1234/rpc/v0",
            "Bearer 123");

    public static void main(String[] agrs) throws Exception {

    }


    public static void getMessageByCid(String cid) throws Exception {
        MessagesResult message = filecoin.getMessageByCid(cid);
        System.out.println(message);
    }


    public static void validateAddress(String address) throws Exception {
        boolean rs = filecoin.validateAddress(address);
        System.out.println(rs);
    }

    public static void getWalletDefaultAddress() throws Exception {
        String address = filecoin.getWalletDefaultAddress();
        System.out.println(address);
    }

    public static void createWalletTest() throws Exception {
        WalletResult wallet = filecoin.createWallet();
        System.out.println(wallet);
    }

    public static void createWalletRpcTest() throws Exception {
        String address = filecoin.createWalletRpc();
        System.out.println(address);
    }

    public static void importWalletTest() throws Exception {
        WalletResult result = filecoin.importWallet("私钥");
        System.out.println(result);
    }

    public static void balanceOfTest() throws Exception {
        BalanceResult result = filecoin.balanceOf("f17o75zwv6hrngzawwxecpcbpcvhacksqxpda5gpa");
        System.out.println(result);
    }

    public static void getGas() throws Exception {
        GasResult result = filecoin.getGas(GetGas.builder().from("f17o75zwv6hrngzawwxecpcbpcvhacksqxpda5gpa")
                .to("f17o75zwv6hrngzawwxecpcbpcvhacksqxpda5gpa")
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

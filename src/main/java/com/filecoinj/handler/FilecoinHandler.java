package com.filecoinj.handler;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.filecoinj.config.Args;
import com.filecoinj.constant.FilecoinCnt;
import com.filecoinj.crypto.ECKey;
import com.filecoinj.exception.BalanceOfException;
import com.filecoinj.exception.ExecuteException;
import com.filecoinj.exception.ParameException;
import com.filecoinj.exception.SendException;
import com.filecoinj.exception.WalletException;
import com.filecoinj.model.EasySend;
import com.filecoinj.model.GetGas;
import com.filecoinj.model.RpcPar;
import com.filecoinj.model.Transaction;
import com.filecoinj.model.result.BalanceResult;
import com.filecoinj.model.result.GasResult;
import com.filecoinj.model.result.SendResult;
import com.filecoinj.model.result.WalletResult;
import ove.crypto.digest.Blake2b;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class FilecoinHandler {
    private TransactionHandler transactionHandler;

    public FilecoinHandler() {
        transactionHandler = new TransactionHandler();
    }

    public WalletResult createWallet() throws WalletException {
        ECKey ecKey = new ECKey();
        byte[] privKeyBytes = ecKey.getPrivKeyBytes();
        byte[] pubKey = ecKey.getPubKey();
        if (privKeyBytes == null || privKeyBytes.length < 1) {
            throw new WalletException("create wallet error");
        }
        String filAddress = byteToAddress(pubKey);
        String privatekey = HexUtil.encodeHexStr(privKeyBytes);
        return WalletResult.builder().address(filAddress).privatekey(privatekey).build();
    }

    public WalletResult importWallet(String privatekey) throws WalletException {
        if (StrUtil.isBlank(privatekey)) {
            throw new WalletException("parameter cannot be empty");
        }
        ECKey ecKey = ECKey.fromPrivate(HexUtil.decodeHex(privatekey));
        byte[] pubKey = ecKey.getPubKey();

        String filAddress = byteToAddress(pubKey);

        return WalletResult.builder().address(filAddress).privatekey(privatekey).build();
    }

    public WalletResult importWallet(byte[] privatekey) throws WalletException {
        if (ArrayUtil.isEmpty(privatekey)) {
            throw new WalletException("parameter cannot be empty");
        }
        ECKey ecKey = ECKey.fromPrivate(privatekey);
        byte[] pubKey = ecKey.getPubKey();

        String filAddress = byteToAddress(pubKey);

        return WalletResult.builder().address(filAddress).privatekey(HexUtil.encodeHexStr(privatekey)).build();
    }

    private String byteToAddress(byte[] pub) {
        Blake2b.Digest digest = Blake2b.Digest.newInstance(20);
        String hash = HexUtil.encodeHexStr(digest.digest(pub));

        //4.计算校验和
        String pubKeyHash = "01" + HexUtil.encodeHexStr(digest.digest(pub));

        Blake2b.Digest blake2b3 = Blake2b.Digest.newInstance(4);
        String checksum = HexUtil.encodeHexStr(blake2b3.digest(HexUtil.decodeHex(pubKeyHash)));
        //5.生成地址

        return "f1" + Base32.encode(HexUtil.decodeHex(hash + checksum)).toLowerCase();
    }

    public BalanceResult balanceOf(String address,int timeout) throws BalanceOfException, ExecuteException {
        if (StrUtil.isBlank(address)) {
            throw new BalanceOfException("parameter cannot be empty");
        }
        List<Object> params = new ArrayList<>();
        params.add(address);
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.GET_BALANCE)
                .params(params).build();
        String execute = execute(par,timeout);
        JSONObject jsonObject = new JSONObject(execute);
        String balance = jsonObject.getStr("result");
        return BalanceResult.builder().balanceOf(new BigInteger(balance)).build();
    }

    private String execute(RpcPar par,int timeout) throws ExecuteException {
        HttpRequest post = HttpUtil.createPost(Args.getINSTANCE().getUrl());
        post.header("Authorization", Args.getINSTANCE().getNodeAuthorization());
        post.body(com.alibaba.fastjson.JSONObject.toJSONString(par));
        //设置超时时间
        post.timeout(timeout);
        HttpResponse execute = post.execute();
        if (execute.getStatus() != 200) {
            throw new ExecuteException("execute error " + execute);
        }
        return execute.body();
    }

    public SendResult send(Transaction transaction, String privatekey,int timeout) throws SendException, ExecuteException {
        if (transaction == null || StrUtil.isBlank(transaction.getFrom())
                || StrUtil.isBlank(transaction.getTo())
                || StrUtil.isBlank(transaction.getGasFeeCap())
                || StrUtil.isBlank(transaction.getGasPremium())
                || StrUtil.isBlank(transaction.getValue())
                || transaction.getGasLimit() == null
                || transaction.getMethod() == null
                || transaction.getNonce() == null
                || StrUtil.isBlank(privatekey)) {
            throw new SendException("parameter cnanot be empty");
        }
        BigInteger account = new BigInteger(transaction.getValue());
        if (account.compareTo(BigInteger.ZERO) < 1) {
            throw new SendException("the transfer amount must be greater than 0");
        }
        byte[] cidHash = null;
        try {
            cidHash = transactionHandler.transactionSerialize(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SendException("transaction entity serialization failed");
        }
        //签名
        ECKey ecKey = ECKey.fromPrivate(HexUtil.decodeHex(privatekey));
        String sing = Base64.encode(ecKey.sign(cidHash).toByteArray());

        List<Object> params = new ArrayList<>();
        JSONObject signatureJson = new JSONObject();
        JSONObject messageJson = new JSONObject(transaction);
        JSONObject json = new JSONObject();
        messageJson.putOpt("version", 0);
        signatureJson.putOpt("data", sing);
        signatureJson.putOpt("type", 1);
        json.putOpt("message", messageJson);
        json.putOpt("signature", signatureJson);

        params.add(json);
        RpcPar rpcPar = RpcPar.builder().id(1).jsonrpc("2.0").method(FilecoinCnt.BOARD_TRANSACTION)
                .params(params).build();

        String execute = execute(rpcPar,timeout);
        SendResult build = null;
        try {
            JSONObject executeJson = new JSONObject(execute);
            String result = executeJson.getJSONObject("result").getStr("/");
            build = SendResult.builder().cid(result).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SendException("send error " + execute);
        }
        return build;
    }

    public SendResult easySend(EasySend send,int timeout) throws ParameException, ExecuteException, SendException {
        if (send == null || StrUtil.isBlank(send.getFrom())
                || StrUtil.isBlank(send.getTo())
                || StrUtil.isBlank(send.getPrivatekey())
                || send.getValue() == null) {
            throw new ParameException("parameter cannot be empty");
        }
        //获取gas
        GasResult gas = getGas(GetGas.builder().from(send.getFrom())
                .to(send.getTo())
                .value(send.getValue()).build(),timeout);
        //获取nonce
        int nonce = getNonce(send.getFrom(),timeout);
        //拼装交易参数
        Transaction transaction = Transaction.builder().from(send.getFrom())
                .to(send.getTo())
                .gasFeeCap(gas.getGasFeeCap())
                .gasLimit(gas.getGasLimit().longValue() * 2)
                .gasPremium(gas.getGasPremium())
                .method(0L)
                .nonce((long) nonce)
                .params("")
                .value(send.getValue().toString()).build();

        return send(transaction, send.getPrivatekey(),timeout);
    }


    public int getNonce(String address,int timeout) throws ParameException, ExecuteException {
        if (StrUtil.isBlank(address)) {
            throw new ParameException("parameter cannot be empty");
        }
        List<Object> params = new ArrayList<>();
        params.add(address);
        RpcPar par = RpcPar.builder().id(1).jsonrpc("2.0").method(FilecoinCnt.GET_NONCE)
                .params(params).build();
        String execute = execute(par,timeout);
        Integer num = 0;
        try {
            JSONObject result = new JSONObject(execute);
            num = result.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecuteException("get nonce error " + execute);
        }
        return num;
    }

    public GasResult getGas(GetGas gas,int timeout) throws ParameException, ExecuteException {
        if (gas == null || StrUtil.isBlank(gas.getFrom())
                || StrUtil.isBlank(gas.getTo())
                || gas.getValue() == null) {
            throw new ParameException("paramter cannot be empty");
        }
        if (gas.getValue().compareTo(BigInteger.ZERO) < 1) {
            throw new ParameException("the transfer amount must be greater than 0");
        }
        List<Object> params = new ArrayList<>();
        JSONObject json = new JSONObject();
        json.putOpt("From", gas.getFrom());
        json.putOpt("To", gas.getTo());
        json.putOpt("Value", gas.getValue().toString());
        params.add(json);
        params.add(null);
        params.add(null);
        RpcPar par = RpcPar.builder().id(1).jsonrpc("2.0")
                .params(params)
                .method(FilecoinCnt.GET_GAS).build();
        String execute = execute(par,timeout);
        GasResult gasResult = null;
        try {
            JSONObject result = new JSONObject(execute);
            JSONObject jsonObject = result.getJSONObject("result");
            gasResult = GasResult.builder().gasFeeCap(jsonObject.getStr("GasFeeCap"))
                    .gasLimit(jsonObject.getBigInteger("GasLimit"))
                    .gasPremium(jsonObject.getStr("GasPremium")).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecuteException("get gas error " + execute);
        }
        return gasResult;
    }
}

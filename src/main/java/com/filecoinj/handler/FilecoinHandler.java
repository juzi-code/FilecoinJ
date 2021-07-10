package com.filecoinj.handler;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.filecoinj.config.Args;
import com.filecoinj.constant.FilecoinCnt;
import com.filecoinj.crypto.ECKey;
import com.filecoinj.exception.*;
import com.filecoinj.model.EasySend;
import com.filecoinj.model.GetGas;
import com.filecoinj.model.RpcPar;
import com.filecoinj.model.Transaction;
import com.filecoinj.model.result.*;
import com.filecoinj.utils.AddressUtil;
import com.filecoinj.utils.Convert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ove.crypto.digest.Blake2b;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
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
        String lotusPrivateKey = AddressUtil.convertToLotus(privatekey);
        return WalletResult.builder().address(filAddress).privatekey(privatekey).lotusPrivateKey(lotusPrivateKey).build();
    }

    /**
     * 从rpc节点获取一个新地址
     * @return
     * @throws SendException
     */
    public String createWalletRpc(int timeout) throws ExecuteException, WalletException {
        List<Object> params = new ArrayList<>();
        params.add("secp256k1"); //密钥类型 包括： bls(已弃用), secp256k1
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.NEW_WALLET_ADDRESS)
                .params(params).build();
        String execute = execute(par,timeout);
        JSONObject jsonObject = new JSONObject(execute);
        String address = jsonObject.getStr("result");
        if (StringUtils.isEmpty(address)){
            throw new WalletException("create wallet error");
        }
        return address;
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
        if (Args.getINSTANCE().getNodeType().equals(FilecoinCnt.PRIVATE_NODE)){
            post.header("Authorization", Args.getINSTANCE().getNodeAuthorization());
        }else if (Args.getINSTANCE().getNodeType().equals(FilecoinCnt.INFURA_NODE)){
            post.basicAuth(Args.getINSTANCE().getRpcUserName(),Args.getINSTANCE().getRpcSecret());
        }
        post.body(com.alibaba.fastjson.JSONObject.toJSONString(par));
        //设置超时时间
        post.timeout(timeout);
        HttpResponse execute = null;
        try {
            execute = post.execute();
        } catch (Exception e) {
            throw new ExecuteException("execute error :::" + e);
        }
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
        if (account.compareTo(BigInteger.ZERO) < 0) {
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
            build = SendResult.builder().cid(result)
                    .nonce(transaction.getNonce()).build();
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
        if (gas.getValue().compareTo(BigInteger.ZERO) < 0) {
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

    /**
     * 获取钱包默认地址
     * @return
     */
    public String getWalletDefaultAddress(int timeout) throws ExecuteException {
        List<Object> params = new ArrayList<>();
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.GET_WALLET_DEFAULT_ADDRESS)
                .params(params).build();
        String execute = execute(par,timeout);
        String address = null;
        try {
            JSONObject jsonObject = new JSONObject(execute);
            address = jsonObject.getStr("result");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecuteException("getWalletDefaultAddress error " + execute);
        }

        return address;
    }

    /**
     * 校验地址是否有效
     * @param address
     * @param timeout
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public boolean validateAddress(String address,int timeout) throws ExecuteException, ParameException {
        if (StringUtils.isEmpty(address)){
            throw new ParameException("paramter cannot be empty");
        }
        List<Object> params = new ArrayList<>();
        params.add(address);
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.WALLET_VALIDATE_ADDRESS)
                .params(params).build();
        String execute = execute(par,timeout);
        String result = null;
        try {
            JSONObject jsonObject = new JSONObject(execute);
            result = jsonObject.getStr("result");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecuteException("validateAddress error " + execute);
        }
        return !StringUtils.isEmpty(result);
    }

    /**
     * 根据区块cid获取区块内所有消息
     * @param blockCid
     * @param timeout
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public List<MessagesResult> getChainBlockMessages(String blockCid, int timeout) throws ExecuteException, ParameException {
        if (StringUtils.isEmpty(blockCid)){
            throw new ParameException("paramter cannot be empty");
        }
        List<Object> params = new ArrayList<>();
        HashMap<String, String> _cid = new HashMap<>();
        _cid.put("/", blockCid);
        params.add(_cid);
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.CHAIN_GET_BLOCK_MESSAGES)
                .params(params).build();
        String execute = execute(par,timeout);
        ArrayList<MessagesResult> messagesResults = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(execute);
            if (!StringUtils.isEmpty(jsonObject.get("error"))){
                throw new WalletException("getChainBlockMessages error: " + execute);
            }
            String resultObj = jsonObject.getStr("result");
            if (StringUtils.isEmpty(resultObj)){
                return messagesResults;
            }
            JSONObject result = new JSONObject(resultObj);
            JSONArray blsMessagesArr = result.getJSONArray("BlsMessages");
            JSONArray secpkMessagesArr = result.getJSONArray("SecpkMessages");
            if (blsMessagesArr != null){
                for (Object o : blsMessagesArr) {
                    messagesResults.add(messagesJsonToMessagesResult(new JSONObject(o)));
                }
            }
            if (secpkMessagesArr != null){
                for (Object o : secpkMessagesArr) {
                    JSONObject secpkMessagesObj = new JSONObject(o);
                    JSONObject message = secpkMessagesObj.getJSONObject("Message");
                    message.putOpt("CID", secpkMessagesObj.getObj("CID"));
                    messagesResults.add(messagesJsonToMessagesResult(message));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecuteException("getChainBlockMessages error " + execute);
        }
        return messagesResults;
    }

    /**
     * 根据消息cid获取消息详情
     * @param messCid 消息cid
     * @param timeout
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public MessagesResult getMessageByCid(String messCid, int timeout) throws ExecuteException, ParameException {
        if (StringUtils.isEmpty(messCid)){
            throw new ParameException("paramter cannot be empty");
        }
        List<Object> params = new ArrayList<>();
        HashMap<String, String> _cid = new HashMap<>();
        _cid.put("/", messCid);
        params.add(_cid);
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.CHAIN_GET_MESSAGE)
                .params(params).build();
        String execute = execute(par,timeout);
        MessagesResult messagesResult = null;
        try {
            JSONObject jsonObject = new JSONObject(execute);
            JSONObject result = jsonObject.getJSONObject("result");
            messagesResult = messagesJsonToMessagesResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecuteException("getMessageByCid error " + execute);
        }
        return messagesResult;
    }

    /**
     * 获取当前最新一个Tip
     * @param timeout
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public ChainResult getChainHead(int timeout) throws ExecuteException {
        List<Object> params = new ArrayList<>();
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.CHAIN_HEAD)
                .params(params).build();
        String execute = execute(par,timeout);
        ChainResult chainResult = null;
        try {
            JSONObject jsonObject = new JSONObject(execute);
            chainResult = chainJsonToChainResult(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecuteException("getChainHead error " + execute);
        }
        return chainResult;
    }

    /**
     * 根据链高度获取Tip
     * @param heigth
     * @param timeout
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public ChainResult getChainTipSetByHeight(BigInteger heigth, int timeout) throws ExecuteException, ParameException {
        if (heigth == null || heigth.compareTo(BigInteger.ZERO) < 0){
            throw new ParameException("paramter cannot be empty");
        }
        List<Object> params = new ArrayList<>();
        params.add(heigth);
        params.add(null);
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.CHAIN_GET_TIP_SET_BY_HEIGHT)
                .params(params).build();
        String execute = execute(par,timeout);
        ChainResult chainResult = null;
        try {
            JSONObject jsonObject = new JSONObject(execute);
            chainResult = chainJsonToChainResult(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecuteException("getChainTipSetByHeight error " + execute);
        }
        return chainResult;
    }

    /**
     * 获取消息收据
     * @param messageCid
     * @param timeout
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public StateGetReceiptResult stateGetReceipt(String messageCid, int timeout) throws ExecuteException, ParameException {
        if (StringUtils.isEmpty(messageCid)){
            throw new ParameException("paramter cannot be empty");
        }
        List<Object> params = new ArrayList<>();
        HashMap<String, String> cidParam = new HashMap<>();
        cidParam.put("/", messageCid);
        params.add(cidParam);
        params.add(null);
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.STATE_GET_RECEIPT)
                .params(params).build();
        String execute = execute(par,timeout);
        StateGetReceiptResult res = null;
        try {
            JSONObject jsonObject = new JSONObject(execute);
            JSONObject result = jsonObject.getJSONObject("result");
            res = StateGetReceiptResult.builder().exitCode(result.getInt("ExitCode"))
                    .messageReturn(result.getStr("Return"))
                    .gasUsed(result.getBigInteger("GasUsed"))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecuteException("getChainTipSetByHeight error " + execute);
        }
        return res;
    }

    /**
     * 指定区块的父链头集合中存储的所有消息
     * @param childBlockCid
     * @param timeout
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public List<MessagesResult> chainGetParentMessages(String childBlockCid, int timeout) throws ExecuteException, ParameException {
        if (StringUtils.isEmpty(childBlockCid)){
            throw new ParameException("paramter cannot be empty");
        }
        List<Object> params = new ArrayList<>();
        HashMap<String, String> _cid = new HashMap<>();
        _cid.put("/", childBlockCid);
        params.add(_cid);
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.CHAIN_GET_PARENT_MESSAGES)
                .params(params).build();
        String execute = execute(par,timeout);
        ArrayList<MessagesResult> messagesResults = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(execute);
            if (!StringUtils.isEmpty(jsonObject.get("error"))){
                throw new WalletException("chainGetParentMessages error: " + execute);
            }
            String resultStr = jsonObject.getStr("result");
            if (!StringUtils.isEmpty(resultStr)){
                JSONArray result = new JSONArray(resultStr);
                for (Object o : result) {
                    JSONObject messageObj = new JSONObject(o);
                    String cid = messageObj.getJSONObject("Cid").getStr("/");
                    JSONObject message = messageObj.getJSONObject("Message");
                    MessagesResult messagesResult = MessagesResult.builder()
                            .cid(cid)
                            ._cid(message.getJSONObject("CID").getStr("/"))
                            .from(message.getStr("From"))
                            .to(message.getStr("To"))
                            .value(Convert.fromAtto(message.getBigDecimal("Value").toString(), Convert.Unit.FIL))
                            .nonce(message.getInt("Nonce"))
                            .gasLimit(message.getInt("GasLimit"))
                            .gasFeeCap(message.getBigInteger("GasFeeCap"))
                            .gasPremium(message.getBigInteger("GasPremium"))
                            .method(message.getInt("Method"))
                            .params(message.getStr("Params"))
                            .version(message.getInt("Version")).build();
                    messagesResults.add(messagesResult);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecuteException("chainGetParentMessages childBlockCid: "+ childBlockCid + "; error " + execute);
        }
        return messagesResults;
    }

    /**
     * 根据区块cid获取父区块所有消息的收据
     * @param childBlockCid
     * @param timeout
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public List<StateGetReceiptResult> chainGetParentReceipts(String childBlockCid, int timeout) throws ExecuteException, ParameException {
        if (StringUtils.isEmpty(childBlockCid)){
            throw new ParameException("paramter cannot be empty");
        }

        JSONArray resultArr = chainGetParentReceiptsHandler(childBlockCid, timeout);
        ArrayList<StateGetReceiptResult> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(resultArr)){
            for (Object o : resultArr) {
                JSONObject receipt = new JSONObject(o);
                result.add(
                        StateGetReceiptResult.builder().exitCode(receipt.getInt("ExitCode"))
                        .messageReturn(receipt.getStr("Return"))
                        .gasUsed(receipt.getBigInteger("GasUsed"))
                        .build()
                );
            }
        }
        return result;
    }

    /**
     * 根据区块cid和消息列表下标索引获取父区块中指定下标消息收据
     * （很难懂的备注，因为对接FILCoin太坑了）
     * @param childBlockCid
     * @param index
     * @param timeout
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public StateGetReceiptResult chainGetParentReceipts(String childBlockCid, int index, int timeout) throws ExecuteException, ParameException {
        if (StringUtils.isEmpty(childBlockCid)){
            throw new ParameException("paramter cannot be empty");
        }
        JSONArray resultArr = chainGetParentReceiptsHandler(childBlockCid, timeout);
        StateGetReceiptResult res = null;
        if (!CollectionUtils.isEmpty(resultArr)){
            Object o = resultArr.get(index);
            if (!StringUtils.isEmpty(o)){
                JSONObject receiptJson = new JSONObject(o);
                res = StateGetReceiptResult.builder()
                        .exitCode(receiptJson.getInt("ExitCode"))
                        .messageReturn(receiptJson.getStr("Return"))
                        .gasUsed(receiptJson.getBigInteger("GasUsed")).build();
            }
        }
        return res;
    }

    /**
     * 查询消息的收据和tipset
     * @param msgCid
     * @return
     */
    public JSONObject stateSearchMsg(String msgCid, int timeout) throws ParameException, ExecuteException {
        if (StringUtils.isEmpty(msgCid)){
            throw new ParameException("paramter cannot be empty");
        }
        List<Object> params = new ArrayList<>();
        HashMap<String, String> _cid = new HashMap<>();
        _cid.put("/", msgCid);
        params.add(_cid);
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.STATE_SEARCH_MSG)
                .params(params).build();
        String execute = execute(par,timeout);
        try {
            JSONObject jsonObject = new JSONObject(execute);
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
            throw new ExecuteException("stateSearchMsg msgCid: "+ msgCid + "; error " + execute);
        }
    }


    /**
     * chainGetParentReceipts JSON RPC数据处理器
     * @param childBlockCid
     * @param timeout
     * @return
     * @throws ExecuteException
     */
    private JSONArray chainGetParentReceiptsHandler(String childBlockCid, int timeout) throws ExecuteException {
        List<Object> params = new ArrayList<>();
        HashMap<String, String> cidParam = new HashMap<>();
        cidParam.put("/", childBlockCid);
        params.add(cidParam);
        RpcPar par = RpcPar.builder().id(1)
                .jsonrpc("2.0")
                .method(FilecoinCnt.CHAIN_GET_PARENT_RECEIPTS)
                .params(params).build();
        String execute = execute(par,timeout);
        StateGetReceiptResult res = null;
        try {
            JSONObject jsonObject = new JSONObject(execute);
            JSONArray result = jsonObject.getJSONArray("result");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExecuteException("getChainTipSetByHeight error " + execute);
        }
    }


    /**
     * 消息json转为消息对象
     * @param jsonObject
     * @return
     */
    private MessagesResult messagesJsonToMessagesResult(JSONObject jsonObject){
        return MessagesResult.builder().from(jsonObject.getStr("From"))
                .to(jsonObject.getStr("To"))
                .version(jsonObject.getInt("Version"))
                .nonce(jsonObject.getInt("Nonce"))
                .value(Convert.fromAtto(jsonObject.getBigInteger("Value").toString(), Convert.Unit.FIL))
                .gasLimit(jsonObject.getInt("GasLimit"))
                .gasFeeCap(jsonObject.getBigInteger("GasFeeCap"))
                .gasPremium(jsonObject.getBigInteger("GasPremium"))
                .method(jsonObject.getInt("Method"))
                .params(jsonObject.getStr("Params"))
                .cid(jsonObject.getJSONObject("CID")
                        .getStr("/"))
                .build();
    }
    /**
     * 区块链json数据转对象
     * @param jsonObject
     * @return
     */
    private ChainResult chainJsonToChainResult(JSONObject jsonObject){
        JSONObject result = jsonObject.getJSONObject("result");
        BigInteger height = result.getBigInteger("Height");
        ArrayList<String> cidList = new ArrayList<>();
        ArrayList<String> parentCidList = new ArrayList<>();
        JSONArray cidJsonArr = result.getJSONArray("Cids");
        JSONArray blocksArr = result.getJSONArray("Blocks");
        if (cidJsonArr != null){
            for (Object o : cidJsonArr) {
                JSONObject cidKy = new JSONObject(o);
                String cid = cidKy.getStr("/");
                if (!StringUtils.isEmpty(cid)){
                    cidList.add(cid);
                }
            }
        }
        if (blocksArr != null && blocksArr.size() > 0){
            JSONArray cidArr = new JSONObject(blocksArr.get(0)).getJSONArray("Parents");
            if (cidArr != null){
                for (Object o : cidArr) {
                    String cid = new JSONObject(o).getStr("/");
                    if (!StringUtils.isEmpty(cid)){
                        parentCidList.add(cid);
                    }
                }
            }
        }
        return ChainResult.builder().height(height).blockCidList(cidList).parentBlockCidList(parentCidList).build();
    }


}

package com.filecoinj;

import com.filecoinj.bean.FilecoinProperties;
import com.filecoinj.config.Args;
import com.filecoinj.constant.FilecoinCnt;
import com.filecoinj.exception.*;
import com.filecoinj.handler.FilecoinHandler;
import com.filecoinj.model.EasySend;
import com.filecoinj.model.GetGas;
import com.filecoinj.model.Transaction;
import com.filecoinj.model.result.*;

import java.math.BigInteger;
import java.util.List;

public class Filecoin {
    private FilecoinHandler filcoinHandler;

    public Filecoin(String url, String nodeAuthorization) {
        Args.getINSTANCE().setNodeType(FilecoinCnt.PRIVATE_NODE);
        Args.getINSTANCE().setUrl(url);
        Args.getINSTANCE().setNodeAuthorization(nodeAuthorization);
        filcoinHandler = new FilecoinHandler();
    }

    public Filecoin(String url, String rpcUserName, String rpcSecret) {
        Args.getINSTANCE().setNodeType(FilecoinCnt.INFURA_NODE);
        Args.getINSTANCE().setUrl(url);
        Args.getINSTANCE().setRpcUserName(rpcUserName);
        Args.getINSTANCE().setRpcSecret(rpcSecret);
        filcoinHandler = new FilecoinHandler();
    }

    public Filecoin(FilecoinProperties filecoinProperties) {
        Args.getINSTANCE().setUrl(filecoinProperties.getRpcUrl());
        //infura节点
        if (filecoinProperties.getNodeType().equalsIgnoreCase(FilecoinCnt.INFURA_NODE)){
            Args.getINSTANCE().setNodeType(FilecoinCnt.INFURA_NODE);
            Args.getINSTANCE().setRpcUserName(filecoinProperties.getRpcUsername());
            Args.getINSTANCE().setRpcSecret(filecoinProperties.getRpcSecret());
        }else {
            //默认私有节点
            Args.getINSTANCE().setNodeType(FilecoinCnt.PRIVATE_NODE);
            Args.getINSTANCE().setNodeAuthorization(filecoinProperties.getRpcToken());
        }
        filcoinHandler = new FilecoinHandler();
    }

    /**
     * 创建钱包
     *
     * @return WalletResult
     */
    public WalletResult createWallet() throws WalletException {
        return filcoinHandler.createWallet();
    }

    /**
     * rpc节点创建钱包
     *
     * @return WalletResult
     */
    public String createWalletRpc() throws WalletException, ExecuteException {
        return filcoinHandler.createWalletRpc(FilecoinCnt.DEFAULT_TIMEOUT);
    }

    /**
     * 导入钱包
     *
     * @return WalletResult
     */
    public WalletResult importWallet(String privatekey) throws WalletException {
        return filcoinHandler.importWallet(privatekey);
    }

    /**
     * 导入钱包
     *
     * @return WalletResult
     */
    public WalletResult importWallet(byte[] privatekey) throws WalletException {
        return filcoinHandler.importWallet(privatekey);
    }

    /**
     * 转账
     *
     * @param transaction 参数
     * @param privatekey  私钥
     * @return SendResult
     * @throws SendException    异常
     * @throws ExecuteException 异常
     */
    public SendResult send(Transaction transaction, String privatekey) throws SendException, ExecuteException {
        return filcoinHandler.send(transaction, privatekey, FilecoinCnt.DEFAULT_TIMEOUT);
    }

    /**
     * 转账
     *
     * @param transaction 参数
     * @param privatekey  私钥
     * @param timeout     请求超时时间 单位：ms
     * @return SendResult
     * @throws SendException    异常
     * @throws ExecuteException 异常
     */
    public SendResult send(Transaction transaction, String privatekey,int timeout) throws SendException, ExecuteException {
        return filcoinHandler.send(transaction, privatekey,timeout);
    }

    /**
     * 简单转账
     *
     * @param send 参数
     * @return SendResult
     * @throws SendException    异常
     * @throws ExecuteException 异常
     */
    public SendResult easySend(EasySend send) throws ParameException, ExecuteException,SendException {
        return filcoinHandler.easySend(send,FilecoinCnt.DEFAULT_TIMEOUT);
    }

    /**
     * 简单转账
     *
     * @param send 参数
     * @param timeout 超时时间 单位：ms
     * @return SendResult
     * @throws SendException    异常
     * @throws ExecuteException 异常
     * @throws ParameException
     */
    public SendResult easySend(EasySend send,int timeout) throws ParameException, ExecuteException,SendException {
        return filcoinHandler.easySend(send,timeout);
    }

    /**
     * 获取nonce值
     *
     * @param address 地址
     * @return int
     * @throws ExecuteException 异常
     * @throws ParameException
     */
    public int getNonce(String address) throws ExecuteException, ParameException {
        return filcoinHandler.getNonce(address,FilecoinCnt.DEFAULT_TIMEOUT);
    }

    /**
     * 获取nonce值
     *
     * @param address 地址
     * @param timeout 超时时间 单位：ms
     * @return int
     * @throws ExecuteException 异常
     * @throws ParameException
     */
    public int getNonce(String address,int timeout) throws ExecuteException, ParameException {
        return filcoinHandler.getNonce(address,timeout);
    }

    /**
     * 获取gas价格
     *
     * @param gas 参数
     * @return GasResult
     * @throws ParameException  异常
     * @throws ExecuteException 异常
     */
    public GasResult getGas(GetGas gas) throws ParameException, ExecuteException {
        return filcoinHandler.getGas(gas,FilecoinCnt.DEFAULT_TIMEOUT);
    }

    /**
     * 获取gas价格
     *
     * @param gas 参数
     * @param timeout 时时间 单位：ms
     * @return GasResult
     * @throws ParameException  异常
     * @throws ExecuteException 异常
     */
    public GasResult getGas(GetGas gas,int timeout) throws ParameException, ExecuteException {
        return filcoinHandler.getGas(gas,timeout);
    }

    /**
     * 查询地址余额
     *
     * @param address 钱包地址
     * @return BalanceResult
     * @throws BalanceOfException 异常
     * @throws ExecuteException   异常
     */
    public BalanceResult balanceOf(String address) throws BalanceOfException, ExecuteException {
        return filcoinHandler.balanceOf(address,FilecoinCnt.DEFAULT_TIMEOUT);
    }

    /**
     * 查询地址余额
     *
     * @param address 钱包地址
     * @param timeout 时时间 单位：ms
     * @return BalanceResult
     * @throws BalanceOfException 异常
     * @throws ExecuteException   异常
     */
    public BalanceResult balanceOf(String address,int timeout) throws BalanceOfException, ExecuteException {
        return filcoinHandler.balanceOf(address,timeout);
    }

    /**
     * 获取钱包默认地址
     * @return
     * @throws ExecuteException
     */
    public String getWalletDefaultAddress() throws ExecuteException {
        return filcoinHandler.getWalletDefaultAddress(FilecoinCnt.DEFAULT_TIMEOUT);
    }

    /**
     * 获取钱包默认地址
     * @return
     * @throws ExecuteException
     */
    public String getWalletDefaultAddress(int timeout) throws ExecuteException {
        return filcoinHandler.getWalletDefaultAddress(timeout);
    }



    /**
     * 校验地址有效性
     * @param address
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public boolean validateAddress(String address) throws ExecuteException, ParameException{
        return filcoinHandler.validateAddress(address,FilecoinCnt.DEFAULT_TIMEOUT);
    }


    /**
     * 校验地址有效性
     * @param address
     * @param timeout
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public boolean validateAddress(String address,int timeout) throws ExecuteException, ParameException{
        return filcoinHandler.validateAddress(address,timeout);
    }

    /**
     * 根据消息cid获取消息信息
     * @param cid
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public MessagesResult getMessageByCid(String cid) throws ExecuteException, ParameException{
        return filcoinHandler.getMessageByCid(cid, FilecoinCnt.DEFAULT_TIMEOUT);
    }

    public MessagesResult getMessageByCid(String cid,int timeout) throws ExecuteException, ParameException{
        return filcoinHandler.getMessageByCid(cid, timeout);
    }

    /**
     * 根据区块cid获取消息
     * @param blockCid
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public List<MessagesResult> getMessagesByBlockCid(String blockCid) throws ExecuteException, ParameException{
        return filcoinHandler.getChainBlockMessages(blockCid, FilecoinCnt.DEFAULT_TIMEOUT);
    }

    public List<MessagesResult> getMessagesByBlockCid(String blockCid, int timeout) throws ExecuteException, ParameException{
        return filcoinHandler.getChainBlockMessages(blockCid, timeout);
    }

    /**
     * 获取当前最新区块链
     * @return
     * @throws ExecuteException
     */
    public ChainResult getChainHead() throws ExecuteException{
        return filcoinHandler.getChainHead(FilecoinCnt.DEFAULT_TIMEOUT);
    }

    public ChainResult getChainHead(int timeout) throws ExecuteException{
        return filcoinHandler.getChainHead(timeout);
    }

    /**
     * 根据区块高度获取区块链
     * @param height
     * @return
     * @throws ExecuteException
     * @throws ParameException
     */
    public ChainResult getChainTipSetByHeight(BigInteger height) throws ExecuteException, ParameException {
        return filcoinHandler.getChainTipSetByHeight(height, FilecoinCnt.DEFAULT_TIMEOUT);
    }

    public ChainResult getChainTipSetByHeight(BigInteger height, int timeout) throws ExecuteException, ParameException {
        return filcoinHandler.getChainTipSetByHeight(height, timeout);
    }



}

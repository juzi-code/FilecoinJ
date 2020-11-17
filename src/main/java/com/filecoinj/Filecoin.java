package com.filecoinj;

import com.filecoinj.config.Args;
import com.filecoinj.constant.FilecoinCnt;
import com.filecoinj.exception.BalanceOfException;
import com.filecoinj.exception.ExecuteException;
import com.filecoinj.exception.ParameException;
import com.filecoinj.exception.SendException;
import com.filecoinj.exception.WalletException;
import com.filecoinj.handler.FilecoinHandler;
import com.filecoinj.model.EasySend;
import com.filecoinj.model.GetGas;
import com.filecoinj.model.Transaction;
import com.filecoinj.model.result.BalanceResult;
import com.filecoinj.model.result.GasResult;
import com.filecoinj.model.result.SendResult;
import com.filecoinj.model.result.WalletResult;

public class Filecoin {
    private FilecoinHandler filcoinHandler;

    public Filecoin(String url, String nodeAuthorization) {
        Args.getINSTANCE().setUrl(url);
        Args.getINSTANCE().setNodeAuthorization(nodeAuthorization);
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


}

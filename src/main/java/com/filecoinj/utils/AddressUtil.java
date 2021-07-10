package com.filecoinj.utils;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.filecoinj.exception.AddressException;
import com.filecoinj.exception.WalletException;
import com.filecoinj.model.Address;

import java.util.HashMap;

public class AddressUtil {
    /**
     * secp256k1地址字节长度
     */
    private final static int addressLength = 21;

    public static Address initAddress(String addressStr) throws AddressException {
        if (StrUtil.isBlank(addressStr)){
            throw new NullPointerException("addressStr 参数不能为空");
        }
        //去掉拼接的前两位
        String substring = addressStr.substring(2);
        //获取type
        String typeStr = addressStr.substring(1, 2);
        //获取网络类型
        String network = addressStr.substring(0,1);
        int type = Integer.parseInt(typeStr);
        if (type != 1 && type !=2 && type != 3){
            throw new AddressException("错误的地址类型");
        }

        byte[] addressBytes = new byte[addressLength];

        switch (type){
            case 1:
                //secp256k1 地址类型
                addressBytes[0] = (byte) type;
                break;
            case 2:
            case 3:
                throw new AddressException("暂不支持的地址类型");
            default:
                throw new AddressException("错误地址类型");
        }

        System.arraycopy(Base32.decode(substring), 0, addressBytes, 1, 20);
        return Address.builder().address(addressStr)
                .bytes(addressBytes)
                .network(network).build();

    }

    public static String convertToLotus(String originPrivateKey) throws WalletException {
        try {
            String base64Key = Base64.encode(HexUtil.decodeHex(originPrivateKey));
            HashMap<String, String> map = new HashMap<>();
            map.put("Type", "secp256k1");
            map.put("PrivateKey", base64Key);
            String json = JSON.toJSONString(map);
            return HexUtil.encodeHexStr(json);
        } catch (Exception e) {
            throw new WalletException("private key parse error");
        }
    }

    public static String convertToOrigin(String privateKey) throws WalletException {
        try {
            String json = HexUtil.decodeHexStr(privateKey);
            JSONObject jsonObject = new JSONObject(json);
            String base64Key = jsonObject.getStr("PrivateKey");
            return HexUtil.encodeHexStr(Base64.decode(base64Key));
        } catch (Exception e) {
            throw new WalletException("private key parse error");
        }
    }
}

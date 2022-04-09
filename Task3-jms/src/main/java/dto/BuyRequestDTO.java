package dto;

import java.io.Serializable;

public class BuyRequestDTO implements Serializable {
    private static final long serialVersionUID = 8109158516343803478L;

    private String buyers_name;
    private int buyers_account_num;
    private String goodsName;

    public BuyRequestDTO(String buyers_name, int buyers_account_num, String goodsName) {
        this.buyers_name = buyers_name;
        this.buyers_account_num = buyers_account_num;
        this.goodsName = goodsName;
    }

    public String getBuyers_name() {
        return buyers_name;
    }

    public void setBuyers_name(String buyers_name) {
        this.buyers_name = buyers_name;
    }

    public int getBuyers_account_num() {
        return buyers_account_num;
    }

    public void setBuyers_account_num(int buyers_account_num) {
        this.buyers_account_num = buyers_account_num;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}

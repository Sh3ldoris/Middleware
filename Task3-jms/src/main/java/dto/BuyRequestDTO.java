package dto;

import java.io.Serializable;

public class BuyRequestDTO implements Serializable {
    private static final long serialVersionUID = 8109158516343803478L;

    private String buyersName;
    private int buyersAccountNum;
    private String goodsName;

    public BuyRequestDTO(String buyers_name, int buyers_account_num, String goodsName) {
        this.buyersName = buyers_name;
        this.buyersAccountNum = buyers_account_num;
        this.goodsName = goodsName;
    }

    public String getBuyersName() {
        return buyersName;
    }

    public void setBuyersName(String buyersName) {
        this.buyersName = buyersName;
    }

    public int getBuyersAccountNum() {
        return buyersAccountNum;
    }

    public void setBuyersAccountNum(int buyersAccountNum) {
        this.buyersAccountNum = buyersAccountNum;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}

package dto;

import java.io.Serializable;

public class BuyResponseDTO implements Serializable {
    private static final long serialVersionUID = 8109458516343853478L;

    private SellResult sellResult;
    private int sellersAccountNum;
    private int priceToPay;

    public BuyResponseDTO(SellResult sellResult, int sellersAccountNum, int priceToPay) {
        this.sellResult = sellResult;
        this.sellersAccountNum = sellersAccountNum;
        this.priceToPay = priceToPay;
    }

    public SellResult getSellResult() {
        return sellResult;
    }

    public void setSellResult(SellResult sellResult) {
        this.sellResult = sellResult;
    }

    public int getSellersAccountNum() {
        return sellersAccountNum;
    }

    public void setSellersAccountNum(int sellersAccountNum) {
        this.sellersAccountNum = sellersAccountNum;
    }

    public int getPriceToPay() {
        return priceToPay;
    }

    public void setPriceToPay(int priceToPay) {
        this.priceToPay = priceToPay;
    }
}

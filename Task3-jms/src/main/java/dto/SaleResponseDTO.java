package dto;

import java.io.Serializable;

public class SaleResponseDTO implements Serializable {
    private static final long serialVersionUID = 8109458511343803478L;

    private SellResult sellResult;
    private String message;
    private String goodsName;

    public SaleResponseDTO(SellResult sellResult, String message, String goodsName) {
        this.sellResult = sellResult;
        this.message = message;
        this.goodsName = goodsName;
    }

    public SellResult getSellResult() {
        return sellResult;
    }

    public void setSellResult(SellResult sellResult) {
        this.sellResult = sellResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}

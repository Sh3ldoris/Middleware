package dto;

import java.io.Serializable;

public class SaleResponseDTO implements Serializable {
    private static final long serialVersionUID = 8109458511343803478L;

    private SellResult sellResult;
    private String message;

    public SaleResponseDTO(SellResult sellResult, String message) {
        this.sellResult = sellResult;
        this.message = message;
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
}

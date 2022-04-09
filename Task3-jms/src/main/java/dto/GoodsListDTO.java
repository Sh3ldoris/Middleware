package dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Goods;

public class GoodsListDTO implements Serializable {
    private static final long serialVersionUID = 8109458516343803478L;

    private List<Goods> goodsList;

    public GoodsListDTO(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    public GoodsListDTO(Map<String, Goods> goodsMap) {
        this.goodsList = new ArrayList<>(goodsMap.values());
    }

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }
}

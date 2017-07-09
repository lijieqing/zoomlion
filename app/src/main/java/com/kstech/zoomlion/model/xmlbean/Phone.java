package com.kstech.zoomlion.model.xmlbean;

/**
 * Created by lijie on 2017/7/9.
 */
public class Phone {
    private String Name;
    private Integer MemerySize;
    private Float Prices;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getMemerySize() {
        return MemerySize;
    }

    public void setMemerySize(Integer memerySize) {
        MemerySize = memerySize;
    }

    public Float getPrices() {
        return Prices;
    }

    public void setPrices(Float prices) {
        Prices = prices;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "Name='" + Name + '\'' +
                ", MemerySize=" + MemerySize +
                ", Prices=" + Prices +
                '}';
    }
}

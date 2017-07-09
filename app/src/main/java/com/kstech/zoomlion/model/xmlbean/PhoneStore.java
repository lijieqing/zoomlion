package com.kstech.zoomlion.model.xmlbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijie on 2017/7/9.
 */
public class PhoneStore {
    private String Address;
    private String StoreName;
    private Boolean Working;
    private List<Phone> phones;

    public PhoneStore() {
        this.phones = new ArrayList<>();
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStoreName() {
        return StoreName;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones.addAll(phones);
    }

    public Boolean getWorking() {
        return Working;
    }

    public void setWorking(Boolean working) {
        Working = working;
    }

    @Override
    public String toString() {
        return "PhoneStore{" +
                "Address='" + Address + '\'' +
                ", StoreName='" + StoreName + '\'' +
                ", isWorking=" + Working +
                ", phones=" + phones +
                '}';
    }
}

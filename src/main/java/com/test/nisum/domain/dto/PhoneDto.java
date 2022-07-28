package com.test.nisum.domain.dto;

public class PhoneDto {

    private String number;
    private String cityCode;
    private String countryCode;

    public PhoneDto() {
//        Empty
    }

    public PhoneDto(String number, String cityCode, String countryCode) {
        this.number = number;
        this.cityCode = cityCode;
        this.countryCode = countryCode;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCityCode() {
        return this.cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "number='" + this.number + '\'' +
                ", cityCode='" + this.cityCode + '\'' +
                ", countryCode='" + this.countryCode + '\'' +
                '}';
    }
}

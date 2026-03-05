
package com.code.fullstack_backend.dto;

public class PostalInfoDTO {
    private String district;
    private String state;

    public PostalInfoDTO() {}

    public PostalInfoDTO(String district, String state) {
        this.district = district;
        this.state = state;
    }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}

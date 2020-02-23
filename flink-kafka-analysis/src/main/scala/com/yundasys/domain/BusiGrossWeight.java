package com.yundasys.domain;

import java.io.Serializable;

public class BusiGrossWeight implements Serializable {
    /**
     * 序列化
     */
    private static final long serialVersionUID = 7990850443415922868L;

    // 发车凭证
    private String transDocId;

    // 发车凭证类型
    private String transDocIdType;

    // 车牌号
    private String carId;

    // 车辆类型
    private String carType;

    // 站点类型
    private String siteType;

    // 站点名称
    private String siteName;

    // 站点编码
    private String siteCode;

    // 毛重
    private String grossWeight;

    // 平均皮重
    private String tareWeight;

    // 进出状态
    private String inOutFlag;

    // 称重时间
    private String weightTime;

    // 入库时间
    private String createTime;

    public String getTransDocId() {
        return transDocId;
    }

    public void setTransDocId(String transDocId) {
        this.transDocId = transDocId;
    }

    public String getTransDocIdType() {
        return transDocIdType;
    }

    public void setTransDocIdType(String transDocIdType) {
        this.transDocIdType = transDocIdType;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getTareWeight() {
        return tareWeight;
    }

    public void setTareWeight(String tareWeight) {
        this.tareWeight = tareWeight;
    }

    public String getInOutFlag() {
        return inOutFlag;
    }

    public void setInOutFlag(String inOutFlag) {
        this.inOutFlag = inOutFlag;
    }

    public String getWeightTime() {
        return weightTime;
    }

    public void setWeightTime(String weightTime) {
        this.weightTime = weightTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "BusiGrossWeight{" +
                "transDocId='" + transDocId + '\'' +
                ", transDocIdType='" + transDocIdType + '\'' +
                ", carId='" + carId + '\'' +
                ", carType='" + carType + '\'' +
                ", siteType='" + siteType + '\'' +
                ", siteName='" + siteName + '\'' +
                ", siteCode='" + siteCode + '\'' +
                ", grossWeight='" + grossWeight + '\'' +
                ", tareWeight='" + tareWeight + '\'' +
                ", inOutFlag='" + inOutFlag + '\'' +
                ", weightTime='" + weightTime + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}

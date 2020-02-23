package com.yunda.bean;

import org.omg.CORBA.INTERNAL;

public class Loadometer {
    /**
     * 发车凭证：
     * 发车凭证类型：0：网络正班/网络加班；1：物流卡班；2：网点直跑车
     * 车牌号：
     * 车辆型号：0：高栏；1：非高栏
     *                   车型仓位<=56的为高栏
     *                   车型仓位>56的为非高栏
     * 站点类型：0：始发站；1：途径站；2：目的站
     * 站点名称：
     * 毛重KG：
     * 平均皮重KG：
     * 进出状态：1.出站、2.进站、3.即装即卸
     * 称重时间：
     * 入库时间：
     */
    private String depart_proof;
    private int depart_proof_type;
    private String car_number;
    private int car_type;
    private int site_type;
    private String site_name;
    private double gross_weight;
    private double  average_tare;
    private int state;
    private long weighing_tine;
    private long warehouse_time;

    public Loadometer(String depart_proof) {
        this.depart_proof = depart_proof;
    }

    public Loadometer(String depart_proof, int depart_proof_type, String car_number, int car_type, int site_type, String site_name, double gross_weight, double average_tare, int state, long weighing_tine, long warehouse_time) {
        this.depart_proof = depart_proof;
        this.depart_proof_type = depart_proof_type;
        this.car_number = car_number;
        this.car_type = car_type;
        this.site_type = site_type;
        this.site_name = site_name;
        this.gross_weight = gross_weight;
        this.average_tare = average_tare;
        this.state = state;
        this.weighing_tine = weighing_tine;
        this.warehouse_time = warehouse_time;
    }


    public String getDepart_proof() {
        return depart_proof;
    }

    public void setDepart_proof(String depart_proof) {
        this.depart_proof = depart_proof;
    }

    public int getDepart_proof_type() {
        return depart_proof_type;
    }

    public void setDepart_proof_type(int depart_proof_type) {
        this.depart_proof_type = depart_proof_type;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public int getCar_type() {
        return car_type;
    }

    public void setCar_type(int car_type) {
        this.car_type = car_type;
    }

    public int getSite_type() {
        return site_type;
    }

    public void setSite_type(int site_type) {
        this.site_type = site_type;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public double getGross_weight() {
        return gross_weight;
    }

    public void setGross_weight(double gross_weight) {
        this.gross_weight = gross_weight;
    }

    public double getAverage_tare() {
        return average_tare;
    }

    public void setAverage_tare(double average_tare) {
        this.average_tare = average_tare;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getWeighing_tine() {
        return weighing_tine;
    }

    public void setWeighing_tine(long weighing_tine) {
        this.weighing_tine = weighing_tine;
    }

    public long getWarehouse_time() {
        return warehouse_time;
    }

    public void setWarehouse_time(long warehouse_time) {
        this.warehouse_time = warehouse_time;
    }

    @Override
    public String toString() {
        return "Loadometer{" +
                "depart_proof='" + depart_proof + '\'' +
                ", depart_proof_type=" + depart_proof_type +
                ", car_number='" + car_number + '\'' +
                ", car_type=" + car_type +
                ", site_type=" + site_type +
                ", site_name='" + site_name + '\'' +
                ", gross_weight=" + gross_weight +
                ", average_tare=" + average_tare +
                ", state=" + state +
                ", weighing_tine=" + weighing_tine +
                ", warehouse_time=" + warehouse_time +
                '}';
    }
}

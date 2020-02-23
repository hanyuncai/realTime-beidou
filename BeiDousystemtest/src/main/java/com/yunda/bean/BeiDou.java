package com.yunda.bean;

public class BeiDou {
    private String  depart_proof;
    private int  site_type;
    private String site_encoding;
    private double loadometer_load_gross_weight;
    private double loadometer_load_net_weight;
    private double loadometer_unload_gross_weight;
    private double loadometer_unload_net_weight;
    private double loadometer_load_unload_weight;
    private double scan_load_weight;
    private int load_number;
    private double scan_unload_weight;
    private int unload_number;
    private long database_timestamp;

    public BeiDou() {
    }

    public BeiDou(String depart_proof, int site_type, String site_encoding, double loadometer_load_gross_weight, double loadometer_load_net_weight, double loadometer_unload_gross_weight, double loadometer_unload_net_weight, double loadometer_load_unload_weight, double scan_load_weight, int load_number, double scan_unload_weight, int unload_number, long database_timestamp) {
        this.depart_proof = depart_proof;
        this.site_type = site_type;
        this.site_encoding = site_encoding;
        this.loadometer_load_gross_weight = loadometer_load_gross_weight;
        this.loadometer_load_net_weight = loadometer_load_net_weight;
        this.loadometer_unload_gross_weight = loadometer_unload_gross_weight;
        this.loadometer_unload_net_weight = loadometer_unload_net_weight;
        this.loadometer_load_unload_weight = loadometer_load_unload_weight;
        this.scan_load_weight = scan_load_weight;
        this.load_number = load_number;
        this.scan_unload_weight = scan_unload_weight;
        this.unload_number = unload_number;
        this.database_timestamp = database_timestamp;
    }

    public String getDepart_proof() {
        return depart_proof;
    }

    public void setDepart_proof(String depart_proof) {
        this.depart_proof = depart_proof;
    }

    public int getSite_type() {
        return site_type;
    }

    public void setSite_type(int site_type) {
        this.site_type = site_type;
    }

    public String getSite_encoding() {
        return site_encoding;
    }

    public void setSite_encoding(String site_encoding) {
        this.site_encoding = site_encoding;
    }

    public double getLoadometer_load_gross_weight() {
        return loadometer_load_gross_weight;
    }

    public void setLoadometer_load_gross_weight(double loadometer_load_gross_weight) {
        this.loadometer_load_gross_weight = loadometer_load_gross_weight;
    }

    public double getLoadometer_load_net_weight() {
        return loadometer_load_net_weight;
    }

    public void setLoadometer_load_net_weight(double loadometer_load_net_weight) {
        this.loadometer_load_net_weight = loadometer_load_net_weight;
    }

    public double getLoadometer_unload_gross_weight() {
        return loadometer_unload_gross_weight;
    }

    public void setLoadometer_unload_gross_weight(double loadometer_unload_gross_weight) {
        this.loadometer_unload_gross_weight = loadometer_unload_gross_weight;
    }

    public double getLoadometer_unload_net_weight() {
        return loadometer_unload_net_weight;
    }

    public void setLoadometer_unload_net_weight(double loadometer_unload_net_weight) {
        this.loadometer_unload_net_weight = loadometer_unload_net_weight;
    }

    public double getLoadometer_load_unload_weight() {
        return loadometer_load_unload_weight;
    }

    public void setLoadometer_load_unload_weight(double loadometer_load_unload_weight) {
        this.loadometer_load_unload_weight = loadometer_load_unload_weight;
    }

    public double getScan_load_weight() {
        return scan_load_weight;
    }

    public void setScan_load_weight(double scan_load_weight) {
        this.scan_load_weight = scan_load_weight;
    }

    public int getLoad_number() {
        return load_number;
    }

    public void setLoad_number(int load_number) {
        this.load_number = load_number;
    }

    public double getScan_unload_weight() {
        return scan_unload_weight;
    }

    public void setScan_unload_weight(double scan_unload_weight) {
        this.scan_unload_weight = scan_unload_weight;
    }

    public int getUnload_number() {
        return unload_number;
    }

    public void setUnload_number(int unload_number) {
        this.unload_number = unload_number;
    }

    public long getDatabase_timestamp() {
        return database_timestamp;
    }

    public void setDatabase_timestamp(long database_timestamp) {
        this.database_timestamp = database_timestamp;
    }

    @Override
    public String toString() {
        return "BeiDou{" +
                "depart_proof='" + depart_proof + '\'' +
                ", site_type=" + site_type +
                ", site_encoding='" + site_encoding + '\'' +
                ", loadometer_load_gross_weight=" + loadometer_load_gross_weight +
                ", loadometer_load_net_weight=" + loadometer_load_net_weight +
                ", loadometer_unload_gross_weight=" + loadometer_unload_gross_weight +
                ", loadometer_unload_net_weight=" + loadometer_unload_net_weight +
                ", loadometer_load_unload_weight=" + loadometer_load_unload_weight +
                ", scan_load_weight=" + scan_load_weight +
                ", load_number=" + load_number +
                ", scan_unload_weight=" + scan_unload_weight +
                ", unload_number=" + unload_number +
                ", database_timestamp=" + database_timestamp +
                '}';
    }
}

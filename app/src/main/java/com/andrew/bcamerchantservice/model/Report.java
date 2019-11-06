package com.andrew.bcamerchantservice.model;

public class Report {
    private String frlid;
    private String report_name;
    private boolean report_is_checked;

    public String getFrlid() {
        return frlid;
    }

    public String getReport_name() {
        return report_name;
    }

    public void setReport_is_checked(boolean report_is_checked) {
        this.report_is_checked = report_is_checked;
    }

    public boolean isReport_is_checked() {
        return report_is_checked;
    }

    public Report() {
    }

    public Report(String frlid, String report_name, boolean report_is_checked) {
        this.frlid = frlid;
        this.report_name = report_name;
        this.report_is_checked = report_is_checked;
    }
}

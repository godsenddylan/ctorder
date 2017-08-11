package com.tencentpic.fhpic.model;

import java.io.Serializable;

public class CarModel extends BaseModel implements Serializable {
    private Integer id;

    private String carmark;

    private String drivername;

    private String driverphone;

    private String guid;

    private String isMain;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCarmark() {
        return carmark;
    }

    public void setCarmark(String carmark) {
        this.carmark = carmark == null ? null : carmark.trim();
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername == null ? null : drivername.trim();
    }

    public String getDriverphone() {
        return driverphone;
    }

    public void setDriverphone(String driverphone) {
        this.driverphone = driverphone == null ? null : driverphone.trim();
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid == null ? null : guid.trim();
    }

    public String getIsMain() {
        return isMain;
    }

    public void setIsMain(String isMain) {
        this.isMain = isMain == null ? null : isMain.trim();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CarModel other = (CarModel) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCarmark() == null ? other.getCarmark() == null : this.getCarmark().equals(other.getCarmark()))
            && (this.getDrivername() == null ? other.getDrivername() == null : this.getDrivername().equals(other.getDrivername()))
            && (this.getDriverphone() == null ? other.getDriverphone() == null : this.getDriverphone().equals(other.getDriverphone()))
            && (this.getGuid() == null ? other.getGuid() == null : this.getGuid().equals(other.getGuid()))
            && (this.getIsMain() == null ? other.getIsMain() == null : this.getIsMain().equals(other.getIsMain()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCarmark() == null) ? 0 : getCarmark().hashCode());
        result = prime * result + ((getDrivername() == null) ? 0 : getDrivername().hashCode());
        result = prime * result + ((getDriverphone() == null) ? 0 : getDriverphone().hashCode());
        result = prime * result + ((getGuid() == null) ? 0 : getGuid().hashCode());
        result = prime * result + ((getIsMain() == null) ? 0 : getIsMain().hashCode());
        return result;
    }
}
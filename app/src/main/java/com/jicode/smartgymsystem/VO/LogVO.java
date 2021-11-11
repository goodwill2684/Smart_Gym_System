package com.jicode.smartgymsystem.VO;

public class LogVO {
    String time;
    Long bpm;
    Double lat;
    Double lon;
    Double sensor_x;
    Double sensor_y;
    Double sensor_z;

    public LogVO(String time, Long bpm, Double lat, Double lon, Double sensor_x, Double sensor_y, Double sensor_z) {
        this.time = time;
        this.bpm = bpm;
        this.lat = lat;
        this.lon = lon;
        this.sensor_x = sensor_x;
        this.sensor_y = sensor_y;
        this.sensor_z = sensor_z;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getBpm() {
        return bpm;
    }

    public void setBpm(Long bpm) {
        this.bpm = bpm;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getSensor_x() {
        return sensor_x;
    }

    public void setSensor_x(Double sensor_x) {
        this.sensor_x = sensor_x;
    }

    public Double getSensor_y() {
        return sensor_y;
    }

    public void setSensor_y(Double sensor_y) {
        this.sensor_y = sensor_y;
    }

    public Double getSensor_z() {
        return sensor_z;
    }

    public void setSensor_z(Double sensor_z) {
        this.sensor_z = sensor_z;
    }
}

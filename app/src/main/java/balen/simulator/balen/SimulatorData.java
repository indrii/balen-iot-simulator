package balen.simulator.balen;

import android.location.Location;

public class SimulatorData {
        private String deviceId;
        private Double temperature;
        private Double latitude;
        private Double longtitude;
        private String door;
        private String Location;
        private String Battrey;
        private String DateTime;

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getBattrey() {
        return Battrey;
    }

    public void setBattrey(String battrey) {
        Battrey = battrey;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getDoor() {
        return door;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public void setDoor(String door) {
        this.door = door;
    }
}

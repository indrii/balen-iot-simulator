package balen.simulator.balen;

public class SimulatorData {
        private String deviceId;
        private Double temperature;
        private Double latitude;
        private Double longitude;
        private String door;
       // private String location;
        private Double battery;
        //private String datetime;

//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }

    public Double getBattery() {
        return battery;
    }

    public void setBattery(Double battery) {
        this.battery = battery;
    }

//    public String getDatetime() {
//        return datetime;
//    }
//
//    public void setDatetime(String datetime) {
//        this.datetime = datetime;
//    }

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

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setDoor(String door) {
        this.door = door;
    }
}

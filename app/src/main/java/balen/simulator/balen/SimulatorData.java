package balen.simulator.balen;

public class SimulatorData {
        private String deviceId;
        private Double temperature;
        private Double humadity;
        private Double latitude;
        private Double longitude;
        private String doorState;

        private String brake;
       // private String location;
        private Double battery;
        private Double bensin;
        //private String datetime;
        private Double speed;
        private String imei;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speeds) {
        this.speed = speeds;
    }
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

    public Double getHumadity() { return humadity; }

    public void setHumadity(Double humadity) { this.humadity = humadity; }

    public String getBrake() { return brake; }

    public void setBrake(String brake) { this.brake = brake; }

    public Double getBensin() { return bensin; }

    public void setBensin(Double bensin) { this.bensin = bensin; }

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

    public String getDoorState() {
        return doorState;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setDoorState(String doorState) {
        this.doorState = doorState;
    }



}

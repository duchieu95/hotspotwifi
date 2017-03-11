package com.example.whp.models;

/**
 * Created by hieuho on 10/03/2017.
 */

public class DevicesConnect {
    public String IpAddr;
    public String HWAddr;
    public String Device;
    public boolean isConnect;

    public DevicesConnect(String IpAddr,String HWAddr,String Device,boolean isConnect){
        super();
        this.IpAddr = IpAddr;
        this.HWAddr = HWAddr;
        this.Device = Device;
        this.isConnect = isConnect;
    }


    public String getIpAddr() {
        return IpAddr;
    }

    public void setIpAddr(String ipAddr) {
        IpAddr = ipAddr;
    }

    public String getHWAddr() {
        return HWAddr;
    }

    public void setHWAddr(String HWAddr) {
        this.HWAddr = HWAddr;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }
}

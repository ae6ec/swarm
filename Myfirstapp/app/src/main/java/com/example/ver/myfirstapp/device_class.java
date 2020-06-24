package com.example.ver.myfirstapp;

public class device_class {



        private String BluName;
        private String BluMACAdd;
        private String BluDevOS;
        private String BludevType;

        public device_class(String name , String macadd) {
            if(name=="")
                BluName ="<null>";
            else if(macadd=="")
                BluMACAdd = "<null>";
            else {
                BluName = name;
                BluMACAdd = macadd;
            }
        }
    public device_class() {
        BluName ="<null>";
        BluMACAdd = "<null>";

    }

        //if required

        public String getBluDeviceName(){return BluName;}
        public String getBluDeviceMAC(){return BluMACAdd;}



}
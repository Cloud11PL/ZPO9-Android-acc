package com.example.wojciech.zpo9_android_acc;

public class pointData {
    private String X;
    private String Y;
    private String Z;

    public pointData(String x, String y, String z) {
        X = x;
        Y = y;
        Z = z;
    }

    public String getX() {
        return X;
    }

    public String getY() {
        return Y;
    }

    public String getZ() {
        return Z;
    }

    @Override
    public String toString() {
        return  "X='" + X + '\'' +
                "Y='" + Y + '\'' +
                "Z='" + Z + '\'';
    }
}

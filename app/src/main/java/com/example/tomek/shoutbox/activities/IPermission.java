package com.example.tomek.shoutbox.activities;

public interface IPermission {
    boolean haveWritePermission();
    void requestPermission();
}

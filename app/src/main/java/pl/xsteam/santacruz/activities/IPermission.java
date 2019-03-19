package pl.xsteam.santacruz.activities;

public interface IPermission {
    boolean haveWritePermission();
    void requestPermission();
}

package pl.xsteam.santacruz.activities;

public interface SbListener {
    void wyslano_wiadomosc(boolean success);
    void polajkowanoWiadomosc(int msgid, int likedMsgPosition);
    void dismissDialog();
    void anulujOdswiezanie();
    void odswiezWiadomosci();
    void pokazDialogDodatki();
    void pobranoStarsze();
    void wstawLinkObrazka(String link);
}

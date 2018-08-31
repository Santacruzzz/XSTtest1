package com.example.tomek.shoutbox;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.tomek.shoutbox.activities.MainActivity;
import com.example.tomek.shoutbox.adapters.PagerAdapterTagiEmotki;
import com.example.tomek.shoutbox.utils.Typy;
import com.example.tomek.shoutbox.utils.Utils;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class DialogDodatki extends DialogFragment {
    private SharedPreferences prefs;
    private int halfHeight;
    private int width;
    private int height;
    private int kbsize;
    private AddonSelectedListener listener;
    private boolean useKbSize;
    private MainActivity mAct;
    private ArrayList<String> listaObrazkowZdysku;
    private boolean listLoaded;
    private PagerAdapterTagiEmotki pagerAdapterTagiEmotki;
    private DialogListener dialogListeer;

    public interface DialogListener {
        void dialogDismissed();
    }

    public DialogDodatki() {
        halfHeight = 0;
        width = 0;
        height = 0;
        mAct = null;
        listLoaded = false;
    }

    public DialogDodatki(MainActivity pAct) {
        listLoaded = false;
        listener = null;
        mAct = pAct;
        prefs = mAct.getSharedPrefs();
        useKbSize = prefs.getBoolean(Typy.PREFS_USE_KB_SIZE,false);
        kbsize = prefs.getInt(Typy.PREFS_KB_SIZE, 200);
        listaObrazkowZdysku = Utils.getArrayList(mAct.getSharedPrefs(), Typy.PREFS_OBRAZKI_Z_DYSKU);
    }

    public void setDialogListeer(DialogListener listener) {
        dialogListeer = listener;
    }

    private int getSoftButtonsBarHeight() {
        // getRealMetrics is only available with API 17 and +
        DisplayMetrics metrics = new DisplayMetrics();
        mAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        mAct.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }

    private void calculateHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Window window = getDialog().getWindow();
        int virtualKeysSize = getSoftButtonsBarHeight();
        if (window != null) {
            window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
            halfHeight = displayMetrics.heightPixels / 2;
            if (kbsize > halfHeight) {
                kbsize = halfHeight;
            }

            if (useKbSize) {
                height = kbsize - virtualKeysSize;
            } else {
                height = halfHeight;
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.DialogDodatkiTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dodatki_layout, container, false);
        setDialogPosition();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton mBtnAddImage = view.findViewById(R.id.buttonAddImage);
        mBtnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    dismiss();
                    listener.pickImageSelected();
                }
            }
        });
        ViewPager pagerTagiEmotki = view.findViewById(R.id.viewPagerTagiEmotki);
        TabLayout mTabsTagiEmotki = view.findViewById(R.id.tabsTagiEmotki);
        pagerAdapterTagiEmotki = new PagerAdapterTagiEmotki(getChildFragmentManager(), listener, listaObrazkowZdysku);
        mTabsTagiEmotki.setupWithViewPager(pagerTagiEmotki);
        pagerTagiEmotki.setAdapter(pagerAdapterTagiEmotki);

        Window window = getDialog().getWindow();
        if (window != null) {
            //window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
            window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            //window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        calculateHeight();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(width, height);
        }
    }

    public static DialogDodatki newInstance(MainActivity act) {
        return new DialogDodatki(act);
    }

    public void imagesFound(ArrayList<String> lista) {
        if (pagerAdapterTagiEmotki != null) {
            pagerAdapterTagiEmotki.setLista(lista);
        } else {
            listaObrazkowZdysku = lista;
        }
    }

    public interface AddonSelectedListener {
        void smileySelected(String smiley);
        void tagSelected(String tag);
        void pickImageSelected();
        void imageSelected(String path);
    }

    public void setAddonSelectedListener(AddonSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroy() {
        dialogListeer.dialogDismissed();
        super.onDestroy();
    }

    @Override
    public void dismiss() {
        dialogListeer.dialogDismissed();
        super.dismiss();
    }

    private void setDialogPosition() {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            WindowManager.LayoutParams params = window.getAttributes();
            params.y = dpToPx(0);
            window.setAttributes(params);
        }
    }

    private int dpToPx(int dp) {
        DisplayMetrics metrics = mAct.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

}

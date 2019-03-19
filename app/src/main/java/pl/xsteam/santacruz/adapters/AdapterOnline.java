package pl.xsteam.santacruz.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pl.xsteam.santacruz.R;
import pl.xsteam.santacruz.User;
import pl.xsteam.santacruz.activities.IMainActivity;
import pl.xsteam.santacruz.activities.MainActivity;
import pl.xsteam.santacruz.utils.Typy;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tomek on 2017-11-05.
 */

public class AdapterOnline extends BaseAdapter {

    private ArrayList<User> listaOnline;
    private LayoutInflater inflater;
    private IMainActivity imain;
    private MainActivity mAct;
    private PrettyTime ptime;

    public AdapterOnline(MainActivity act, ArrayList<User> _list) {
        listaOnline = _list;
        Context context = act.getApplicationContext();
        inflater = LayoutInflater.from(context);
        imain = (IMainActivity) act;
        mAct = act;
        ptime = new PrettyTime();
    }

    @Override
    public int getCount() {
        return listaOnline.size();
    }

    @Override
    public Object getItem(int i) {
        if (i >= 0 && i < listaOnline.size()) {
            return listaOnline.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        if (i >= 0 && i < listaOnline.size()) {
            return listaOnline.get(i).getUserid();
        }
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (inflater == null) {
            inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        View row = convertView;
        if (row == null) {
            assert inflater != null;
            row = inflater.inflate(R.layout.online_item, viewGroup, false);
        }
        TextView nick = row.findViewById(R.id.v_nick);
        TextView status = row.findViewById(R.id.statusOnline);
        TextView txtPlatforma = row.findViewById(R.id.v_txtPlatforma);
        TextView txtUa = row.findViewById(R.id.v_txtUa);
        ImageView platformaImage = row.findViewById(R.id.v_platforma);
        User mUser;

        try {
            mUser = listaOnline.get(i);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        ImageView avatar = row.findViewById(R.id.v_avatarOnline);
        Picasso.with(mAct).load(mUser.getAvatarUrl()).into(avatar);

        nick.setText(mUser.getNick());
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            Typeface face = Typeface.createFromAsset(mAct.getAssets(),
                    "fonts/nasalization.ttf");
            nick.setTypeface(face);
        }

        final RelativeLayout layOnline = row.findViewById(R.id.layOnline);
        row.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layOnline.getBackground().setHotspot(event.getX(), event.getY());
                layOnline.performClick();
                return(false);
            }
        });

        switch (mUser.getPlatform()) {
            case "Windows":
                platformaImage.setImageResource(R.drawable.windows);
                break;
            case "Android":
                platformaImage.setImageResource(R.drawable.android);
                break;
            case "Linux":
                platformaImage.setImageResource(R.drawable.linux);
                break;
            default:
                platformaImage.setImageResource(R.drawable.xst);
                break;
        }
        txtPlatforma.setText(mUser.getOs());
        txtUa.setText(mUser.getUa());

        Date lastDate = new Date(mUser.getAlive() * 1000L);
        if (mUser.isOnline()) {
            if (imain.getState() == Typy.STATE_ONLINE) {
                status.setText("Online");
            } else {
                status.setText("Brak połączenia");
            }
            int[] attr_color_nick = {R.attr.nickColor};
            nick.setTextColor(imain.getThemeColor(attr_color_nick));
            int[] attrs = {R.attr.onlineTextColor};
            status.setTextColor(imain.getThemeColor(attrs));
            avatar.setAlpha(1f);
            platformaImage.setAlpha(1f);
        } else {
            status.setText(ptime.format(lastDate));
            int[] attr_color_nick = {R.attr.offlineNickColor};
            nick.setTextColor(imain.getThemeColor(attr_color_nick));
            int[] attrs = {R.attr.offlineTextColor};
            status.setTextColor(imain.getThemeColor(attrs));
            avatar.setAlpha(0.4f);
            platformaImage.setAlpha(0.4f);
        }

        return row;
    }

    public void odswiezOnline() {
        listaOnline = mAct.getXstDatabase().getListaOnline();
        notifyDataSetChanged();
    }
}

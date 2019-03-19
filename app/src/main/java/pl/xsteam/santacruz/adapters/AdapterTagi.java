package pl.xsteam.santacruz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import pl.xsteam.santacruz.R;

import java.util.ArrayList;

public class AdapterTagi extends BaseAdapter {

    ArrayList<Tag> tagi;
    Context cntx;
    private LayoutInflater inflater;

    public AdapterTagi(Context p_cntx) {
        cntx = p_cntx;
        tagi = new ArrayList<>();
        tagi.add(new Tag("[img] [/img]", "Obrazek"));
        tagi.add(new Tag("[code] [/code]", "Kod"));
        tagi.add(new Tag("[webm] [/webm]", "Film .webm"));
        tagi.add(new Tag("[pul] [/pul]", "Tekst czerwony"));
        tagi.add(new Tag("[pulgreen] [/pulgreen]", "Tekst zielony"));
        tagi.add(new Tag("[vib] [/vib]", "Efekt wibracji"));
        tagi.add(new Tag("[rot] [/rot]", "Efekt rotacji"));
        tagi.add(new Tag("[u] [/u]", "Podkreślenie"));
        tagi.add(new Tag("[i] [/i]", "Kursywa"));
        tagi.add(new Tag("[b] [/b]", "Pogrubienie"));
        tagi.add(new Tag("[quote] [/quote]", "Cytat, dodanie autora: [/quote=autor]"));
        tagi.add(new Tag("[wall] [/wall]", "Ogłoszenie, dodanie tytułu: [wall=tytuł]"));
    }

    @Override
    public int getCount() {
        return tagi.size();
    }

    @Override
    public Object getItem(int position) {
        return tagi.get(position).tag;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) cntx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        View row;
        try {
            if (inflater != null) {
                row = inflater.inflate(R.layout.tag_layout, parent, false);
            } else return null;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }

        TextView tag = row.findViewById(R.id.textTag);
        TextView opis = row.findViewById(R.id.textOpis);

        tag.setText(tagi.get(position).tag);
        opis.setText(tagi.get(position).opis);

        return row;
    }

    private class Tag {
        public String tag;
        public String opis;

        public Tag(String p_tag, String p_opis) {
            tag = p_tag;
            opis = p_opis;
        }
    }
}

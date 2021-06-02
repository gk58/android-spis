package pl.kosim.spis.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.kosim.spis.MainActivity;
import pl.kosim.spis.R;
import pl.kosim.spis.dao.SpisManager;

public class ListaSpisowAdapter extends RecyclerView.Adapter<ListaSpisowAdapter.SpisHolder> {

    private static final String TAG = "ListaSpisowAdapter";

    private MainActivity mainActivity;

    public static class SpisHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView spisData;
        public int spisId = 0;
        private MainActivity mainActivity;

        public SpisHolder( MainActivity mainActivity, View v )
        {
            super(v);
            this.mainActivity = mainActivity;
            spisData = v.findViewById(R.id.spis_data);
            Button button = v.findViewById(R.id.btn_otworz_spis);
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mainActivity.openSpis(spisId);
        }
    }

    public ListaSpisowAdapter(MainActivity mainActivity ) {
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public SpisHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //TextView v = new TextView(parent.getContext());
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_spisow_item, parent, false);
        SpisHolder vh = new SpisHolder(mainActivity,v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SpisHolder holder, int position) {
        holder.spisId = position;
        holder.spisData.setText( SpisManager.get().podajNazweSpisu(position) );
    }

    @Override
    public int getItemCount() {
        return SpisManager.get().podajLiczbe();
    }
}

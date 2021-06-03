package pl.kosim.spis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.kosim.spis.adapter.ListaSpisowAdapter;
import pl.kosim.spis.dao.SpisManager;
import pl.kosim.spis.interfejsy.Oczekujacy;

public class MainActivity extends AppCompatActivity implements Oczekujacy {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.lista_spisow);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ListaSpisowAdapter(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_nowy_spis:
                nowySpis();
                return false;
            case R.id.action_pobierz_spis:
                pobierzSpis();
                return false;
            case R.id.action_ustawienia:
                ustawienia();
                return false;
            case R.id.action_odswiez:
                mAdapter.notifyDataSetChanged();
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    public void openSpis(int poz ) {
        Log.d(TAG, "openSpis: "+poz);
        Intent intent = new Intent(this, SpisActivity.class);
        SpisManager.get().ustawSpis(poz);
        startActivity(intent);
    }

    private void nowySpis() {
        Log.d(TAG, "nowySpis: ");
        Intent intent = new Intent(this, NowySpisActivity.class);
        startActivity(intent);
    }

    private void pobierzSpis() {
        Log.d(TAG, "pobierzSpis: ");
        final Oczekujacy ja = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy mam pobrać spis.json?");//.setTitle("R.string.dialog_title");
        builder.setPositiveButton("tak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                SpisManager.get().pobierzPlik( ja );
            }
        });
        builder.setNegativeButton("nie", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void ustawienia() {
        Log.d(TAG, "ustawienia: ");
        Intent intent = new Intent(this, UstawieniaActivity.class);
        startActivity(intent);
    }

    private void test() {
        Log.d(TAG, "test: ");
    }


    @Override
    public void koniecPracy(String komunikat) {
        Toast.makeText(getApplicationContext(),komunikat,Toast.LENGTH_LONG).show();
        mAdapter.notifyDataSetChanged();
    }
}

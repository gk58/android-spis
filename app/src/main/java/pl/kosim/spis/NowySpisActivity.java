package pl.kosim.spis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pl.kosim.spis.dao.SpisManager;

public class NowySpisActivity extends AppCompatActivity {

    private static final String TAG = "NowySpisActivity";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Date data = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowy_spis);

        Toolbar toolbar = findViewById(R.id.toolbar_nowy_spis);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        final CalendarView cv = findViewById(R.id.calendarView_nowy_spis);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                data = c.getTime();
            }
        });

        Button button = findViewById(R.id.btn_nowy_spis);
        button.setOnClickListener( new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                utworzSpis();
            }
        });
    }

    private void utworzSpis() {
        CalendarView calendarView = findViewById(R.id.calendarView_nowy_spis);
        String selectedDate = sdf.format(data);
        Log.d(TAG, "utworzSpis: wybrano "+selectedDate);
        String komunikat = SpisManager.get().utworzSpis(selectedDate);
        if( komunikat.length()==0 ) {
            Intent intent = new Intent(this, SpisActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),komunikat,Toast.LENGTH_LONG);
            toast.show();
        }
    }


}

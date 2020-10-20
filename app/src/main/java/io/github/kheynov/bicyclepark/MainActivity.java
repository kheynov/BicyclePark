package io.github.kheynov.bicyclepark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.smart_bike_park.R;

import static io.github.kheynov.bicyclepark.Util.Constants.APP_PREFERENCES;
import static io.github.kheynov.bicyclepark.Util.Constants.APP_PREFERENCES_IS_CLOSED_BEFORE;
import static io.github.kheynov.bicyclepark.Util.Constants.infoTag;

public class MainActivity extends AppCompatActivity {
    TextView status_text;
    Button openParkButton;
    SharedPreferences preferences;
    FragmentManager fragmentManager;
    boolean isClosed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        fragmentManager = getSupportFragmentManager();
        status_text = findViewById(R.id.status);
        openParkButton = findViewById(R.id.buttonOpenClose);
        preferences = getPreferences(MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClosed = preferences.getBoolean(APP_PREFERENCES_IS_CLOSED_BEFORE, false);
        Log.i(infoTag, "IS CLOSED : " + isClosed);
        if (isClosed) {
            status_text.setText("Отсканируйте код на парковке, чтобы открыть");
            openParkButton.setText("Открыть парковку");
        } else {
            status_text.setText("Нажмите, чтобы начать пользоваться");
            openParkButton.setText("Закрыть парковку");
        }
    }



    public void scanQR(View view) {
        Intent intent = new Intent(MainActivity.this, ParkHandler.class);
        startActivity(intent);
    }

    public void showOnMap(View view) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }
}

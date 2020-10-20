package io.github.kheynov.bicyclepark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.smart_bike_park.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import io.github.kheynov.bicyclepark.Util.Constants;
import io.github.kheynov.bicyclepark.Util.Encryption;

import static io.github.kheynov.bicyclepark.Util.Constants.APP_PREFERENCES;
import static io.github.kheynov.bicyclepark.Util.Constants.APP_PREFERENCES_IS_CLOSED_BEFORE;
import static io.github.kheynov.bicyclepark.Util.Constants.APP_PREFERENCES_LAST_CLOSED_PARK_ID;
import static io.github.kheynov.bicyclepark.Util.Constants.APP_PREFERENCES_PASSWORD;
import static io.github.kheynov.bicyclepark.Util.Constants.PREFERENCES_DATA_FROM_QR;
import static io.github.kheynov.bicyclepark.Util.Constants.infoTag;

public class ParkHandler extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    NetworkManager connection;
    ArrayList<ParkPlaceListItem> listItems = new ArrayList<>();
    ListView listView;
    TextView textViewAlreadyClosed;
    TextView textViewTimePassed;
    TextView buttonOpenPark;
    FloatingActionButton buttonExit;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_handler);

        listView = findViewById(R.id.listView);
        textViewAlreadyClosed = findViewById(R.id.textViewAlredyClosed);
        textViewTimePassed = findViewById(R.id.textViewTimePassed);

        connection = new NetworkManager(Constants.park_host, Constants.park_port);

        buttonOpenPark = findViewById(R.id.buttonOpen);
        buttonExit = findViewById(R.id.buttonExit);

        sharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(APP_PREFERENCES_IS_CLOSED_BEFORE, false)) {
            listView.setVisibility(View.INVISIBLE);
            listView.setClickable(false);
            textViewAlreadyClosed.setVisibility(View.VISIBLE);
            textViewAlreadyClosed.setText(textViewAlreadyClosed.getText().toString() + (sharedPreferences.getInt(APP_PREFERENCES_LAST_CLOSED_PARK_ID, 1)+1));
            textViewTimePassed.setVisibility(View.INVISIBLE);
            buttonOpenPark.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            listView.setClickable(true);
            textViewAlreadyClosed.setVisibility(View.INVISIBLE);
            textViewTimePassed.setVisibility(View.INVISIBLE);
            buttonOpenPark.setVisibility(View.INVISIBLE);
        }

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(infoTag, "EXIT");
                finish();
            }
        });

        buttonOpenPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openPark(sharedPreferences.getInt(APP_PREFERENCES_LAST_CLOSED_PARK_ID, 0), loadPassword());
                AlertDialog.Builder builder = new AlertDialog.Builder(ParkHandler.this);
                builder.setTitle("Подтвердите открытие парковки")
                        .setMessage("Нажмите \"ОК\" чтобы открыть парковку")
                        .setCancelable(false)
                        .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                connection.openPark(sharedPreferences.getInt(APP_PREFERENCES_LAST_CLOSED_PARK_ID, 0), loadPassword());
                                resetPassword();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
            }
        });

        CreateConnection createConnection = new CreateConnection();
        createConnection.execute(sharedPreferences.getString(PREFERENCES_DATA_FROM_QR, ""));
    }


    void savePassword(String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_PREFERENCES_PASSWORD, password);
        editor.apply();
    }

    String loadPassword() {
        return sharedPreferences.getString("PASSWORD", "");
    }

    void resetPassword() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(APP_PREFERENCES_PASSWORD);
        editor.putBoolean(APP_PREFERENCES_IS_CLOSED_BEFORE, false);
        editor.apply();
    }

    void saveParkClosedId(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(APP_PREFERENCES_IS_CLOSED_BEFORE, true);
        editor.putInt(APP_PREFERENCES_LAST_CLOSED_PARK_ID, id);
        editor.apply();
    }

    @SuppressLint("StaticFieldLeak")
    class CreateConnection extends AsyncTask<String, Integer, ServerResponse> {
        WifiConfiguration wifiConfiguration;
        WifiManager wifiManager;
        WifiInfo wifiInfo;
        LoadingFragment fragment;
        FragmentManager fragmentManager;


        private int checkConnection(String SSID) {
            ConnectivityManager cm = (ConnectivityManager) Objects.requireNonNull(getSystemService(Context.CONNECTIVITY_SERVICE));
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            wifiInfo = wifiManager.getConnectionInfo();
            if (netInfo != null && netInfo.isConnected() && wifiInfo.getSSID().equals(SSID)) {
                return 0;
            } else if (netInfo != null && netInfo.isConnected() && !wifiInfo.getSSID().equals(SSID)) {
                return 1;
            } else {
                return 2;
            }
        }

        private void connectToAP(String SSID) {
            wifiManager = (WifiManager) Objects.requireNonNull(getApplicationContext().getSystemService(Context.WIFI_SERVICE));
            wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = String.format("\"%s\"", SSID);//название сети
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);//без пароля
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            int res = wifiManager.addNetwork(wifiConfiguration);
            wifiManager.disconnect();
            wifiManager.enableNetwork(res, true);
            wifiManager.reconnect();

            Log.i(infoTag, "Connected to AP");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            fragment = new LoadingFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.park_handler_activity, fragment)
                    .commit();

            listItems.clear();

        }

        @Override
        protected ServerResponse doInBackground(String... strings) {
            int status = 0;
            /*connectToAP(strings[0]);
            while (true) {
                if (checkConnection(strings[0]) == 0) {
                    break;
                }
            }*/
            publishProgress(++status);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(++status);
            ServerResponse response = connection.getParksStatus();
            return response;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]) {
                case 0:
                    fragment.updateText("Подключаемся к парковке");
                    break;
                case 1:
                    fragment.updateText("Устанавливаем соединение");
                    break;
                case 2:
                    fragment.updateText("Обновляем данные");
                    break;
                default:
                    fragment.updateText("Не удалось подключиться к парковке");
                    break;
            }
        }

        @Override
        protected void onPostExecute(ServerResponse response) {
            super.onPostExecute(response);
            while (!connection.isResponseUpdated()) ;
            connection.clearResponseUpdated();
            listItems.clear();
            for (int i = 1; i <= response.getParks_count(); i++) {
                if (response.getParksState().get(i - 1).equals("OPENED")) {
                    listItems.add(new ParkPlaceListItem(i, ParkPlaceListItem.parkPlaceStates.OPENED));
                } else {
                    listItems.add(new ParkPlaceListItem(i, ParkPlaceListItem.parkPlaceStates.CLOSED));
                }
            }
            Log.i(infoTag, listItems.toString());
            ParkListAdapter listAdapter = new ParkListAdapter(getApplicationContext(), listItems);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    final ParkPlaceListItem item = listItems.get(position);
                    if (item.getStatus() == ParkPlaceListItem.parkPlaceStates.OPENED) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(ParkHandler.this);
                        builder.setTitle("Вы хотите закрыть парковочное место?")
                                .setMessage("Установите велосипед на парковку и нажмите \"ОК\" чтобы закрыть замок")
                                .setCancelable(false)
                                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String password = Encryption.generatePassword();
                                        savePassword(password);
                                        connection.closePark(item.getId()-1, Encryption.stringToHash(loadPassword()));
                                        saveParkClosedId(item.getId()-1);
                                        Toast.makeText(getApplicationContext(), "Успешно", Toast.LENGTH_SHORT).show();
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {
                        Snackbar.make(view, "Эта парковка уже занята", Snackbar.LENGTH_SHORT).show();
                    }
                    Log.i(infoTag, "ITEM CLICKED: " + (item.getId()-1) + ", " + item.getStatus().toString());
                }
            });
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }
}

package io.github.kheynov.bicyclepark;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_bike_park.R;

import java.util.Objects;

import io.github.kheynov.bicyclepark.Util.Constants;

public class PINActivity extends AppCompatActivity {
    private String passcode = "";
    TextView textView;
    private TextView textViewPassInfo;
    SharedPreferences preferences;
    Intent intent;
    boolean isChangingPIN_status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        preferences = getPreferences(MODE_PRIVATE);
        intent = getIntent();
        isChangingPIN_status = !preferences.getBoolean(Constants.APP_PREFERENCES_IS_PIN_EXIST, false);//Если ПИН-код не существует, создаем его

        textViewPassInfo = findViewById(R.id.textView);
        textView = findViewById(R.id.textViewPassEnter);
        if (isChangingPIN_status) {
            textViewPassInfo.setText("Задайте новый пароль");
        } else {
            textViewPassInfo.setText("Введите пароль");
        }
    }

    @SuppressLint("SetTextI18n")
    public void button1(View view) {
        passcode += "1";
        if (isChangingPIN_status) {
            textView.setText(textView.getText() + String.valueOf(passcode.charAt(passcode.length() - 1)));
        } else {
            textView.setText(textView.getText().toString() + "*");
        }
        update();
    }

    @SuppressLint("SetTextI18n")
    public void button2(View view) {
        passcode += "2";
        if (isChangingPIN_status) {

            textView.setText(textView.getText() + String.valueOf(passcode.charAt(passcode.length() - 1)));
        } else {
            textView.setText(textView.getText().toString() + "*");
        }
        update();
    }

    @SuppressLint("SetTextI18n")
    public void button3(View view) {
        passcode += "3";
        if (isChangingPIN_status) {
            textView.setText(textView.getText() + String.valueOf(passcode.charAt(passcode.length() - 1)));
        } else {
            textView.setText(textView.getText().toString() + "*");
        }
        update();
    }

    @SuppressLint("SetTextI18n")
    public void button4(View view) {
        passcode += "4";
        if (isChangingPIN_status) {
            textView.setText(textView.getText() + String.valueOf(passcode.charAt(passcode.length() - 1)));
        } else {
            textView.setText(textView.getText().toString() + "*");
        }
        update();
    }

    @SuppressLint("SetTextI18n")
    public void button5(View view) {
        passcode += "5";
        if (isChangingPIN_status) {
            textView.setText(textView.getText() + String.valueOf(passcode.charAt(passcode.length() - 1)));
        } else {
            textView.setText(textView.getText().toString() + "*");
        }
        update();
    }

    @SuppressLint("SetTextI18n")
    public void button6(View view) {
        passcode += "6";
        if (isChangingPIN_status) {
            textView.setText(textView.getText() + String.valueOf(passcode.charAt(passcode.length() - 1)));
        } else {
            textView.setText(textView.getText().toString() + "*");
        }
        update();
    }

    @SuppressLint("SetTextI18n")
    public void button7(View view) {
        passcode += "7";
        if (isChangingPIN_status) {
            textView.setText(textView.getText() + String.valueOf(passcode.charAt(passcode.length() - 1)));
        } else {
            textView.setText(textView.getText().toString() + "*");
        }
        update();
    }

    @SuppressLint("SetTextI18n")
    public void button8(View view) {
        passcode += "8";
        if (isChangingPIN_status) {
            textView.setText(textView.getText() + String.valueOf(passcode.charAt(passcode.length() - 1)));
        } else {
            textView.setText(textView.getText().toString() + "*");
        }
        update();
    }

    @SuppressLint("SetTextI18n")
    public void button9(View view) {
        passcode += "9";
        if (isChangingPIN_status) {
            textView.setText(textView.getText() + String.valueOf(passcode.charAt(passcode.length() - 1)));
        } else {
            textView.setText(textView.getText().toString() + "*");
        }
        update();
    }

    @SuppressLint("SetTextI18n")
    public void button0(View view) {
        passcode += "0";
        if (isChangingPIN_status) {
            textView.setText(textView.getText() + String.valueOf(passcode.charAt(passcode.length() - 1)));
        } else {
            textView.setText(textView.getText().toString() + "*");
        }
        update();
    }

    public void buttonBackSpace(View view) {
        if (passcode.length() != 0 && !textView.getText().toString().equals("")) {
            passcode = "";
            textView.setText("");
        }

    }

    private void update() {
        if (passcode.length() == 5) {
            if (isChangingPIN_status) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Constants.PREFERENCES_MASTER_KEY, passcode);
                editor.putBoolean(Constants.APP_PREFERENCES_IS_PIN_EXIST, true);
                editor.apply();
//                Intent intent1 = new Intent(PINActivity.this, ParkHandler.class);
//                startActivity(intent1);
                finish();
            } else {
                Log.i("INFO", passcode);
                Log.i("INFO", preferences.getString(Constants.PREFERENCES_MASTER_KEY, ""));
                if (Objects.requireNonNull(preferences.getString(Constants.PREFERENCES_MASTER_KEY, "")).equals(passcode)) {
                    Toast.makeText(this, "Доступ разрешён", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(PINActivity.this, ParkHandler.class);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(Constants.APP_PREFERENCES_IS_PIN_EXIST, false);
                    editor.apply();
                    passcode = "";
                    textView.setText("");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Неверный ПИН-код", Toast.LENGTH_SHORT).show();
                    passcode = "";
                    textView.setText("");
                }
            }
        }
    }
}

package io.github.kheynov.bicyclepark;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.smart_bike_park.R;

public class LoadingFragment extends Fragment {

    private TextView textView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        textView = view.findViewById(R.id.textViewIsUpdating);
        textView.setText("Подключаемся к парковке");
        return view;
    }
    void updateText(String text) {
        textView.setText(text);
    }
}

package com.example.lavugio_mobile.ui.profile.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lavugio_mobile.R;

public class InfoRowView extends LinearLayout {
    private TextView labelTextView;
    private TextView valueTextView;

    public InfoRowView(Context context) {
        super(context);
        init(context);
    }

    public InfoRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InfoRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.profile_info_row, this, true);

        // Find views
        labelTextView = findViewById(R.id.label);
        valueTextView = findViewById(R.id.value);
    }

    public void setLabel(String label) {
        labelTextView.setText(label);
    }

    public void setValue(String value) {
        valueTextView.setText(value);
        adjustTextSize(value);
    }

    private void adjustTextSize(String text) {
        int length = text.length();

        if (length <= 30) {
            valueTextView.setTextSize(20); // Normal size
        } else {
            valueTextView.setTextSize(16); // Smaller size
        }
    }

    public void setData(String label, String value) {
        setLabel(label);
        setValue(value);
    }

    public String getLabel() {
        return labelTextView.getText().toString();
    }

    public String getValue() {
        return valueTextView.getText().toString();
    }
}

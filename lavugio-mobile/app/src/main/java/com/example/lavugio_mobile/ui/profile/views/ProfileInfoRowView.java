package com.example.lavugio_mobile.ui.profile.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lavugio_mobile.R;

public class ProfileInfoRowView extends LinearLayout {
    private TextView labelTextView;
    private TextView valueTextView;
    private EditText valueEditText;
    private boolean isEditMode = false;
    private boolean isEditable = true;

    public ProfileInfoRowView(Context context) {
        super(context);
        init(context);
    }

    public ProfileInfoRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProfileInfoRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.profile_info_row, this, true);

        // Find views
        labelTextView = findViewById(R.id.label);
        valueTextView = findViewById(R.id.value);
        valueEditText = findViewById(R.id.value_edit);
    }

    public void setLabel(String label) {
        labelTextView.setText(label);
    }

    public void setValue(String value) {
        valueTextView.setText(value);
        valueEditText.setText(value);
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
        if (isEditMode) {
            return valueEditText.getText().toString();
        }
        return valueTextView.getText().toString();
    }

    // Toggle between display and edit mode
    public void setEditMode(boolean editMode) {
        if (!isEditable) {
            editMode = false; // Force display mode if not editable
        }

        this.isEditMode = editMode;

        if (editMode) {
            valueTextView.setVisibility(GONE);
            valueEditText.setVisibility(VISIBLE);
            valueEditText.setText(valueTextView.getText());
        } else {
            valueTextView.setVisibility(VISIBLE);
            valueEditText.setVisibility(GONE);
            // Update display value from edit field
            valueTextView.setText(valueEditText.getText());
        }
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    // Set whether this field can be edited
    public void setEditable(boolean editable) {
        this.isEditable = editable;
        if (!editable) {
            setEditMode(false);
        }
    }

    // Set input type for EditText (email, phone, text, etc.)
    public void setInputType(int inputType) {
        valueEditText.setInputType(inputType);
    }
}

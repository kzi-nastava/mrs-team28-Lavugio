package com.example.lavugio_mobile.ui.profile.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.lavugio_mobile.R;

import java.util.Arrays;
import java.util.List;

public class ProfileInfoRowView extends LinearLayout {
    private TextView labelTextView;
    private TextView valueTextView;
    private EditText valueEditText;
    private Spinner valueSpinner;
    private CheckBox valueCheckbox;
    private boolean isEditMode = false;
    private boolean isEditable = true;
    private boolean isDropdown = false;
    private boolean isCheckbox = false;
    private boolean booleanValue = false;
    private List<String> dropdownOptions;

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
        valueSpinner = findViewById(R.id.value_spinner);
        valueCheckbox = findViewById(R.id.value_checkbox);
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
            if (isDropdown) {
                return valueSpinner.getSelectedItem() != null ? valueSpinner.getSelectedItem().toString() : "";
            }
            if (isCheckbox) {
                return valueCheckbox.isChecked() ? "Yes" : "No";
            }
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
            if (isDropdown) {
                valueEditText.setVisibility(GONE);
                valueCheckbox.setVisibility(GONE);
                valueSpinner.setVisibility(VISIBLE);
                // Select the current value in the spinner
                String currentValue = valueTextView.getText().toString();
                if (dropdownOptions != null) {
                    int position = dropdownOptions.indexOf(currentValue);
                    if (position >= 0) {
                        valueSpinner.setSelection(position);
                    }
                }
            } else if (isCheckbox) {
                valueEditText.setVisibility(GONE);
                valueSpinner.setVisibility(GONE);
                valueCheckbox.setVisibility(VISIBLE);
                valueCheckbox.setChecked(booleanValue);
            } else {
                valueSpinner.setVisibility(GONE);
                valueCheckbox.setVisibility(GONE);
                valueEditText.setVisibility(VISIBLE);
                valueEditText.setText(valueTextView.getText());
            }
        } else {
            valueTextView.setVisibility(VISIBLE);
            valueEditText.setVisibility(GONE);
            valueSpinner.setVisibility(GONE);
            valueCheckbox.setVisibility(GONE);
            // Update display value from edit field, spinner, or checkbox
            if (isDropdown && valueSpinner.getSelectedItem() != null) {
                valueTextView.setText(valueSpinner.getSelectedItem().toString());
            } else if (isCheckbox) {
                booleanValue = valueCheckbox.isChecked();
                valueTextView.setText(booleanValue ? "Yes" : "No");
            } else {
                valueTextView.setText(valueEditText.getText());
            }
        }
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditable(boolean editable) {
        this.isEditable = editable;
        if (!editable) {
            setEditMode(false);
        }
    }

    public void setInputType(int inputType) {
        int typeClass = inputType & android.text.InputType.TYPE_MASK_CLASS;
        if (typeClass == android.text.InputType.TYPE_CLASS_TEXT) {
            valueEditText.setRawInputType(inputType);
        } else {
            valueEditText.setInputType(inputType);
        }
    }

    public void setDropdownOptions(String[] options) {
        this.isDropdown = true;
        this.dropdownOptions = Arrays.asList(options);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                options
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        valueSpinner.setAdapter(adapter);

        // Select the current value if it exists in options
        String currentValue = valueTextView.getText().toString();
        int position = dropdownOptions.indexOf(currentValue);
        if (position >= 0) {
            valueSpinner.setSelection(position);
        }
    }

    public boolean isDropdown() {
        return isDropdown;
    }

    public void setCheckboxMode(boolean initialValue) {
        this.isCheckbox = true;
        this.booleanValue = initialValue;
        valueTextView.setText(initialValue ? "Yes" : "No");
        valueCheckbox.setChecked(initialValue);
    }

    public boolean isCheckbox() {
        return isCheckbox;
    }

    // Get the boolean value (for checkbox mode)
    public boolean getBooleanValue() {
        if (isEditMode && isCheckbox) {
            return valueCheckbox.isChecked();
        }
        return booleanValue;
    }
}

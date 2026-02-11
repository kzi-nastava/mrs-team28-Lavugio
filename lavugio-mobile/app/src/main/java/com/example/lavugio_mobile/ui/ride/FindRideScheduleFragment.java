package com.example.lavugio_mobile.ui.ride;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.example.lavugio_mobile.R;

import java.util.Calendar;

public class FindRideScheduleFragment extends DialogFragment {
    private RadioGroup rgRideType;
    private RadioButton rbRideNow;
    private RadioButton rbScheduleRide;
    private LinearLayout llTimeInput;
    private TextView tvSelectedTime;
    private ImageView ivTimePicker;
    private Button btnCancel;
    private Button btnSchedule;

    public interface OnRideScheduledListener {
        void onRideScheduled(String rideType, String selectedTime);
    }

    private OnRideScheduledListener listener;

    public void setOnRideScheduledListener(OnRideScheduledListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_ride, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();

        // By default ride now is selected
        rbRideNow.setChecked(true);
        disableTimeInput();
    }

    private void initViews(View view) {
        rgRideType = view.findViewById(R.id.rgRideType);
        rbRideNow = view.findViewById(R.id.rbRideNow);
        rbScheduleRide = view.findViewById(R.id.rbScheduleRide);
        llTimeInput = view.findViewById(R.id.llTimeInput);
        tvSelectedTime = view.findViewById(R.id.tvSelectedTime);
        ivTimePicker = view.findViewById(R.id.ivTimePicker);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSchedule = view.findViewById(R.id.btnSchedule);
    }

    private void setupListeners() {
        rgRideType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbRideNow) {
                    disableTimeInput();
                } else {
                    enableTimeInput();
                }
            }
        });

        ivTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llTimeInput.isEnabled()) {
                    showTimePicker();
                }
            }
        });

        llTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llTimeInput.isEnabled()) {
                    showTimePicker();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissFragment();
            }
        });

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rideType = rbRideNow.isChecked() ? "ride_now" : "schedule_ride";
                String selectedTime = tvSelectedTime.getText().toString();

                if (listener != null) {
                    listener.onRideScheduled(rideType, selectedTime);
                }

                dismissFragment();
            }
        });
    }

    private void disableTimeInput() {
        llTimeInput.setEnabled(false);
        llTimeInput.setAlpha(0.5f);
        ivTimePicker.setEnabled(false);
        tvSelectedTime.setTextColor(Color.parseColor("#666666"));
    }

    private void enableTimeInput() {
        llTimeInput.setEnabled(true);
        llTimeInput.setAlpha(1f);
        ivTimePicker.setEnabled(true);
        tvSelectedTime.setTextColor(Color.parseColor("#333333"));
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String time = formatTime(hourOfDay, minuteOfHour);
                    tvSelectedTime.setText(time);
                },
                hour,
                minute,
                false
        );
        timePickerDialog.show();
    }

    private String formatTime(int hourOfDay, int minute) {
        String amPm;
        int hour;

        if (hourOfDay == 0) {
            hour = 12;
            amPm = "AM";
        } else if (hourOfDay == 12) {
            hour = 12;
            amPm = "PM";
        } else if (hourOfDay > 12) {
            hour = hourOfDay - 12;
            amPm = "PM";
        } else {
            hour = hourOfDay;
            amPm = "AM";
        }

        return String.format("%02d:%02d %s", hour, minute, amPm);
    }

    private void dismissFragment() {
        getParentFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }
}

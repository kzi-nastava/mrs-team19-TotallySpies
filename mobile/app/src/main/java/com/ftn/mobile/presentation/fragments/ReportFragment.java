package com.ftn.mobile.presentation.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ftn.mobile.R;
import com.ftn.mobile.data.local.UserRoleManger;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.DailyReportDTO;
import com.ftn.mobile.data.remote.dto.ReportResponseDTO;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportFragment extends Fragment {

    private LineChart moneyChart, countChart, distanceChart;
    private TextView tvTotalCount, tvTotalDistance, tvTotalMoney, tvDailyAverage;;
    private Button btnPickFrom, btnPickTo, btnGenerate;
    private EditText etTargetEmail;

    private Calendar calendarFrom = Calendar.getInstance();
    private Calendar calendarTo = Calendar.getInstance();

    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        moneyChart = view.findViewById(R.id.moneyChart);
        countChart = view.findViewById(R.id.countChart);
        distanceChart = view.findViewById(R.id.distanceChart);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        tvTotalDistance = view.findViewById(R.id.tvTotalDistance);
        tvTotalMoney = view.findViewById(R.id.tvTotalMoney);
        tvDailyAverage = view.findViewById(R.id.tvDailyAverage);
        btnPickFrom = view.findViewById(R.id.btnPickFrom);
        btnPickTo = view.findViewById(R.id.btnPickTo);
        btnGenerate = view.findViewById(R.id.btnGenerate);
        etTargetEmail = view.findViewById(R.id.etTargetEmail);

        calendarFrom.add(Calendar.DAY_OF_MONTH, -30);

        btnPickFrom.setOnClickListener(v -> showDatePicker(true));
        btnPickTo.setOnClickListener(v -> showDatePicker(false));
        btnGenerate.setOnClickListener(v -> fetchReport());

        updateDateButtons();
        UserRoleManger.getRoleLiveData().observe(getViewLifecycleOwner(), role -> {
            boolean isAdmin = "ROLE_ADMIN".equals(role);
            if (isAdmin) {
                etTargetEmail.setVisibility(View.VISIBLE);
            } else {
                etTargetEmail.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void showDatePicker(boolean isFrom) {
        Calendar cal = isFrom ? calendarFrom : calendarTo;
        new DatePickerDialog(getContext(), (view, year, month, day) -> {
            cal.set(year, month, day);
            updateDateButtons();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateButtons() {
        btnPickFrom.setText("From: " + String.format("%d-%02d-%02d", calendarFrom.get(Calendar.YEAR), calendarFrom.get(Calendar.MONTH) + 1, calendarFrom.get(Calendar.DAY_OF_MONTH)));
        btnPickTo.setText("To: " + String.format("%d-%02d-%02d", calendarTo.get(Calendar.YEAR), calendarTo.get(Calendar.MONTH) + 1, calendarTo.get(Calendar.DAY_OF_MONTH)));
    }

    private void fetchReport() {
        String fromIso = String.format("%d-%02d-%02dT00:00:00", calendarFrom.get(Calendar.YEAR), calendarFrom.get(Calendar.MONTH) + 1, calendarFrom.get(Calendar.DAY_OF_MONTH));
        String toIso = String.format("%d-%02d-%02dT23:59:59", calendarTo.get(Calendar.YEAR), calendarTo.get(Calendar.MONTH) + 1, calendarTo.get(Calendar.DAY_OF_MONTH));
        String target = etTargetEmail.getText().toString().trim();

        ApiProvider.user().getReport(fromIso, toIso, target.isEmpty() ? null : target).enqueue(new Callback<ReportResponseDTO>() {
            @Override
            public void onResponse(Call<ReportResponseDTO> call, Response<ReportResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayData(response.body());
                }
            }
            @Override
            public void onFailure(Call<ReportResponseDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(ReportResponseDTO data) {
        tvTotalCount.setText(String.valueOf(data.getTotalCount()));
        tvTotalDistance.setText(String.format("%.1f km", data.getTotalDistance()));
        tvTotalMoney.setText(String.format("%.2f RSD", data.getTotalSum()));
        tvDailyAverage.setText(String.format("%.2f RSD", data.getAverageMoney()));

        setupChart(moneyChart, data.getDailyData(), "money", Color.parseColor("#6E58C6"));
        setupChart(countChart, data.getDailyData(), "count", Color.parseColor("#FF6384"));
        setupChart(distanceChart, data.getDailyData(), "distance", Color.parseColor("#36A2EB"));
    }

    private void setupChart(LineChart chart, List<DailyReportDTO> dailyData, String type, int color) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < dailyData.size(); i++) {
            DailyReportDTO d = dailyData.get(i);
            float val = 0;
            if (type.equals("money")) val = (float) d.getMoney();
            else if (type.equals("count")) val = (float) d.getCount();
            else val = (float) d.getDistance();

            entries.add(new Entry(i, val));
            labels.add(d.getDate());
        }

        LineDataSet dataSet = new LineDataSet(entries, type.toUpperCase());
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(color);
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                }
                return "";
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }
}
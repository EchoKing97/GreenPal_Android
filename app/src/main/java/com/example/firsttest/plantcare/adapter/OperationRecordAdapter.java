package com.example.firsttest.plantcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.firsttest.R;
import com.example.firsttest.plantcare.model.OperationRecord;
import java.text.SimpleDateFormat;
import java.util.List;

public class OperationRecordAdapter extends ArrayAdapter<OperationRecord> {
    private Context context;
    private List<OperationRecord> operationRecords;

    public OperationRecordAdapter(Context context, List<OperationRecord> operationRecords) {
        super(context, 0, operationRecords);
        this.context = context;
        this.operationRecords = operationRecords;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OperationRecord record = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.operation_record_item, parent, false);
        }
        TextView operationTextView = convertView.findViewById(R.id.operation);
        TextView timeTextView = convertView.findViewById(R.id.time);

        operationTextView.setText(record.getOperation());
        timeTextView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.getOperationTime()));
        return convertView;
    }
}
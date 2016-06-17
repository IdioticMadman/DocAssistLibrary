package net.ezbim.sample.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import net.ezbim.docassist.office.MyIntent;
import net.ezbim.sample.Common;
import net.ezbim.sample.R;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class OfficeFragment extends Fragment implements View.OnClickListener {


    private Button btn_open_doc;
    private Button btn_open_excel;
    private Context mContext;

    public OfficeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_office, container, false);
        btn_open_doc = (Button) view.findViewById(R.id.btn_open_doc);
        btn_open_excel = (Button) view.findViewById(R.id.btn_open_excel);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_open_doc.setOnClickListener(this);
        btn_open_excel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_doc:
                Intent wordFileIntent = MyIntent.getWordFileIntent(Common.officePathFolder + File.separator + "abc.docx");
                boolean available = MyIntent.isIntentAvailable(mContext, wordFileIntent);
                if (available) {
                    startActivity(wordFileIntent);
                } else {
                    Toast.makeText(mContext, "请安装打开office的APP", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_open_excel:
                startActivity(MyIntent.getWordFileIntent(Common.officePathFolder + File.separator + "excel.xls"));
                break;
        }
    }


}

package net.ezbim.uhflibrary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.magicrf.uhfreaderlib.reader.Tools;
import com.magicrf.uhfreaderlib.reader.UhfReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robert on 2016/6/23.
 */
public class UHFActivity extends AppCompatActivity implements View.OnClickListener {
    //控制读取的标志
    private boolean runFlag = true;
    private boolean startFlag = true;

    //listView数据
    private ArrayList<EPC> listEPC;
    private int idCount = 1;
    private UhfReader reader; //超高频读写器

    //组件变量
//    private Button btnStartInventory;
//    private Button btnStopInventory;
//    private Button btnClear;
    private ListView lvItem;
    private ScreenStateReceiver screenReceiver;
    private Context mContext;
    private UHFBaseAdapter uhfBaseAdapter;
    private Button btnConfirm;
    private Button btnCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int style = intent.getIntExtra("style", 1);
        if (style == 1) {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_uhf);

        mContext = this;

        initView();
        setListener();
        setTitle("请选择");
        //开启电源
        Util.controlUHFReaderPower(Util.powerOn);
        if (initUHFReader()) return;

        registerScreenRece();

        Thread thread = new InventoryThread();
        thread.start();
        //初始化声音池
        Util.initSoundPool(this);
    }

    @Override
    protected void onPause() {
        startFlag = false;
        super.onPause();
    }

    private void registerScreenRece() {
        //添加广播，默认屏灭时休眠，屏亮时唤醒
        screenReceiver = new ScreenStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);
    }

    private boolean initUHFReader() {
        //设置端口
        String serialPortPath = (String) SPUtils.get(mContext, "portPath", "/dev/ttyS2");
        UhfReader.setPortPath(serialPortPath);
        reader = UhfReader.getInstance();
        //获取读写器设备示例，若返回null，则设备电源打开失败

        if (reader == null) {
            AlertDialog dialog = new AlertDialog.Builder(mContext).create();
            dialog.setMessage("UHFReader serial port init fail");
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
//            setAllButtonFalse();
            return true;
        }

        //获取用户设置功率,并设置
        int value = (int) SPUtils.get(mContext, "value", 26);
        Log.d("", "value" + value);
        reader.setOutputPower(value);
        return false;
    }

    /**
     * 硬件初始化失败，不允许操作
     */
//    private void setAllButtonFalse() {
//        setButtonClickable(btnClear, false);
//        setButtonClickable(btnStartInventory, false);
//        setButtonClickable(btnStopInventory, false);
//    }

    /**
     * 初始化view
     */
    private void initView() {
//        btnStartInventory = (Button) findViewById(R.id.btn_start_inventory);
//        btnStopInventory = (Button) findViewById(R.id.btn_stop_inventory);
//        btnClear = (Button) findViewById(R.id.btn_clear);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        lvItem = (ListView) findViewById(R.id.lv_item);
        listEPC = new ArrayList<>();
    }

    /**
     * 设置监听
     */
    private void setListener() {
//        btnClear.setOnClickListener(this);
//        btnStartInventory.setOnClickListener(this);
//        btnStopInventory.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EPC epc = listEPC.get(position);
                epc.setChecked(!epc.isChecked());
                uhfBaseAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_start_inventory) {
            startFlag = true;
        }
        else if (viewId == R.id.btn_confirm) {
            ArrayList<String> epcStrs = new ArrayList<>();
            for (EPC epc : listEPC) {
                if (epc.isChecked()) {
                    epcStrs.add(epc.getEpc());
                }
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra("epcStrs", epcStrs);
            setResult(25, intent);
            finish();
        } else if (viewId == R.id.btn_cancel) {
            finish();
        }// else if (viewId == R.id.btn_stop_inventory) {
//            startFlag = false;
//        } else if (viewId == R.id.btn_clear) {
//            listEPC.removeAll(listEPC);
//            if (uhfBaseAdapter != null) {
//                uhfBaseAdapter.notifyDataSetChanged();
//            }
//        }
    }

    //设置按钮是否可用
    private void setButtonClickable(Button button, boolean flag) {
        button.setClickable(flag);
        if (flag) {
            button.setTextColor(Color.BLACK);
        } else {
            button.setTextColor(Color.GRAY);
        }
    }


    /**
     * 盘存线程
     */
    class InventoryThread extends Thread {

        private List<byte[]> epcList;

        @Override
        public void run() {
            while (runFlag) {
                if (startFlag) {
//					reader.stopInventoryMulti()
                    epcList = reader.inventoryRealTime(); //实时盘存
                    if (epcList != null && !epcList.isEmpty()) {
                        //播放提示音
                        //Util.play(1, 0);
                        for (byte[] epc : epcList) {
                            if (epc != null) {
                                String epcStr = Tools.Bytes2HexString(epc, epc.length);
                                Log.e("EPC", "标签: " + epcStr);
                                addToList(listEPC, epcStr);
                            }
                        }
                    }
                    epcList = null;
                }
                SystemClock.sleep(200);
            }
        }

    }

    //将读取的EPC添加到listView
    private void addToList(final List<EPC> list, final String epc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //第一次读入数据
                if (list.isEmpty()) {
                    EPC epcTag = new EPC();
                    epcTag.setEpc(epc);
                    epcTag.setCount(1);
                    epcTag.setId(idCount);
                    list.add(epcTag);
                    idCount++;
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        EPC mEPC = list.get(i);
                        //list中有此EPC
                        if (epc.equals(mEPC.getEpc())) {
                            mEPC.setCount(mEPC.getCount() + 1);
                            list.set(i, mEPC);
                            break;
                        } else if (i == (list.size() - 1)) {
                            //list中没有此epc
                            EPC newEPC = new EPC();
                            newEPC.setId(idCount);
                            newEPC.setEpc(epc);
                            newEPC.setCount(1);
                            list.add(newEPC);
                            idCount++;
                        }
                    }
                }

                if (uhfBaseAdapter == null) {
                    uhfBaseAdapter = new UHFBaseAdapter();
                    lvItem.setAdapter(uhfBaseAdapter);
                } else {
                    uhfBaseAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    public class UHFBaseAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return listEPC.size();
        }

        @Override
        public Object getItem(int position) {
            return listEPC.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view;
            if (convertView != null) {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            } else {
                viewHolder = new ViewHolder();
                view = View.inflate(mContext, R.layout.item, null);
                viewHolder.cb = (CheckBox) view.findViewById(R.id.cb);
                viewHolder.tvId = (TextView) view.findViewById(R.id.tv_id);
                viewHolder.tvEPC = (TextView) view.findViewById(R.id.tv_epc);
                //viewHolder.tvCount = (TextView) view.findViewById(R.id.tv_count);
                view.setTag(viewHolder);
            }

            EPC epc = listEPC.get(position);
            viewHolder.cb.setChecked(epc.isChecked());
            viewHolder.tvId.setText(position + "");
            viewHolder.tvEPC.setText(epc.getEpc());
            //viewHolder.tvCount.setText(epc.getCount()+"");
            return view;
        }


    }

    static class ViewHolder {
        public CheckBox cb;
        public TextView tvId;
        public TextView tvEPC;
        public TextView tvCount;
    }

    @Override
    protected void onDestroy() {

        if (screenReceiver != null) {
            unregisterReceiver(screenReceiver);
        }
        runFlag = false;
        if (reader != null) {
            reader.close();
        }

        super.onDestroy();
        //关闭电源
        Util.controlUHFReaderPower(Util.powerOff);
    }
}

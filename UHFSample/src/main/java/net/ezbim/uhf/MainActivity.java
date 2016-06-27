package net.ezbim.uhf;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.magicrf.uhfreaderlib.reader.Tools;
import com.magicrf.uhfreaderlib.reader.UhfReader;

import net.ezbim.uhflibrary.EPC;
import net.ezbim.uhflibrary.SPUtils;
import net.ezbim.uhflibrary.ScreenStateReceiver;
import net.ezbim.uhflibrary.UHFUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //控制读取的标志
    private boolean runFlag = true;
    private boolean startFlag = false;
    private boolean connectFlag = false;

    //listView数据
    private ArrayList<EPC> listEPC;
    private ArrayList<Map<String, Object>> listMap;

    private UhfReader reader; //超高频读写器

    //组件变量
    private Button btnConnect;
    private Button btnStartInventory;
    private Button btnStopInventory;
    private Button btnClear;
    private ListView lvItem;
    private RelativeLayout rl_root;
    private ScreenStateReceiver screenReceiver;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        initView();
        setListener();

        if (initUHFReader()) return;

        registerScreenRece();

        Thread thread = new InventoryThread();
        thread.start();
        //初始化声音池
        UHFUtils.initSoundPool(this);
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
            Snackbar.make(rl_root, "serialport init fail", Snackbar.LENGTH_LONG).show();
            setAllButtonFalse();
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
    private void setAllButtonFalse() {
        setButtonClickable(btnClear, false);
        setButtonClickable(btnStartInventory, false);
        setButtonClickable(btnStopInventory, false);
        setButtonClickable(btnConnect, false);
    }

    /**
     * 初始化view
     */
    private void initView() {
        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnStartInventory = (Button) findViewById(R.id.btn_start_inventory);
        btnStopInventory = (Button) findViewById(R.id.btn_stop_inventory);
        btnClear = (Button) findViewById(R.id.btn_clear);
        lvItem = (ListView) findViewById(R.id.lv_item);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        listEPC = new ArrayList<>();
    }

    /**
     * 设置监听
     */
    private void setListener() {
        btnClear.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnStartInventory.setOnClickListener(this);
        btnStopInventory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:

                byte[] versionBytes = reader.getFirmware();
                if (versionBytes != null) {
                    //播放提示音
                    UHFUtils.play(1, 0);
                    String version = new String(versionBytes);
                    Snackbar.make(rl_root, "连接成功", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(rl_root, "连接失败", Snackbar.LENGTH_SHORT).show();
                }
                setButtonClickable(btnConnect, false);
                setButtonClickable(btnStartInventory, true);
                break;
            case R.id.btn_start_inventory:
                startFlag = true;
                break;
            case R.id.btn_stop_inventory:
                startFlag = false;
                break;
            case R.id.btn_clear:
                listEPC.removeAll(listEPC);
                lvItem.setAdapter(null);
                break;
        }
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
                        //UHFUtils.play(1, 0);
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
            }
        }
    }

    //将读取的EPC添加到LISTVIEW
    private void addToList(final List<EPC> list, final String epc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //第一次读入数据
                if (list.isEmpty()) {
                    EPC epcTag = new EPC();
                    epcTag.setEpc(epc);
                    epcTag.setCount(1);
                    list.add(epcTag);
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
                            newEPC.setEpc(epc);
                            newEPC.setCount(1);
                            list.add(newEPC);
                        }
                    }
                }
                //将数据添加到ListView
                listMap = new ArrayList<Map<String, Object>>();
                int idcount = 1;
                for (EPC epcdata : list) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("ID", idcount);
                    map.put("EPC", epcdata.getEpc());
                    map.put("COUNT", epcdata.getCount());
                    idcount++;
                    listMap.add(map);
                }
                lvItem.setAdapter(new SimpleAdapter(MainActivity.this,
                        listMap, R.layout.item,
                        new String[]{"ID", "EPC", "COUNT"},
                        new int[]{R.id.textView_id, R.id.textView_epc, R.id.textView_count}));
            }
        });
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
    }
}

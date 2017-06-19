package com.oraro.myapplicationuhf;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.oraro.utils.App;
import com.oraro.utils.DataTransfer;
import com.oraro.utils.ExceptionForToast;
import com.senter.support.openapi.StUhf;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private String ui;

   ContinuousInventoryListener	workerLisener=new ContinuousInventoryListener()
    {

        @Override
        public void onTagInventory(StUhf.UII uii, StUhf.InterrogatorModelDs.UmdFrequencyPoint frequencyPoint, Integer antennaId, StUhf.InterrogatorModelDs.UmdRssi rssi)
        {
            Log.e("jw","eeeeee");
            addNewUiiMassageToListview(uii);
        }

        @Override
        public void onFinished()
        {

        }
    };
    private TextView tv;
    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uhfModelChoiced(null);
        //App.appCfgSaveModelClear();
        App.getUhf(App.appCfgSavedModel());
        try {
            App.uhfInit();
        } catch (ExceptionForToast e) {
            e.printStackTrace();
            App.uhfClear();
            App.appCfgSaveModelClear();
        }
        editText = (EditText) findViewById(R.id.edit);
        tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (worker.isInventroing())
                {
                    worker.stopInventory();
                } else
                {
                    worker.startInventory();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
      editText.setText(ui);
    }
    protected void uhfModelChoiced(StUhf.InterrogatorModel interrogatorModel){
        if (interrogatorModel==null) {
            if (App.getUhfWithDetectionAutomaticallyIfNeed()!=null) {
//                if (views.cbRememberChoice.isChecked()) {
//                    App.appCfgSaveModel(App.uhfInterfaceAsModel());
//                }else {
//                }
                    App.appCfgSaveModelClear();
            }else {
//                ah.showToastShort("no uhf module detected");
            }
        }else {
            if (App.getUhf(interrogatorModel)!=null) {
//                if (views.cbRememberChoice.isChecked()) {
//                    App.appCfgSaveModel(interrogatorModel);
//                }else {
//                }
                App.appCfgSaveModelClear();
            }else {
//                ah.showToastShort("no uhf module detected");
            }
        }
    }

    private void addNewUiiMassageToListview(StUhf.UII uii) {
        ui = DataTransfer.xGetString(uii.getBytes());
        handler.post(new Runnable() {
            @Override
            public void run() {
                editText.setText(ui);
            }
        });

    }
    ContinuousInventoryWorker	worker = new ContinuousInventoryWorker(workerLisener);
    private interface ContinuousInventoryListener
    {
        /**
         * will be called on finished completely
         */
        public void onFinished();

        public void onTagInventory(StUhf.UII uii, StUhf.InterrogatorModelDs.UmdFrequencyPoint frequencyPoint, Integer antennaId, StUhf.InterrogatorModelDs.UmdRssi rssi);
    }
    private static class ContinuousInventoryWorker
    {
        /**
         * go on inventoring after one inventory cycle finished.
         */
        private boolean					goOnInventoring	= true;

        private ContinuousInventoryListener	mListener	= null;

        private boolean			isInventoring		= false;

        /**
         *
         * @param listener
         *            must no be null
         */
        public ContinuousInventoryWorker(ContinuousInventoryListener listener) {
            if (listener == null)
            {
                throw new NullPointerException();
            }
            mListener = listener;
        }

        public void startInventory()
        {
            goOnInventoring = true;
            isInventoring=true;

            App.uhfInterfaceAsModelD2().iso18k6cRealTimeInventory(1, new StUhf.InterrogatorModelDs.UmdOnIso18k6cRealTimeInventory()
            {

                @Override
                public void onFinishedWithError(StUhf.InterrogatorModelDs.UmdErrorCode error)
                {
                    onFinishedOnce();
                }

                @Override
                public void onFinishedSuccessfully(	Integer antennaId, int readRate, int totalRead)
                {
                    onFinishedOnce();
                }

                private void onFinishedOnce()
                {
                    if (goOnInventoring)
                    {
                        startInventory();
                    } else
                    {
                        isInventoring=false;
                        mListener.onFinished();
                    }
                }

                @Override
                public void onTagInventory(StUhf.UII uii, StUhf.InterrogatorModelDs.UmdFrequencyPoint frequencyPoint, Integer antennaId, StUhf.InterrogatorModelDs.UmdRssi rssi)
                {
                    mListener.onTagInventory(uii, frequencyPoint, antennaId, rssi);
                }
            });
        }

        public void stopInventory()
        {
            goOnInventoring = false;
        }

        public boolean isInventroing()
        {
            return isInventoring;
        }
    }
}

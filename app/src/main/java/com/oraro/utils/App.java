package com.oraro.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.senter.support.openapi.StUhf;
import com.senter.support.openapi.StUhf.Function;
import com.senter.support.openapi.StUhf.InterrogatorModel;

/**
 * description：
 * com.senter.demo.uhf.modelA
 * com.senter.demo.uhf.modelB
 * com.senter.demo.uhf.modelC
 * com.senter.demo.uhf.modelD2
 * com.senter.demo.uhf.modelE
 * com.senter.demo.uhf.modelF
 * every package represents a module modle.so before reading this project source code,please confirm which model in your pda or pad
 * 
 */
public class App extends Application
{
	public static final String TAG="MainApp";
	private static StUhf uhf;
	
	private static App mSinglton; 
	private static Configuration mAppConfiguration;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mSinglton=this;
	}
	
	public static App AppInstance()
	{
		return mSinglton;
	}

	/**
	 * create a uhf instance with the specified model if need
	 */
	public static StUhf getUhf(InterrogatorModel interrogatorModel){
		if (uhf==null) {
			uhf= StUhf.getUhfInstance(interrogatorModel);
			uhfInterfaceAsModel=interrogatorModel;
		}
		return uhf;
	}
	
	public static StUhf getUhfWithDetectionAutomaticallyIfNeed()
	{
		if (uhf == null)
		{
			StUhf rf = null;
			if (appCfgSavedModel()==null)
			{
				rf = StUhf.getUhfInstance();
			}else {
				rf= StUhf.getUhfInstance(appCfgSavedModel());
			}
			if (rf==null) {
				return null;
			}
			InterrogatorModel model=rf.getInterrogatorModel();
			uhf = rf;
			uhfInterfaceAsModel=model;
			appCfgSaveModel(model);
		}
		return uhf;
	}
	
	public static StUhf uhf()
	{
		return uhf;
	}
	public static void uhfInit() throws ExceptionForToast
	{
		Log.i(TAG, "App.uhfInit()");
		if (uhf==null) {
			throw new ExceptionForToast("no device found ,so can not init it ");
		}
		boolean inited=uhf.init();
		if (inited==false) {
			throw new ExceptionForToast("can not init uhf module,please try again");
		}
	}
	public static void uhfUninit()
	{
		Log.i(TAG, "App.uhfUninit()");
		if (uhf==null) {
			return;
		}
		Log.i(TAG, "App.uhfUninit().uninit");
		uhf.uninit();
	}
	
	public static void uhfClear()
	{
		uhf=null;
		uhfInterfaceAsModel=null;
	}

	private static InterrogatorModel uhfInterfaceAsModel;
	
	public static StUhf.InterrogatorModel uhfInterfaceAsModel(){
		if (uhf==null||uhfInterfaceAsModel==null) {	throw new IllegalStateException();	}
		return uhfInterfaceAsModel;
	}
	public static StUhf.InterrogatorModelA uhfInterfaceAsModelA()
	{
		assetUhfInstanceObtained();
		assert(uhfInterfaceAsModel()== InterrogatorModel.InterrogatorModelA);
		return uhf.getInterrogatorAs(StUhf.InterrogatorModelA.class);
	}
	public static StUhf.InterrogatorModelB uhfInterfaceAsModelB()
	{
		assetUhfInstanceObtained();
		assert(uhfInterfaceAsModel()== InterrogatorModel.InterrogatorModelB);
		return uhf.getInterrogatorAs(StUhf.InterrogatorModelB.class);
	}
	public static StUhf.InterrogatorModelC uhfInterfaceAsModelC()
	{
		assetUhfInstanceObtained();
		assert(uhfInterfaceAsModel()== InterrogatorModel.InterrogatorModelC);
		return uhf.getInterrogatorAs(StUhf.InterrogatorModelC.class);
	}
	public static StUhf.InterrogatorModelDs.InterrogatorModelD2 uhfInterfaceAsModelD2()
	{
		assetUhfInstanceObtained();
		assert(uhfInterfaceAsModel()== InterrogatorModel.InterrogatorModelD2);
		return uhf.getInterrogatorAs(StUhf.InterrogatorModelDs.InterrogatorModelD2.class);
	}
	public static StUhf.InterrogatorModelE uhfInterfaceAsModelE()
	{
		assetUhfInstanceObtained();
		assert(uhfInterfaceAsModel()== InterrogatorModel.InterrogatorModelE);
		return uhf.getInterrogatorAs(StUhf.InterrogatorModelE.class);
	}
	public static StUhf.InterrogatorModelF uhfInterfaceAsModelF()
	{
		assetUhfInstanceObtained();
		assert(uhfInterfaceAsModel()== InterrogatorModel.InterrogatorModelF);
		return uhf.getInterrogatorAs(StUhf.InterrogatorModelF.class);
	}
	private static void assetUhfInstanceObtained(){
		Log.e("jw","uhf=="+uhf+"--uhfInterfaceAsModel=="+uhfInterfaceAsModel);
		if (uhf==null||uhfInterfaceAsModel==null) {	throw new IllegalStateException();	}
	}

	/**
	 * stop the operation excuting by module,three times if need.
	 *
	 */
	public static boolean stop()
	{
		if (uhf != null)
		{
			if (uhf.isFunctionSupported(com.senter.support.openapi.StUhf.Function.StopOperation))
			{
				for (int i = 0; i < 3; i++)
				{
					if (uhf().stopOperation())
					{
						return true;
					}
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * clear both mask settings
	 * 
	 */
	public static void clearMaskSettings()
	{
		if (uhf.isFunctionSupported(Function.DisableMaskSettings))
		{
			uhf.disableMaskSettings();
		}
	}
	
	

	public static final InterrogatorModel appCfgSavedModel()
	{
		String modelName=appConfiguration().getString("modelName", "");
		Log.e("jw","modelName=="+modelName);
		if (modelName.length()!=0)
		{
			try {
				Log.e("jw","1111");
				return InterrogatorModel.valueOf(modelName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.e("jw","2222");
		}
		Log.e("jw","333");
		return null;
	}

	public static final void appCfgSaveModelClear()
	{
		 appConfiguration().setString("modelName", "InterrogatorModelD2");
	}
	public static final void appCfgSaveModel(InterrogatorModel model)
	{
		if (model==null)
		{
			throw new NullPointerException();
		}
		appConfiguration().setString("modelName", model.name());
	}
	private static final Configuration appConfiguration()
	{
		Log.e("jw","mAppConfiguration=="+mAppConfiguration);
		if (mAppConfiguration==null)
		{
			mAppConfiguration=new Configuration(mSinglton, "settings", Context.MODE_PRIVATE);
		}
		return mAppConfiguration;
	}
}












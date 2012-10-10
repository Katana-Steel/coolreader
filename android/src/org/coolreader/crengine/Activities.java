package org.coolreader.crengine;

import org.coolreader.CoolReader;
import org.coolreader.R;
import org.coolreader.crengine.History.BookInfoLoadedCallack;

import android.R.bool;
import android.app.Activity;
import android.content.Intent;

public class Activities {
	
	public static final Logger log = L.create("aa");
	
	private static CoolReader mainActivity;
	private static ReaderActivity readerActivity;
	private static BrowserActivity browserActivity;
	public static int activityCount() {
		int count = (mainActivity != null ? 1 : 0)
				+ (readerActivity != null ? 1 : 0)
				+ (browserActivity != null ? 1 : 0);
		return count;
	}
	private static void onChange(BaseActivity activity) {
		if (activity != null && activityCount() == 1) {
			Services.onFirstActivityCreated(activity);
		} else if (activity == null && activityCount() == 0) {
			Services.onLastActivityDestroyed();
		}
	}
	public static void setMain(CoolReader coolReader) {
		mainActivity = coolReader;
		onChange(coolReader);
	}
	public static CoolReader getMain() {
		return mainActivity;
	}
	public static ReaderActivity getReader() {
		return readerActivity;
	}
	public static BrowserActivity getBrowser() {
		return browserActivity;
	}
	public static void setReader(ReaderActivity reader) {
		readerActivity = reader;
		onChange(reader);
	}
	public static void setBrowser(BrowserActivity browser) {
		browserActivity = browser;
		onChange(browser);
	}

	public static void showReader() {
		log.d("Activities.showReader()");
	}

	public static void loadDocument( FileInfo item )
	{
		loadDocument(item, null);
	}
	
	public static void loadDocument( FileInfo item, Runnable callback )
	{
		log.d("Activities.loadDocument(" + item.pathname + ")");
		loadDocument(item.getPathName(), null);
		// TODO: load document
		//showView(readerView);
		//setContentView(readerView);
		//mReaderView.loadDocument(item, null);
	}
	
	public static String getString(int resourceId) {
		BaseActivity activity = getCurrentActivity();
		if (activity != null) {
			return activity.getString(resourceId);
		} else {
			return "unknown string";
		}
	}
	
	public static BaseActivity getCurrentActivity() {
		if (mainActivity != null)
			return mainActivity;
		if (browserActivity != null)
			return browserActivity;
		if (readerActivity != null)
			return readerActivity;
		return null;
	}
	
	public static void startActivity(Class<?> activityClass, String paramName, String paramValue) {
		BaseActivity activity = getCurrentActivity();
		if (activity != null) {
			Intent intent = new Intent(activity.getApplicationContext(), activityClass);
			if (paramName != null)
				intent.putExtra(paramName, paramValue);
			activity.startActivity(intent);
		}
	}

	public static void showManual() {
		loadDocument("@manual", null);
	}
	
	public static final String OPEN_FILE_PARAM = "FILE_TO_OPEN";
	public static void loadDocument( String item, Runnable callback )
	{
		startActivity(ReaderActivity.class, OPEN_FILE_PARAM, item);
	}
	
	public static void showOpenedBook()
	{
		startActivity(ReaderActivity.class, OPEN_FILE_PARAM, null);
	}
	
	public static void showRootWindow() {
		startActivity(CoolReader.class, null, null);
	}
	
	public static final String OPEN_DIR_PARAM = "DIR_TO_OPEN";
	public static void showBrowser() {
		startActivity(BrowserActivity.class, OPEN_DIR_PARAM, null);
	}
	
	public static void showBrowser(FileInfo dir) {
		startActivity(BrowserActivity.class, OPEN_DIR_PARAM, dir != null ? dir.getPathName() : null);
	}
	
	public static void showBrowser(String dir) {
		startActivity(BrowserActivity.class, OPEN_DIR_PARAM, dir);
	}
	
	public static void showRecentBooks() {
		log.d("Activities.showRecentBooks() is called");
		startActivity(BrowserActivity.class, OPEN_DIR_PARAM, FileInfo.RECENT_DIR_TAG);
	}

	public static void showOnlineCatalogs() {
		log.d("Activities.showOnlineCatalogs() is called");
		startActivity(BrowserActivity.class, OPEN_DIR_PARAM, FileInfo.OPDS_LIST_TAG);
	}

	public static void showDirectory(FileInfo path) {
		log.d("Activities.showDirectory(" + path + ") is called");
		startActivity(BrowserActivity.class, OPEN_DIR_PARAM, path.getPathName());
	}

	public static void showCatalog(FileInfo path) {
		log.d("Activities.showCatalog(" + path + ") is called");
		startActivity(BrowserActivity.class, OPEN_DIR_PARAM, path.getPathName());
	}

	
	public static void directoryUpdated(FileInfo dir) {
		if (mainActivity != null)
			mainActivity.directoryUpdated(dir);
		if (browserActivity != null)
			browserActivity.directoryUpdated(dir);
	}
	
	public static void applyAppSetting( String key, String value )
	{
		if (mainActivity != null)
			mainActivity.applyAppSetting(key, value);
		if (readerActivity != null)
			readerActivity.applyAppSetting(key, value);
		if (browserActivity != null)
			browserActivity.applyAppSetting(key, value);
	}
	
	public static void onSettingsChanged(Properties props) {
		for (Object key : props.keySet()) {
			applyAppSetting((String)key, props.getProperty((String)key));
		}
		if (mainActivity != null)
			mainActivity.onSettingsChanged(props);
		if (readerActivity != null)
			readerActivity.onSettingsChanged(props);
		if (browserActivity != null)
			browserActivity.onSettingsChanged(props);
	}

	public static void showBrowserOptionsDialog() {
		// TODO:
	}
	
	private final static int EXITING_INTERVAL = 2000;
	private static long tsExiting;
	public static boolean exiting(boolean clearFlag) {
		boolean result = System.currentTimeMillis() - tsExiting < EXITING_INTERVAL;
		if (clearFlag)
			tsExiting = 0;
		return result;
	}
	
	public static void finish() {
		log.i("Activities.finish() is called");
		tsExiting = System.currentTimeMillis();
		//startActivity(CoolReader.class, "EXIT", "true");
//		BaseActivity activity = getCurrentActivity();
//		if (activity != null) {
//			Intent intent = new Intent(activity.getApplicationContext(), CoolReader.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.putExtra("EXIT", true);
//			activity.startActivity(intent);
//		}
		if (readerActivity != null)
			readerActivity.finish();
		if (browserActivity != null)
			browserActivity.finish();
		if (mainActivity != null)
			mainActivity.finish();
	}
}

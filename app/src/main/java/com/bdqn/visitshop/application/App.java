package com.bdqn.visitshop.application;

import com.bdqn.visitshop.utils.LogUtils;

import org.litepal.LitePalApplication;



public class App extends LitePalApplication {/*数据库配置 只需要继承LitePalApplication 然后把这个Application应用到当前的工程中就可以了 把第一行改成自己的类名App*/
	
	private App app;
	
	public App initApplication(){
		app = new App();
		return app;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.setISLogCat(true);
	}

}

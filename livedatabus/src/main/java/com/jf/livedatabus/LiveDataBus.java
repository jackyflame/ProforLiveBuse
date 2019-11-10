package com.jf.livedatabus;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class LiveDataBus {

    private final Map<String, MyMutableLiveData<Object>> bus;

    private LiveDataBus(){
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        /***单例对象实例*/
        static final LiveDataBus INSTANCE = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return LiveDataBus.SingletonHolder.INSTANCE;
    }

    public MyMutableLiveData<Object> with(String key){
        return with(key, Object.class,false);
    }

    public MyMutableLiveData<Object> with(String key, boolean withStick){
        return with(key, Object.class,withStick);
    }

    public <T> MyMutableLiveData<T> with(String key, Class<T> clz){
        return with(key,clz,false);
    }

    public <T> MyMutableLiveData<T> with(String key, Class<T> clz, boolean withStick){
        if(!bus.containsKey(key)){
            MyMutableLiveData<Object> liveData = new MyMutableLiveData<>();
            bus.put(key,liveData);
        }
        MyMutableLiveData<Object> liveData = bus.get(key);
        liveData.setWithStick(withStick);
        return (MyMutableLiveData<T>) liveData;
    }

    public class MyMutableLiveData<T> extends MutableLiveData<T> {

        private boolean isWithStick = false;

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super.observe(owner, observer);
            if(!isWithStick){
                hookToCleanStick(observer);
            }
        }

        public void setWithStick(boolean withStick){
            this.isWithStick = withStick;
        }

        private void hookToCleanStick(Observer<? super T> observer){
            try {
                //获取LiveData的mObservers
                Field mObserversField = LiveData.class.getDeclaredField("mObservers");
                mObserversField.setAccessible(true);
                Object mObservers = mObserversField.get(this);
                //获取mObservers中observer对应的MAP.ENTITY
                Method methodGet = mObservers.getClass().getDeclaredMethod("get", Object.class);
                methodGet.setAccessible(true);
                Map.Entry entry = (Map.Entry) methodGet.invoke(mObservers,observer);
                //获取observer对应的observerWrapper
                Object observerWrapper = entry.getValue();
                //获取当前LivedData中mVersion的值
                Field mVersionField = LiveData.class.getDeclaredField("mVersion");
                mVersionField.setAccessible(true);
                Object mVersion = mVersionField.get(this);
                //更新observerWrapper中mLastVersion值为LiveData的mVersion值
                Field mLastVersion = observerWrapper.getClass().getSuperclass().getDeclaredField("mLastVersion");
                mLastVersion.setAccessible(true);
                mLastVersion.set(observerWrapper,mVersion);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

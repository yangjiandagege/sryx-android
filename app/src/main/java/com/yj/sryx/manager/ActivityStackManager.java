package com.yj.sryx.manager;

import android.app.Activity;

import java.util.Stack;

/**
 *
 * Created by eason.yang on 17/1/14.
 */
public class ActivityStackManager {

    private static Stack<Activity> activityStack;
    private static ActivityStackManager      instance;

    private ActivityStackManager() {}

    /**
     * 单一实例
     */
    public static ActivityStackManager getInstance() {
        if (instance == null) {
            instance = new ActivityStackManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack == null) {
            return null;
        }
        return activityStack.lastElement();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack == null) {
            return;
        }
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activityStack != null && activity != null && !activity.isFinishing()) {
            activity.finish();
            activityStack.remove(activity);
        }
    }

    /**
     * 结束指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activityStack != null && activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (activityStack == null) {
            return;
        }
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack == null) {
            return;
        }
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                //finishActivity方法中的activity.isFinishing()方法会导致某些activity无法销毁
                //貌似跳转的时候最后一个activity 是finishing状态，所以没有执行
                //内部实现不是很清楚，但是实测结果如此，使用下面代码则没有问题
                // find by TopJohn
                finishActivity(activityStack.get(i));

                activityStack.get(i).finish();
                //break;
            }
        }
        activityStack.clear();
    }

    /**
     * 获取指定的Activity
     *
     * @author kymjs
     */
    public static Activity getActivity(Class<?> cls) {
        if (activityStack != null)
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            finishAllActivity();
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishOtherActivity(Class<?> cls) {
        if (activityStack == null) {
            return;
        }
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            Activity activity = activityStack.get(i);
            if (null != activity) {
                if (!activity.getClass().equals(cls)) {
                    activity.finish();
                }
            }
        }
        activityStack.clear();
    }
}

package com.yj.sryx.manager;

import android.support.annotation.NonNull;

import java.util.Vector;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by eason.yang on 2017/7/13.
 */

public class RxBus {
    private static RxBus instance;
    private Vector<Subject> subjectList = new Vector<>();

    private RxBus() {
    }

    public static synchronized RxBus getInstance() {
        if (null == instance) {
            instance = new RxBus();
        }
        return instance;
    }

    public synchronized <T> Observable<T> register(@NonNull Object object) {
        Subject<T, T> subject = PublishSubject.create();
        subjectList.add(subject);
        return subject;
    }

    public synchronized void unregister(Object object) {
        subjectList.remove(object);
    }

    public void post(@NonNull Object content) {
        synchronized (this) {
            for (Subject subject : subjectList) {
                if (subject != null) {
                    subject.onNext(content);
                }
            }
        }
    }
}

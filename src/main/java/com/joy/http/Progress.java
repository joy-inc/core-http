package com.joy.http;

/**
 * Created by Daisw on 2017/5/24.
 */

public class Progress<T> {

    private int mProgress;
    private T mT;

    public Progress(int progress, T t) {
        this.mProgress = progress;
        this.mT = t;
    }

    public Progress(T t) {
        this(100, t);
    }

    public Progress(int progress) {
        this(progress, null);
    }

    /**
     * int range: [0, 100]
     *
     * @return progress
     */
    public int getProgress() {
        return mProgress;
    }

    public T getEntity() {
        return mT;
    }

    public boolean isCompleted() {
        return mProgress == 100;
    }

    @Override
    public String toString() {
        return "Progress{" +
                "progress=" + mProgress +
                ", entity=" + mT +
                '}';
    }
}

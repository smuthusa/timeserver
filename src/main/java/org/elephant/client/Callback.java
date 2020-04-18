package org.elephant.client;

public interface Callback<T> {
    void done(T t, Throwable exception);
}

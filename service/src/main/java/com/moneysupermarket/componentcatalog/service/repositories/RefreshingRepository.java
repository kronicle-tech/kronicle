package com.moneysupermarket.componentcatalog.service.repositories;

import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.util.concurrent.Semaphore;

public abstract class RefreshingRepository {

    private final Semaphore refreshSemaphore = new Semaphore(1);

    @PostConstruct
    public void initialize() {
        doInitialize();
        new Thread(() -> {
            try {
                refresh(true);
            } catch (Throwable e) {
                log().error("Error refreshing", e);
            }
        }).start();
    }

    public void refresh() {
        refresh(false);
    }

    private void refresh(boolean firstTime) {
        // Do not refresh if a refresh is already in progress
        if (refreshSemaphore.tryAcquire()) {
            log().info("Starting refresh");
            try {
                doRefresh(firstTime);
                log().info("Finished refresh");
            } catch (Exception e) {
                log().error("Refresh aborted due to exception", e);
            } finally {
                refreshSemaphore.release();
            }
        } else {
            log().info("Skipped refresh because another refresh is already in progress");
        }
    }

    protected abstract void doInitialize();

    protected abstract void doRefresh(boolean firstTime);

    protected abstract Logger log();
}

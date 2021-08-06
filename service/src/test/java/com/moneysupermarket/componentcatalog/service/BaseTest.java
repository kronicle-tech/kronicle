package com.moneysupermarket.componentcatalog.service;

import com.moneysupermarket.componentcatalog.service.testutils.TestFileHelper;

import java.nio.file.Path;

public class BaseTest {

    public Path getResourcesDir(String name) {
        return TestFileHelper.getResourcesDir(name, getClass());
    }
}

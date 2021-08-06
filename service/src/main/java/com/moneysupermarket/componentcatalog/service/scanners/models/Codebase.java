package com.moneysupermarket.componentcatalog.service.scanners.models;

import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithReference;
import com.moneysupermarket.componentcatalog.sdk.models.Repo;
import lombok.Value;

import java.nio.file.Path;

@Value
public class Codebase implements ObjectWithReference {

    Repo repo;
    Path dir;

    @Override
    public String reference() {
        return repo.reference();
    }
}

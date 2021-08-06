package com.moneysupermarket.componentcatalog.service.scanners.zipkin.models;

import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithSourceIndexAndTargetIndex;
import lombok.Value;

@Value
public class SourceIndexAndTargetIndex {

    Integer sourceIndex;
    Integer targetIndex;

    public SourceIndexAndTargetIndex(ObjectWithSourceIndexAndTargetIndex dependency) {
        this.sourceIndex = dependency.getSourceIndex();
        this.targetIndex = dependency.getTargetIndex();
    }
}

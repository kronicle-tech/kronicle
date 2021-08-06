package com.moneysupermarket.componentcatalog.service.scanners;

import com.moneysupermarket.componentcatalog.componentmetadata.models.ComponentMetadata;
import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithReference;
import com.moneysupermarket.componentcatalog.sdk.models.ObjectWithScannerId;
import com.moneysupermarket.componentcatalog.sdk.models.Summary;
import com.moneysupermarket.componentcatalog.service.scanners.models.Output;

import java.util.ArrayList;
import java.util.List;

import static com.moneysupermarket.componentcatalog.common.utils.CaseUtils.toKebabCase;

public abstract class Scanner<I extends ObjectWithReference, O> {

    public String id() {
        return toKebabCase(getClass().getSimpleName()).replaceFirst("-scanner$", "");
    }

    public abstract String description();

    public String notes() {
        return null;
    }

    public void refresh(ComponentMetadata componentMetadata) {
    }

    public abstract Output<O> scan(I input);

    public Summary transformSummary(Summary summary) {
        return summary;
    }

    protected <T extends ObjectWithScannerId> List<T> replaceScannerItemsInList(List<T> list, List<T> newItems) {
        List<T> newList = new ArrayList<>(list);
        newList.removeIf(item -> item.getScannerId().equals(id()));
        newList.addAll(newItems);
        return newList;
    }
}

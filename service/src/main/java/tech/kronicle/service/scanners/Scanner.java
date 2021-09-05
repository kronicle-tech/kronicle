package tech.kronicle.service.scanners;

import tech.kronicle.componentmetadata.models.ComponentMetadata;
import tech.kronicle.sdk.models.ObjectWithReference;
import tech.kronicle.sdk.models.ObjectWithScannerId;
import tech.kronicle.sdk.models.Summary;
import tech.kronicle.service.scanners.models.Output;
import tech.kronicle.common.utils.CaseUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class Scanner<I extends ObjectWithReference, O> {

    public String id() {
        return CaseUtils.toKebabCase(getClass().getSimpleName()).replaceFirst("-scanner$", "");
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

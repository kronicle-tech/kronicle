package tech.kronicle.plugins.aws.xray.models;

import lombok.Value;
import tech.kronicle.plugins.aws.models.Page;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
public class XRayServiceGraphPage implements Page<XRayDependency> {

    List<XRayDependency> items;
    String nextPage;

    public XRayServiceGraphPage(List<XRayDependency> items, String nextPage) {
        this.items = createUnmodifiableList(items);
        this.nextPage = nextPage;
    }
}

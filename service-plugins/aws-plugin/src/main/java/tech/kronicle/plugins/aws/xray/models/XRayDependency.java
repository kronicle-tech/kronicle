package tech.kronicle.plugins.aws.xray.models;

import lombok.Value;

import java.util.List;

import static tech.kronicle.sdk.utils.ListUtils.createUnmodifiableList;

@Value
public class XRayDependency {

    List<String> sourceServiceNames;
    List<String> targetServiceNames;

    public XRayDependency(List<String> sourceServiceNames, List<String> targetServiceNames) {
        this.sourceServiceNames = createUnmodifiableList(sourceServiceNames);
        this.targetServiceNames = createUnmodifiableList(targetServiceNames);
    }
}

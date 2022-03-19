package tech.kronicle.sdk.models.zipkin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

/***
 * Code using this class should migrate to using the tracing data available on Summary.
 */
@Deprecated
@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class ZipkinDependency {

    String parent;
    String child;
    Integer callCount;
    Integer errorCount;
}

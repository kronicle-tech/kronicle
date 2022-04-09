package tech.kronicle.plugins.aws.client;

import tech.kronicle.plugins.aws.models.AwsProfileAndRegion;

public interface ClientFactory<T> {

    T createClient(AwsProfileAndRegion profileAndRegion);
}

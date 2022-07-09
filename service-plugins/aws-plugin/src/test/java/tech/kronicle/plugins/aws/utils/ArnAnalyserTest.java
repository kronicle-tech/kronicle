package tech.kronicle.plugins.aws.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArnAnalyserTest {

    @Test
    public void analyseArnShouldWorkOutResourceTypeForArnResourceIdWithColon() {
        // Given
        String arn = "arn:aws:lambda:us-west-1:123456789012:function:ExampleStack-exampleFunction123ABC-123456ABCDEF";

        // When
        AnalysedArn returnValue = ArnAnalyser.analyseArn(arn);

        // When
        assertThat(returnValue).isEqualTo(AnalysedArn.builder()
                .arn(arn)
                .partition("aws")
                .service("lambda")
                .region("us-west-1")
                .accountId("123456789012")
                .resourceId("ExampleStack-exampleFunction123ABC-123456ABCDEF")
                .derivedResourceType("lambda-function")
                .build());
    }

    @Test
    public void analyseArnShouldWorkOutResourceTypeForArnResourceIdWithForwardSlash() {
        // Given
        String arn = "arn:aws:ec2:us-west-1:123456789012:security-group/sg-12345678901ABCDEF";

        // When
        AnalysedArn returnValue = ArnAnalyser.analyseArn(arn);

        // When
        assertThat(returnValue).isEqualTo(AnalysedArn.builder()
                .arn(arn)
                .partition("aws")
                .service("ec2")
                .region("us-west-1")
                .accountId("123456789012")
                .resourceId("security-group/sg-12345678901ABCDEF")
                .derivedResourceType("ec2-security-group")
                .build());
    }

    @Test
    public void analyseArnShouldWorkOutResourceTypeForAnS3BucketWithTheWordBucketAppendedToTheResourceType() {
        // Given
        String arn = "arn:aws:s3:::example-resource-id-1234567ABCDEF";

        // When
        AnalysedArn returnValue = ArnAnalyser.analyseArn(arn);

        // When
        assertThat(returnValue).isEqualTo(AnalysedArn.builder()
                .arn(arn)
                .partition("aws")
                .service("s3")
                .resourceId("example-resource-id-1234567ABCDEF")
                .derivedResourceType("s3-bucket")
                .build());
    }

    @Test
    public void analyseArnShouldWorkOutResourceTypeForAnApiGatewayRestApi() {
        // Given
        String arn = "arn:aws:apigateway:us-west-1::/restapis/1234abcdef";

        // When
        AnalysedArn returnValue = ArnAnalyser.analyseArn(arn);

        // When
        assertThat(returnValue).isEqualTo(AnalysedArn.builder()
                .arn(arn)
                .partition("aws")
                .service("apigateway")
                .region("us-west-1")
                .resourceId("/restapis/1234abcdef")
                .derivedResourceType("apigateway-restapi")
                .build());
    }

    @Test
    public void analyseArnShouldWorkOutResourceTypeForAnApiGatewayRestApiStage() {
        // Given
        String arn = "arn:aws:apigateway:us-west-1::/restapis/1234abcdef/stages/prod";

        // When
        AnalysedArn returnValue = ArnAnalyser.analyseArn(arn);

        // When
        assertThat(returnValue).isEqualTo(AnalysedArn.builder()
                .arn(arn)
                .partition("aws")
                .service("apigateway")
                .region("us-west-1")
                .resourceId("/restapis/1234abcdef/stages/prod")
                .derivedResourceType("apigateway-restapi-stage")
                .build());
    }

    @Test
    public void analyseArnShouldWorkOutResourceTypeForArnResourceIdWithColonAndForwardSlash() {
        // Given
        String arn = "arn:aws:secretsmanager:us-west-1:123456789012:secret:one/two/three-123ABC";

        // When
        AnalysedArn returnValue = ArnAnalyser.analyseArn(arn);

        // When
        assertThat(returnValue).isEqualTo(AnalysedArn.builder()
                .arn(arn)
                .partition("aws")
                .service("secretsmanager")
                .region("us-west-1")
                .accountId("123456789012")
                .resourceId("one/two/three-123ABC")
                .derivedResourceType("secretsmanager-secret")
                .build());
    }
}

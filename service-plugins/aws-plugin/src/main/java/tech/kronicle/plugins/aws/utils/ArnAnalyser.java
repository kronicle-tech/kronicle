package tech.kronicle.plugins.aws.utils;

import java.util.regex.Pattern;

public class ArnAnalyser {

    private static final Pattern REST_API_PATTERN = Pattern.compile("^/restapis/[^/]+$");
    private static final Pattern REST_API_STAGE_PATTERN = Pattern.compile("^/restapis/[^/]+/stages/[^/]+$");

    public static AnalysedArn analyseArn(String arn) {
        AnalysedArn.AnalysedArnBuilder builder = AnalysedArn.builder()
                .arn(arn);
        String[] parts = arn.split(":", 6);
        AnalysedArn analysedArn = builder.partition(parts[1])
                .service(parts[2])
                .region(replaceEmptyWithNull(parts[3]))
                .accountId(replaceEmptyWithNull(parts[4]))
                .resourceId(parts[5])
                .build();
        String derivedResourceType = getDerivedResourceType(analysedArn);
        if (!derivedResourceType.isEmpty()) {
            derivedResourceType = "-" + derivedResourceType;
        }
        derivedResourceType = "aws-" + analysedArn.getService() + derivedResourceType;
        if (derivedResourceType.equals("aws-s3")) {
            derivedResourceType = "aws-s3-bucket";
        } else if (derivedResourceType.equals("aws-apigateway")) {
            if (matchesPattern(analysedArn.getResourceId(), REST_API_PATTERN)) {
                derivedResourceType = "aws-apigateway-restapi";
            } else if (matchesPattern(analysedArn.getResourceId(), REST_API_STAGE_PATTERN)) {
                derivedResourceType = "aws-apigateway-restapi-stage";
            }
        }
        return analysedArn.withDerivedResourceType(derivedResourceType);
    }

    private static boolean matchesPattern(String value, Pattern pattern) {
        return pattern.matcher(value).matches();
    }

    private static String replaceEmptyWithNull(String value) {
        return value.isEmpty() ? null : value;
    }

    private static String getDerivedResourceType(AnalysedArn analysedArn) {
        if (analysedArn.getResourceId().contains(":")) {
            return getStartBefore(analysedArn.getResourceId(), ':');
        } else if (analysedArn.getResourceId().contains("/")) {
            return getStartBefore(analysedArn.getResourceId(), '/');
        } else {
            return "";
        }
    }

    private static String getStartBefore(String value, int ch) {
        return value.substring(0, value.indexOf(ch));
    }
}

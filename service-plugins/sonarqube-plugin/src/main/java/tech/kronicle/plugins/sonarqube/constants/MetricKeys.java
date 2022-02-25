package tech.kronicle.plugins.sonarqube.constants;

import java.util.List;

public final class MetricKeys {

    public static final String LAST_COMMIT_DATE = "last_commit_date";
    public static final String NEW_DEVELOPMENT_COST = "new_development_cost";
    /**
     * This can be removed once we upgrade to SonarQube 8.1 or higher.
     * See https://jira.sonarsource.com/browse/SONAR-12728 for more information.
     *
     * @param metricKey
     * @return
     */
    public static final List<String> AFFECTED_BY_SONARQUBE_BUG = List.of(NEW_DEVELOPMENT_COST);

    private MetricKeys() {
    }
}

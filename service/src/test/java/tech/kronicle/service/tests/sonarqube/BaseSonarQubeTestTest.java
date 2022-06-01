package tech.kronicle.service.tests.sonarqube;

import tech.kronicle.sdk.models.Component;
import tech.kronicle.sdk.models.SonarQubeProjectsState;
import tech.kronicle.sdk.models.sonarqube.SonarQubeProject;

import java.util.List;

public class BaseSonarQubeTestTest {

    protected Component createComponent(List<SonarQubeProject> sonarQubeProjects) {
        return Component.builder()
                .states(List.of(
                        new SonarQubeProjectsState(
                                "test-plugin-id",
                                sonarQubeProjects
                        )
                ))
                .build();
    }
}

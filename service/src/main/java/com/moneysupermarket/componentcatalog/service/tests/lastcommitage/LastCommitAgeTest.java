package com.moneysupermarket.componentcatalog.service.tests.lastcommitage;

import com.moneysupermarket.componentcatalog.sdk.models.Component;
import com.moneysupermarket.componentcatalog.sdk.models.Priority;
import com.moneysupermarket.componentcatalog.sdk.models.TestResult;
import com.moneysupermarket.componentcatalog.service.spring.stereotypes.Test;
import com.moneysupermarket.componentcatalog.service.tests.ComponentTest;
import com.moneysupermarket.componentcatalog.service.tests.models.TestContext;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import static java.util.Objects.isNull;

@Test
@RequiredArgsConstructor
public class LastCommitAgeTest extends ComponentTest {

    private static final int MAX_MONTHS = 6;

    private final Clock clock;

    @Override
    public String description() {
        return String.format("Checks whether there has been at least 1 code commit to a component's repo is the last %d months", MAX_MONTHS);
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public TestResult test(Component input, TestContext testContext) {
        if (isNull(input.getGitRepo())) {
            return createNotApplicableTestResult();
        }

        long lastCommitAgeInTotalWholeMonths = getLastCommitAge(input).toTotalMonths();
        if (lastCommitAgeInTotalWholeMonths >= MAX_MONTHS) {
            return createFailTestResult(String.format(
                    "There has not been any code commits to the component's repo in the last %d months.  Will the component's build and deployment\n"
                            + "jobs still work next time there is a code commit?  Could there be vulnerabilities in the component that have not been found\n"
                            + "because a build has not been ran?  ",
                    lastCommitAgeInTotalWholeMonths));
        }
        return createPassTestResult(String.format(
                "It has only been %d months since the last code commit to the component's repo.  ",
                lastCommitAgeInTotalWholeMonths));
    }

    private Period getLastCommitAge(Component input) {
        LocalDateTime lastCommitTimestamp = input.getGitRepo().getLastCommitTimestamp();
        return Period.between(lastCommitTimestamp.toLocalDate(), LocalDate.now(clock));
    }
}

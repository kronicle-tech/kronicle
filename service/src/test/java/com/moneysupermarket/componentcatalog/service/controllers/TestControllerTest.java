package com.moneysupermarket.componentcatalog.service.controllers;

import com.moneysupermarket.componentcatalog.sdk.models.GetTestResponse;
import com.moneysupermarket.componentcatalog.sdk.models.GetTestsResponse;
import com.moneysupermarket.componentcatalog.service.services.ComponentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestControllerTest {

    private static final com.moneysupermarket.componentcatalog.sdk.models.Test TEST_1 = com.moneysupermarket.componentcatalog.sdk.models.Test.builder().id("test-test-1").build();
    private static final com.moneysupermarket.componentcatalog.sdk.models.Test TEST_2 = com.moneysupermarket.componentcatalog.sdk.models.Test.builder().id("test-test-2").build();
    private static final List<com.moneysupermarket.componentcatalog.sdk.models.Test> TESTS = List.of(TEST_1, TEST_2);

    @Mock
    private ComponentService mockComponentService;
    private TestController underTest;

    @BeforeEach
    public void beforeEach() {
        underTest = new TestController(mockComponentService);
    }

    @Test
    public void getTestsShouldReturnTests() {
        // Given
        when(mockComponentService.getTests()).thenReturn(TESTS);

        // When
        GetTestsResponse returnValue = underTest.getTests();

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTests()).containsExactlyElementsOf(TESTS);
    }

    @Test
    public void getTestShouldReturnATest() {
        // Given
        when(mockComponentService.getTest(TEST_1.getId())).thenReturn(TEST_1);

        // When
        GetTestResponse returnValue = underTest.getTest(TEST_1.getId());

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTest()).isSameAs(TEST_1);
    }

    @Test
    public void getTestShouldNotReturnATestWhenTestIdIsUnknown() {
        // Given
        String testId = "unknown";
        when(mockComponentService.getTest(testId)).thenReturn(null);

        // When
        GetTestResponse returnValue = underTest.getTest(testId);

        // Then
        assertThat(returnValue).isNotNull();
        assertThat(returnValue.getTest()).isNull();
    }
}

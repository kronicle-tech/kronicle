package tech.kronicle.service.partialresponse.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pressassociation.pr.match.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PartialResponseResponseBodyAdviceTest {

    private PartialResponseResponseBodyAdvice underTest;
    private ObjectMapper objectMapper;
    @Mock
    private PartialResponseApplier partialResponseApplier;
    private ExamplePojo body;
    @Mock
    private ServletServerHttpRequest request;
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private ServletServerHttpResponse response;
    @Mock
    private HttpServletResponse servletResponse;
    @Mock
    private Matcher matcher;

    @BeforeEach
    public void beforeEach() {
        objectMapper = new ObjectMapper();
        body = new ExamplePojo();
        body.setValue("test-value");

        underTest = new PartialResponseResponseBodyAdvice(objectMapper, partialResponseApplier);
    }

    @Test
    public void supportsShouldAlwaysReturnTrue() {
        // When
        boolean returnValue = underTest.supports(null, null);

        // Then
        assertThat(returnValue).isTrue();
    }

    @Test
    public void beforeBodyWriteShouldReturnOriginalBodyWhenThereIsNoMatcherAssignedToTheRequest() {
        // When
        when(request.getServletRequest()).thenReturn(servletRequest);
        when(servletRequest.getAttribute(RequestAttributeNames.MATCHER)).thenReturn(null);
        Object returnValue = underTest.beforeBodyWrite(body, null, null, null, request, null);

        // Then
        assertThat(returnValue).isSameAs(body);
    }

    @Test
    public void beforeBodyWriteShouldReturnOriginalBodyWhenStatusCodeIsNot2xxOr3xx() {
        // Given
        when(request.getServletRequest()).thenReturn(servletRequest);
        when(servletRequest.getAttribute(RequestAttributeNames.MATCHER)).thenReturn(matcher);
        when(response.getServletResponse()).thenReturn(servletResponse);
        when(servletResponse.getStatus()).thenReturn(400);

        // When
        Object returnValue = underTest.beforeBodyWrite(body, null, null, null, request, response);

        // Then
        assertThat(returnValue).isSameAs(body);
    }

    @Test
    public void beforeBodyWriteShouldConvertBodyToJsonTreeAndFilterFields() {
        // Given
        when(request.getServletRequest()).thenReturn(servletRequest);
        when(servletRequest.getAttribute(RequestAttributeNames.MATCHER)).thenReturn(matcher);
        when(response.getServletResponse()).thenReturn(servletResponse);
        when(servletResponse.getStatus()).thenReturn(200);
        ArgumentCaptor<JsonNode> jsonCaptor = ArgumentCaptor.forClass(JsonNode.class);
        doNothing().when(partialResponseApplier).apply(jsonCaptor.capture(), eq(matcher));

        // When
        Object returnValue = underTest.beforeBodyWrite(body, null, null, null, request, response);

        // Then
        JsonNode json = jsonCaptor.getValue();
        assertThat(returnValue).isNotSameAs(body);
        assertThat(returnValue).isSameAs(json);
    }

    private static class ExamplePojo {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

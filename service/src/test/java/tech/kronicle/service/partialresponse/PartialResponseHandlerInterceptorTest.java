package tech.kronicle.service.partialresponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PartialResponseHandlerInterceptorTest {

    private PartialResponseHandlerInterceptor underTest;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HandlerMethod handlerMethod;

    @BeforeEach
    public void beforeEach() {
        underTest = new PartialResponseHandlerInterceptor();
    }

    @Test
    public void shouldIgnorePreHandleWhenHandlerIsNotHandlerMethod() {
        // Given
        Object handler = new Object();

        // When
        boolean returnValue = underTest.preHandle(request, response, handler);

        // Then
        assertThat(returnValue).isTrue();
    }

    @Test
    public void shouldIgnorePreHandleWhenHandlerDoesNotHaveAnnotation() {
        // Given
        when(handlerMethod.hasMethodAnnotation(PartialResponse.class)).thenReturn(false);

        // When
        boolean returnValue = underTest.preHandle(request, response, handlerMethod);

        // Then
        assertThat(returnValue).isTrue();
    }

    @Test
    public void shouldBeSuccessfulWhenThereIsNoFieldsParam() {
        // Given
        when(handlerMethod.hasMethodAnnotation(PartialResponse.class)).thenReturn(true);
        when(request.getParameterValues("fields")).thenReturn(null);

        // When
        boolean returnValue = underTest.preHandle(request, response, handlerMethod);

        // Then
        verify(request).getParameterValues("fields");
        assertThat(returnValue).isTrue();
    }

    @Test
    public void shouldBeSuccessfulWhenFieldsIsAnEmptyArray() {
        // Given
        when(handlerMethod.hasMethodAnnotation(PartialResponse.class)).thenReturn(true);
        when(request.getParameterValues("fields")).thenReturn(new String[] {});

        // When
        boolean returnValue = underTest.preHandle(request, response, handlerMethod);

        // Then
        verify(request).getParameterValues("fields");
        assertThat(returnValue).isTrue();
    }

    @Test
    public void shouldBeSuccessfulWhenThereIsASingleFieldValue() {
        // Given
        when(handlerMethod.hasMethodAnnotation(PartialResponse.class)).thenReturn(true);
        when(request.getParameterValues("fields")).thenReturn(new String[] { "a" });

        // When
        boolean returnValue = underTest.preHandle(request, response, handlerMethod);

        // Then
        verify(request).getParameterValues("fields");
        assertThat(returnValue).isTrue();
    }

    @Test
    public void shouldBeSuccessfulWhenThereIsMultipleFieldValues() {
        // Given
        when(handlerMethod.hasMethodAnnotation(PartialResponse.class)).thenReturn(true);
        when(request.getParameterValues("fields")).thenReturn(new String[] { "a", "b" });

        // When
        boolean returnValue = underTest.preHandle(request, response, handlerMethod);

        // Then
        verify(request).getParameterValues("fields");
        assertThat(returnValue).isTrue();
    }

    @Test
    public void shouldFailValidationWhenFieldsValueIsAnInvalidValue() {
        // Given
        when(handlerMethod.hasMethodAnnotation(PartialResponse.class)).thenReturn(true);
        when(request.getParameterValues("fields")).thenReturn(new String[] { "a/" });

        // When
        Throwable thrown = catchThrowable(() -> underTest.preHandle(request, response, handlerMethod));

        // Then
        assertThat(thrown).isInstanceOf(InvalidFieldsQueryParamException.class);
        assertThat(thrown).hasMessage("400 BAD_REQUEST \"Invalid value \"a/\" for \"fields\" query param\"; nested exception is java.lang.IllegalArgumentException: Was expecting at least one of CharMatcher.and(CharMatcher.whitespace().negate(), CharMatcher.anyOf(\"\\u0028\\u0029\\u002A\\u002C\\u002F\").negate())");
        assertThat(thrown).hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldFailValidationWhenFieldsValuesAreInvalidValues() {
        // Given
        when(handlerMethod.hasMethodAnnotation(PartialResponse.class)).thenReturn(true);
        when(request.getParameterValues("fields")).thenReturn(new String[] { "a/", "b/" });

        // When
        Throwable thrown = catchThrowable(() -> underTest.preHandle(request, response, handlerMethod));

        // Then
        assertThat(thrown).isInstanceOf(InvalidFieldsQueryParamException.class);
        assertThat(thrown).hasMessage("400 BAD_REQUEST \"Invalid value \"a/,b/\" for \"fields\" query param\"; nested exception is java.lang.IllegalArgumentException: Was expecting at least one of CharMatcher.and(CharMatcher.whitespace().negate(), CharMatcher.anyOf(\"\\u0028\\u0029\\u002A\\u002C\\u002F\").negate())");
        assertThat(thrown).hasCauseInstanceOf(IllegalArgumentException.class);
    }
}

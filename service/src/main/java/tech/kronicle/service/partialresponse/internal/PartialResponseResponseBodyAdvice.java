package tech.kronicle.service.partialresponse.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pressassociation.pr.match.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

import static java.util.Objects.isNull;

@ControllerAdvice
public class PartialResponseResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;
    private final PartialResponseApplier partialResponseApplier;

    @Autowired
    public PartialResponseResponseBodyAdvice(ObjectMapper objectMapper, PartialResponseApplier partialResponseApplier) {
        this.objectMapper = objectMapper;
        this.partialResponseApplier = partialResponseApplier;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        Matcher matcher = getMatcher(servletRequest);

        if (isNull(matcher)) {
            return body;
        }

        int statusCode = ((ServletServerHttpResponse) response).getServletResponse().getStatus();

        if (!(statusCode >= 200 && statusCode <= 399)) {
            return body;
        }

        return filterFields(body, matcher);
    }

    private Matcher getMatcher(HttpServletRequest request) {
        return (Matcher) request.getAttribute(RequestAttributeNames.MATCHER);
    }

    private JsonNode filterFields(Object body, Matcher matcher) {
        JsonNode json = objectMapper.valueToTree(body);
        partialResponseApplier.apply(json, matcher);
        return json;
    }
}

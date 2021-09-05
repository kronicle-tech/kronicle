package tech.kronicle.service.partialresponse;

import com.google.common.base.Joiner;
import tech.kronicle.service.partialresponse.internal.QueryParamNames;
import tech.kronicle.service.partialresponse.internal.RequestAttributeNames;
import com.pressassociation.pr.match.Matcher;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class PartialResponseHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isPartialResponseHandler(handler)) {
            Matcher matcher = validateFieldsQueryParameter(request);
            storeMatcher(request, matcher);
        }

        return true;
    }

    private boolean isPartialResponseHandler(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            return (handlerMethod.hasMethodAnnotation(PartialResponse.class));
        } else {
            return false;
        }
    }

    private Matcher validateFieldsQueryParameter(HttpServletRequest request) {
        String[] fields = request.getParameterValues(QueryParamNames.FIELDS);
        if (fields == null || fields.length == 0) {
            return null;
        }

        String pattern = Joiner.on(",").join(fields);
        try {
            return Matcher.of(pattern);
        } catch (IllegalArgumentException e) {
            throw new InvalidFieldsQueryParamException(String.format("Invalid value \"%s\" for \"fields\" query param", pattern), e);
        }
    }

    private void storeMatcher(HttpServletRequest request, Matcher matcher) {
        request.setAttribute(RequestAttributeNames.MATCHER, matcher);
    }
}

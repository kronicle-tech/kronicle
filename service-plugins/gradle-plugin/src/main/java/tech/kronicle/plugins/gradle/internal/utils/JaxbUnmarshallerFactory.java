package tech.kronicle.plugins.gradle.internal.utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.SneakyThrows;

public final class JaxbUnmarshallerFactory {

    @SneakyThrows
    public static Unmarshaller createJaxbUnmarshaller(Class<?> type) {
        return JAXBContext.newInstance(type).createUnmarshaller();
    }

    private JaxbUnmarshallerFactory() {
    }
}

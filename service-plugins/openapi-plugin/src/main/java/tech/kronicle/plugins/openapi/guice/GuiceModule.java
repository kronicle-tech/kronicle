package tech.kronicle.plugins.openapi.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.utils.FileUtils;
import tech.kronicle.utils.ThrowableToScannerErrorMapper;

import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;
import static tech.kronicle.utils.JsonMapperFactory.createJsonMapper;

public class GuiceModule extends AbstractModule {

    @Provides
    public FileUtils fileUtils() {
        return createFileUtils();
    }

    @Provides
    public ObjectMapper objectMapper() {
        return createJsonMapper();
    }

    @Provides
    public ThrowableToScannerErrorMapper throwableToScannerErrorMapper() {
        return new ThrowableToScannerErrorMapper();
    }
}

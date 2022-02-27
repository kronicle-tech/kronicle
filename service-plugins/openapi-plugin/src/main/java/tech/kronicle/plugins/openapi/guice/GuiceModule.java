package tech.kronicle.plugins.openapi.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.pluginutils.FileUtils;
import tech.kronicle.pluginutils.ThrowableToScannerErrorMapper;

import static tech.kronicle.pluginutils.FileUtilsFactory.createFileUtils;
import static tech.kronicle.pluginutils.JsonMapperFactory.createJsonMapper;

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

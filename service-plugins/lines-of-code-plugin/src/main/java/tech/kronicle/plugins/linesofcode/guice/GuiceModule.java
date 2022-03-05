package tech.kronicle.plugins.linesofcode.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.utils.FileUtils;

import static tech.kronicle.utils.FileUtilsFactory.createFileUtils;

public class GuiceModule extends AbstractModule {

    @Provides
    public FileUtils fileUtils() {
        return createFileUtils();
    }
}

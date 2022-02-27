package tech.kronicle.plugins.javaimport.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import tech.kronicle.pluginutils.FileUtils;

import static tech.kronicle.pluginutils.FileUtilsFactory.createFileUtils;

public class GuiceModule extends AbstractModule {

    @Provides
    public FileUtils fileUtils() {
        return createFileUtils();
    }
}

package tech.kronicle.pluginutils;

public final class FileUtilsFactory {
    
    public static FileUtils createFileUtils() {
        return new FileUtils(new AntStyleIgnoreFileLoader());
    }

    private FileUtilsFactory() {
    }
}

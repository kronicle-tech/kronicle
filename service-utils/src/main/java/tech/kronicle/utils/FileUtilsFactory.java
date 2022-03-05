package tech.kronicle.utils;

public final class FileUtilsFactory {
    
    public static FileUtils createFileUtils() {
        return new FileUtils(new AntStyleIgnoreFileLoader());
    }

    private FileUtilsFactory() {
    }
}

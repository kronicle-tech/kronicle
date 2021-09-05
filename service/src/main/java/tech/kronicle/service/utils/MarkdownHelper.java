package tech.kronicle.service.utils;

public final class MarkdownHelper {

    public static String createMarkdownLink(String text, String url) {
        return String.format("[%s](%s)", text, url);
    }

    private MarkdownHelper() {
    }
}

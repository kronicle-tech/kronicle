package tech.kronicle.plugins.kubernetes.constants;

import java.util.List;

public final class LabelKeys {

    public static final String APP_KUBERNETES_IO_NAME = "app.kubernetes.io/name";
    public static final String APP_KUBERNETES_IO_PART_OF = "app.kubernetes.io/part-of";
    public static final List<String> SUPPORTED_KEYS = List.of(
            APP_KUBERNETES_IO_NAME,
            APP_KUBERNETES_IO_PART_OF
    );

    private LabelKeys() {
    }
}

package tech.kronicle.service.constants;

import java.util.List;

public class KronicleMetadataFilePaths {

  public static final String COMPONENT_METADATA_YAML = "component-metadata.yaml";
  public static final String KRONICLE_YAML = "kronicle.yaml";
  public static final List<String> ALL = List.of(KRONICLE_YAML, COMPONENT_METADATA_YAML);

  private KronicleMetadataFilePaths() {
  }
}

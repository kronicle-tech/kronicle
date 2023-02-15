package tech.kronicle;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import tech.kronicle.common.ValidationConstraintViolationTransformer;
import tech.kronicle.common.ValidatorService;
import tech.kronicle.sdk.models.ComponentMetadata;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This class is used by the build scripts of the PR build jobs and master build jobs for "component-metadata" repos that contain "kronicle.yaml"
 * files that define components, teams, areas etc.
 */
public class KronicleMetadataValidator {

    private static final YAMLMapper YAML_MAPPER = new YAMLMapper();
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final ValidationConstraintViolationTransformer CONSTRAINT_VIOLATION_TRANSFORMER = new ValidationConstraintViolationTransformer();
    private static final ValidatorService VALIDATION_SERVICE = new ValidatorService(VALIDATOR, CONSTRAINT_VIOLATION_TRANSFORMER);

    static {
        YAML_MAPPER.configure(DeserializationFeature. FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    private KronicleMetadataValidator() {
    }

    public static void validate(File componentMetadataFile) throws IOException {
        validate(Files.readString(componentMetadataFile.toPath()));
    }

    public static void validate(String componentMetadataYaml) throws com.fasterxml.jackson.core.JsonProcessingException {
        validate(YAML_MAPPER.readValue(componentMetadataYaml, ComponentMetadata.class));
    }

    public static void validate(ComponentMetadata componentMetadata) {
        VALIDATION_SERVICE.validate(componentMetadata, "Component Metadata file has failed validation");
    }
}

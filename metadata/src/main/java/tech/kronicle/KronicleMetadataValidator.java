package tech.kronicle;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import tech.kronicle.common.services.ValidationConstraintViolationTransformer;
import tech.kronicle.componentmetadata.exceptions.ValidationException;
import tech.kronicle.componentmetadata.models.ComponentMetadata;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

/**
 * This class is used by the build scripts of the PR build jobs and master build jobs for "component-metadata" repos that contain "component-metadata.yaml"
 * files that define components, teams, areas etc.
 */
public class KronicleMetadataValidator {

    private static final YAMLMapper YAML_MAPPER = new YAMLMapper(new YAMLFactory());
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final ValidationConstraintViolationTransformer CONSTRAINT_VIOLATION_TRANSFORMER = new ValidationConstraintViolationTransformer();

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
        Set<ConstraintViolation<ComponentMetadata>> constraintViolations = VALIDATOR.validate(componentMetadata);

        if (!constraintViolations.isEmpty()) {
            throw new ValidationException(String.format("Component Metadata file has failed validation:%n%s",
                    CONSTRAINT_VIOLATION_TRANSFORMER.transform(constraintViolations)));
        }
    }
}

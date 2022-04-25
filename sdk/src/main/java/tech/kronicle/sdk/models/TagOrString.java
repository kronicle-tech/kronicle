package tech.kronicle.sdk.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import tech.kronicle.sdk.constants.PatternStrings;
import tech.kronicle.sdk.jackson.TagOrStringDeserializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@JsonDeserialize(using = TagOrStringDeserializer.class)
public interface TagOrString {

    @NotBlank
    @Pattern(regexp = PatternStrings.ID)
    String getKey();
    String getValue();
}

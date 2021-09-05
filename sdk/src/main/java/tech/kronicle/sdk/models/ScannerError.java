package tech.kronicle.sdk.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;

import static java.util.Objects.nonNull;

@Value
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class ScannerError {

    @NotBlank
    String scannerId;
    @NotBlank
    String message;
    ScannerError cause;

    @Override
    public String toString() {
        String text = this.message;
        if (nonNull(cause)) {
            text += " | " + cause.toString();
        }
        return text;
    }
}

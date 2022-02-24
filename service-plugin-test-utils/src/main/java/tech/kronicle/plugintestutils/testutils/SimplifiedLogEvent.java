package tech.kronicle.plugintestutils.testutils;

import ch.qos.logback.classic.Level;
import lombok.Value;

@Value
public class SimplifiedLogEvent {

    Level level;
    String formattedMessage;
}

package tech.kronicle.plugintestutils;

import ch.qos.logback.classic.Level;
import lombok.Value;

@Value
public class SimplifiedLogEvent {

    Level level;
    String formattedMessage;
}

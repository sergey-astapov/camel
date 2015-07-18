import static ch.qos.logback.classic.Level.*

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%-4relative %logger{5} [%thread] - %msg%n"
    }
}

logger("com.eventhandler.core.aggregate", DEBUG)

root(INFO, ["CONSOLE"])
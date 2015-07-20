import static ch.qos.logback.classic.Level.*

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%-4relative %logger{5} [%thread] - %msg%n"
    }
}

root(INFO, ["CONSOLE"])
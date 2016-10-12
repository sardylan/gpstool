import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelThresholdFilter
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.joran.spi.ConsoleTarget
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

//def logDir = new File(System.getProperty("user.home"), "/work/utility").toString();
//def logName = "gpstool.log";
//def logTag = "%d{yyyyMMdd}";

appender("STDOUT", ConsoleAppender) {
    target = ConsoleTarget.SystemOut
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{5} - %msg%n"
    }
    filter(LevelThresholdFilter) {
        condition = LevelThresholdFilter.Condition.LESSER
    }
}

appender("STDERR", ConsoleAppender) {
    target = ConsoleTarget.SystemErr
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{5} - %msg%n"
    }
    filter LevelThresholdFilter
}

//appender("FILE", RollingFileAppender) {
//    append = true
//    rollingPolicy(TimeBasedRollingPolicy) {
//        fileNamePattern = new File(logDir, String.format("%s-%s", logTag, logName)).toString()
//        maxHistory = 30
//    }
//    encoder(PatternLayoutEncoder) {
//        pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{5} - %msg%n"
//    }
//}

root(WARN, ["STDOUT", "STDERR"])

logger("org.thehellnet.tools.gpstool", DEBUG)

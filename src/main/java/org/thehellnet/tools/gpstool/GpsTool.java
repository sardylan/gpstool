package org.thehellnet.tools.gpstool;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thehellnet.tools.gpstool.serial.GpsSerial;
import org.thehellnet.tools.gpstool.serial.GpsSerialCallback;
import org.thehellnet.utility.gpsutility.exception.nmea.NMEAException;
import org.thehellnet.utility.gpsutility.sentence.AbstractNMEASentence;
import org.thehellnet.utility.gpsutility.sentence.NMEASentenceFactory;

/**
 * Created by sardylan on 31/08/16.
 */
public class GpsTool implements GpsSerialCallback {

    private static final Logger logger = LoggerFactory.getLogger(GpsTool.class);

    private GpsSerial gpsSerial;

    public static void main(String[] args) {
        final GpsTool gpsTool = new GpsTool();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                gpsTool.stop();
            }
        });
        gpsTool.run(args);
    }

    @Override
    public void newLine(String rawLine) {
        logger.debug(rawLine);

        AbstractNMEASentence sentence;
        try {
            sentence = NMEASentenceFactory.parseSentence(rawLine);
        } catch (NMEAException e) {
            e.printStackTrace();
            return;
        }

        logger.info(sentence.getIdentifier());
    }

    private void run(String[] args) {
        logger.info("START");

        String port = args[0];

        gpsSerial = new GpsSerial(this);
        gpsSerial.config(port,
                SerialPort.BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

        try {
            gpsSerial.start();
        } catch (SerialPortException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        gpsSerial.join();
        gpsSerial.stop();

        logger.info("END");
    }

    private void stop() {
        gpsSerial.stop();
    }
}

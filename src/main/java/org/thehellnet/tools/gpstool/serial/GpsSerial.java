package org.thehellnet.tools.gpstool.serial;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by sardylan on 31/08/16.
 */
public class GpsSerial {

    private static final Logger logger = LoggerFactory.getLogger(GpsSerial.class);

    private GpsSerialCallback callback;
    private SerialPort serialPort;

    private String portName;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parity;

    private boolean running = false;
    private Thread thread;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public GpsSerial(GpsSerialCallback callback) {
        this.callback = callback;
    }

    public void config(String portName, int baudRate, int dataBits, int stopBits, int parity) {
        this.portName = portName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }

    public synchronized void start() throws SerialPortException {
        if (running) return;
        running = true;

        serialPort = new SerialPort(portName);
        serialPort.openPort();
        serialPort.setParams(baudRate, dataBits, stopBits, parity);

        thread = new Thread(() -> {
            while (!thread.isInterrupted() && thread.isAlive()) {
                byte b;

                try {
                    b = serialPort.readBytes(1)[0];
                } catch (SerialPortException e) {
                    e.printStackTrace();
                    logger.warn(e.getMessage());
                    break;
                }

                if (b == '\n' || b == '\r') {
                    byte[] rawLine = buffer.toByteArray();
                    if (rawLine.length == 0) {
                        continue;
                    }

                    String line = new String(rawLine);
                    callback.newLine(line);

                    buffer.reset();
                    continue;
                }

                buffer.write(b);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;

        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

        serialPort = null;
    }

    public void join() {
        if (!running) return;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

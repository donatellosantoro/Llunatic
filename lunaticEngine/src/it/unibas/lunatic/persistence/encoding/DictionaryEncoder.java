package it.unibas.lunatic.persistence.encoding;

import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.operators.dbms.IValueEncoder;
import speedy.utility.SpeedyUtility;

public class DictionaryEncoder implements IValueEncoder {

    private final static Logger logger = LoggerFactory.getLogger(DictionaryEncoder.class);
    private final String scenarioName;
    private WritingThread writingThread = null;
    private boolean writingInProgress = false;
    private long lastValue = 0;
    private Map<String, Long> encodingMap;
    private Map<Long, String> decodingMap;

    public DictionaryEncoder(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String encode(String original) {
        if (encodingMap == null) {
            loadEncodingMap();
        }
        long start = new Date().getTime();
        Long encoded = encodingMap.get(original);
        if (encoded == null) {
            encoded = nextValue();
            encodingMap.put(original, encoded);
        }
        if (original.equals("")) {

        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.DICTIONARY_ENCODING_TIME, end - start);
        return encoded + "";
    }

    public String decode(String encoded) {
        long start = new Date().getTime();
        String decodedValue = decodeValueUsingCache(encoded);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.DICTIONARY_DECODING_TIME, end - start);
        return decodedValue;
    }

    private String decodeValueUsingCache(String encoded) {
        Long encodedValue;
        try {
            encodedValue = Long.parseLong(encoded);
        } catch (NumberFormatException nfe) {
            throw new DAOException("Unable to decode string value " + encoded);
        }
        String decoded = decodingMap.get(encodedValue);
        if (decoded == null) {
            if (SpeedyUtility.isSkolem(encoded) || SpeedyUtility.isVariable(encoded)) {
                return encoded;
            }
            throw new DAOException("Unable to decode value " + encodedValue);
        }
        return decoded;
    }

    private Long nextValue() {
        lastValue++;
        if (lastValue < SpeedyConstants.MIN_BIGINT_SKOLEM_VALUE && lastValue < SpeedyConstants.MIN_BIGINT_LLUN_VALUE) {
            return lastValue;
        }
        String stringLastValue = lastValue + "";
        if (stringLastValue.startsWith(SpeedyConstants.BIGINT_SKOLEM_PREFIX) || stringLastValue.startsWith(SpeedyConstants.BIGINT_LLUN_PREFIX)) {
            lastValue += SpeedyConstants.MIN_BIGINT_SAFETY_SKIP_VALUE;
        }
        if (logger.isTraceEnabled()) {
            if (SpeedyUtility.isSkolem(lastValue + "") || SpeedyUtility.isVariable(lastValue + "")) throw new IllegalArgumentException("Dictionary encoder generates a skolem or variable " + lastValue);
            if (lastValue % 1000000L == 0) logger.trace("Next value: " + lastValue);
        }
        return lastValue;
    }

    public void prepareForEncoding() {
        encodingMap = null;
    }

    private void loadEncodingMap() {
        File mapFile = new File(getFileForEncoding());
        if (!mapFile.canRead()) {
            encodingMap = new HashMap<String, Long>();
            return;
        }
        loadMapFromFile(mapFile);
    }

    @SuppressWarnings("unchecked")
    private void loadMapFromFile(File mapFile) {
        ObjectInputStream inStream = null;
        try {
            inStream = new ObjectInputStream(new FileInputStream(mapFile));
            int size = inStream.readInt();
            encodingMap = new HashMap<String, Long>(size);
            for (int i = 0; i < size; i++) {
                String key = inStream.readUTF();
                Long value = inStream.readLong();
                encodingMap.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException("Unable to load map from file " + mapFile + ".\n" + e.getLocalizedMessage());
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException ioe) {
            }
        }
    }

    public void closeEncoding() {
        if (encodingMap == null) {
            return;
        }
        writingThread = new WritingThread(encodingMap, getFileForEncoding());
        writingThread.start();
        encodingMap = null;
    }

    public void prepareForDecoding() {
        if (writingInProgress) {
            try {
                writingThread.join();
            } catch (InterruptedException ex) {
            }
        }
        File mapFile = new File(getFileForEncoding());
        if (!mapFile.canRead()) {
            throw new DAOException("Unable to load encoding map file " + mapFile);
        }
        ObjectInputStream inStream = null;
        try {
            inStream = new ObjectInputStream(new FileInputStream(mapFile));
            int size = inStream.readInt();
            decodingMap = new HashMap<Long, String>(size);
            for (int i = 0; i < size; i++) {
                String key = inStream.readUTF();
                Long value = inStream.readLong();
                if (logger.isTraceEnabled()) {
                    if (decodingMap.containsKey(value)) {
                        throw new IllegalArgumentException("Value " + value + " for key " + key + " was already used");
                    }
                }
                decodingMap.put(value, key);
            }
        } catch (Exception e) {
            throw new DAOException("Unable to load map from file " + mapFile + ".\n" + e.getLocalizedMessage());
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException ioe) {
            }
        }
    }

    public void closeDecoding() {
    }

    private String getFileForEncoding() {
        String homeDir = System.getProperty("user.home");
        return homeDir + File.separator + SpeedyConstants.WORK_DIR + File.separator + "Encoding" + File.separator + "MAP_" + scenarioName + ".map";
    }

    public void waitingForEnding() {
        if (writingInProgress) {
            try {
                writingThread.join();
            } catch (InterruptedException ex) {
            }
        }
    }

    class WritingThread extends Thread {

        private Map<String, Long> mapToWrite;
        private String fileToWrite;

        public WritingThread(Map<String, Long> mapToWrite, String fileToWrite) {
            this.mapToWrite = mapToWrite;
            this.fileToWrite = fileToWrite;
        }

        @Override
        public void run() {
            writingInProgress = true;
            ObjectOutputStream out = null;
            try {
                long start = new Date().getTime();
                File mapFile = new File(fileToWrite);
                mapFile.getParentFile().mkdirs();
                out = new ObjectOutputStream(new FileOutputStream(mapFile));
                out.writeInt(mapToWrite.size());
                for (String key : mapToWrite.keySet()) {
                    Long value = mapToWrite.get(key);
                    out.writeUTF(key);
                    out.writeLong(value);
                }
                out.close();
                long end = new Date().getTime();
                ChaseStats.getInstance().addStat(ChaseStats.DICTIONARY_WRITING_TIME, end - start);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new DAOException("Unable to write dictionary to file " + fileToWrite + ".\n" + ex.getLocalizedMessage());
            } finally {
                try {
                    if (out != null) out.close();
                } catch (IOException ex) {
                }
                writingInProgress = false;
            }
        }

    }

}

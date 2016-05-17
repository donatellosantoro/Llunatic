package it.unibas.lunatic.persistence;

import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import java.util.Date;
import java.util.Properties;
import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.engine.control.CompositeCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.operators.dbms.IValueEncoder;
import speedy.utility.SpeedyUtility;

public class DictionaryEncoder implements IValueEncoder {

    private final static Logger logger = LoggerFactory.getLogger(DictionaryEncoder.class);
    private String scenarioName;
    private long lastValue = 0;
    private CacheAccess<String, Long> encodingCache;
    private CacheAccess<Long, String> decodingCache;

    public DictionaryEncoder(String scenarioName) {
        this.scenarioName = scenarioName;
        this.initCaches();
    }

    public String encode(String original) {
        long start = new Date().getTime();
        Long encoded = encodingCache.get(original);
        if (encoded == null) {
            encoded = nextValue();
            encodingCache.put(original, encoded);
            decodingCache.put(encoded, original);
        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.DICTIONARY_ENCODING_TIME, end - start);
        return encoded + "";
    }

    public String decode(String encoded) {
        long start = new Date().getTime();
        String decodedValue = decodeValueUsingCache(encoded);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.DICTIONARY_ENCODING_TIME, end - start);
        return decodedValue;
    }

    private String decodeValueUsingCache(String encoded) {
        Long encodedValue;
        try {
            encodedValue = Long.parseLong(encoded);
        } catch (NumberFormatException nfe) {
            throw new DAOException("Unable to decode string value " + encoded);
        }
        String decoded = decodingCache.get(encodedValue);
        if (decoded == null) {
            if (SpeedyUtility.isSkolem(encoded) || SpeedyUtility.isVariable(encoded)) {
                return encoded;
            }
            throw new DAOException("Unable to decode value " + encodedValue + ". Cache stats:\n" + decodingCache.getStats());
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
        return lastValue;
    }

    private void initCaches() {
        long start = new Date().getTime();
        Properties props = new Properties();
        // Region cache
        props.put("jcs.region.dictionarycache_enc", "DICTIONARY_ENC");
        props.put("jcs.region.dictionarycache_enc.cacheattributes", "org.apache.jcs.engine.CompositeCacheAttributes");
        props.put("jcs.region.dictionarycache_enc.cacheattributes.MaxObjects", "100000000");
        props.put("jcs.region.dictionarycache_enc.cacheattributes.MemoryCacheName", "org.apache.jcs.engine.memory.lru.LRUMemoryCache");
        props.put("jcs.region.dictionarycache_enc.cacheattributes.UseMemoryShrinker", "true");
        props.put("jcs.region.dictionarycache_enc.cacheattributes.MaxMemoryIdleTimeSeconds", "3600");
        props.put("jcs.region.dictionarycache_enc.cacheattributes.ShrinkerIntervalSeconds", "60");
        props.put("jcs.region.dictionarycache_enc.cacheattributes.MaxSpoolPerRun", "500");
        props.put("jcs.region.dictionarycache_enc.cacheattributes.DiskUsagePatternName", "UPDATE");
        props.put("jcs.region.dictionarycache_enc.elementattributes", "org.apache.jcs.engine.ElementAttributes");
        props.put("jcs.region.dictionarycache_enc.elementattributes.IsEternal", "true");
        // Auxiliary
        props.put("jcs.auxiliary.DICTIONARY_ENC", "org.apache.commons.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory");
        props.put("jcs.auxiliary.DICTIONARY_ENC.attributes", "org.apache.commons.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes");
        props.put("jcs.auxiliary.DICTIONARY_ENC.attributes.DiskPath", "${user.home}/Temp/JCS/Dictionary/" + scenarioName + "/");
        props.put("jcs.auxiliary.DICTIONARY_ENC.attributes.MaxPurgatorySize", "10000");
        props.put("jcs.auxiliary.DICTIONARY_ENC.attributes.MaxKeySize", "-1");
        props.put("jcs.auxiliary.DICTIONARY_ENC.attributes.MaxRecycleBinSize", "7500");
        props.put("jcs.auxiliary.DICTIONARY_ENC.attributes.ClearDiskOnStartup", "false");
        props.put("jcs.auxiliary.DICTIONARY_ENC.attributes.OptimizeOnShutdown", "false");
        // Region cache
        props.put("jcs.region.dictionarycache_dec", "DICTIONARY_DEC");
        props.put("jcs.region.dictionarycache_dec.cacheattributes", "org.apache.jcs.engine.CompositeCacheAttributes");
        props.put("jcs.region.dictionarycache_dec.cacheattributes.MaxObjects", "100000000");
        props.put("jcs.region.dictionarycache_dec.cacheattributes.MemoryCacheName", "org.apache.jcs.engine.memory.lru.LRUMemoryCache");
        props.put("jcs.region.dictionarycache_dec.cacheattributes.UseMemoryShrinker", "true");
        props.put("jcs.region.dictionarycache_dec.cacheattributes.MaxMemoryIdleTimeSeconds", "3600");
        props.put("jcs.region.dictionarycache_dec.cacheattributes.ShrinkerIntervalSeconds", "60");
        props.put("jcs.region.dictionarycache_dec.cacheattributes.MaxSpoolPerRun", "500");
        props.put("jcs.region.dictionarycache_dec.cacheattributes.DiskUsagePatternName", "UPDATE");
        props.put("jcs.region.dictionarycache_dec.elementattributes", "org.apache.jcs.engine.ElementAttributes");
        props.put("jcs.region.dictionarycache_dec.elementattributes.IsEternal", "true");
        // Auxiliary
        props.put("jcs.auxiliary.DICTIONARY_DEC", "org.apache.commons.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory");
        props.put("jcs.auxiliary.DICTIONARY_DEC.attributes", "org.apache.commons.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes");
        props.put("jcs.auxiliary.DICTIONARY_DEC.attributes.DiskPath", "${user.home}/Temp/JCS/Dictionary/" + scenarioName + "/");
        props.put("jcs.auxiliary.DICTIONARY_DEC.attributes.MaxPurgatorySize", "10000");
        props.put("jcs.auxiliary.DICTIONARY_DEC.attributes.MaxKeySize", "-1");
        props.put("jcs.auxiliary.DICTIONARY_DEC.attributes.MaxRecycleBinSize", "7500");
        props.put("jcs.auxiliary.DICTIONARY_DEC.attributes.ClearDiskOnStartup", "false");
        props.put("jcs.auxiliary.DICTIONARY_DEC.attributes.OptimizeOnShutdown", "false");
        // Configure
        CompositeCacheManager ccm = CompositeCacheManager.getUnconfiguredInstance();
        ccm.configure(props);
        // Access region
        this.encodingCache = JCS.getInstance("dictionarycache_enc");
        this.decodingCache = JCS.getInstance("dictionarycache_dec");
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.DICTIONARY_ENCODING_TIME, end - start);
    }

}

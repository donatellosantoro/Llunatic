package it.unibas.lunatic.persistence;

public class DAOConfiguration {

    private String suffix;
    private boolean importData = true;
    private boolean processDependencies = true;
    private boolean exportEncodedDependencies = false;
    private boolean useEncodedDependencies = false;
    private boolean removeExistingDictionary = false;
    //Parameters received via commandline that we need to override on the loaded configuration
    private Boolean useDictionaryEncoding;
    private Boolean printTargetStats;
    private Boolean useCompactAttributeName;
    private String chaseMode;

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isImportData() {
        return importData;
    }

    public void setImportData(boolean importData) {
        this.importData = importData;
    }

    public boolean isProcessDependencies() {
        return processDependencies;
    }

    public void setProcessDependencies(boolean processDependencies) {
        this.processDependencies = processDependencies;
    }

    public boolean isExportEncodedDependencies() {
        return exportEncodedDependencies;
    }

    public void setExportEncodedDependencies(boolean exportEncodedDependencies) {
        this.exportEncodedDependencies = exportEncodedDependencies;
    }

    public boolean isUseEncodedDependencies() {
        return useEncodedDependencies;
    }

    public void setUseEncodedDependencies(boolean useEncodedDependencies) {
        this.useEncodedDependencies = useEncodedDependencies;
    }

    public boolean isRemoveExistingDictionary() {
        return removeExistingDictionary;
    }

    public void setRemoveExistingDictionary(boolean removeExistingDictionary) {
        this.removeExistingDictionary = removeExistingDictionary;
    }

    public Boolean getUseDictionaryEncoding() {
        return useDictionaryEncoding;
    }

    public void setUseDictionaryEncoding(Boolean useDictionaryEncoding) {
        this.useDictionaryEncoding = useDictionaryEncoding;
    }

    public Boolean getUseCompactAttributeName() {
        return useCompactAttributeName;
    }

    public void setUseCompactAttributeName(Boolean useCompactAttributeName) {
        this.useCompactAttributeName = useCompactAttributeName;
    }

    public String getChaseMode() {
        return chaseMode;
    }

    public void setChaseMode(String chaseMode) {
        this.chaseMode = chaseMode;
    }

    public Boolean getPrintTargetStats() {
        return printTargetStats;
    }

    public void setPrintTargetStats(Boolean printTargetStats) {
        this.printTargetStats = printTargetStats;
    }

}

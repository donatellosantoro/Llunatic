package it.unibas.lunatic.persistence;

public class DAOConfiguration {

    private String suffix;
    private boolean importData = true;
    private boolean processDependencies = true;
    private boolean exportRewrittenDependencies = false;
    private boolean useRewrittenDependencies = false;

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

    public boolean isExportRewrittenDependencies() {
        return exportRewrittenDependencies;
    }

    public void setExportRewrittenDependencies(boolean exportRewrittenDependencies) {
        this.exportRewrittenDependencies = exportRewrittenDependencies;
    }

    public boolean isUseRewrittenDependencies() {
        return useRewrittenDependencies;
    }

    public void setUseRewrittenDependencies(boolean useRewrittenDependencies) {
        this.useRewrittenDependencies = useRewrittenDependencies;
    }

}

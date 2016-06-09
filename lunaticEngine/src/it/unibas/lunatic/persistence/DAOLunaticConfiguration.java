package it.unibas.lunatic.persistence;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerConfiguration;
import it.unibas.lunatic.model.chase.chasemc.partialorder.FrequencyPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.GreedyPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.IPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.OrderingAttribute;
import it.unibas.lunatic.model.chase.chasemc.partialorder.ScriptPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.StandardPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator.DateComparator;
import it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator.FloatComparator;
import it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator.IValueComparator;
import it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator.StringComparatorForIValues;
import it.unibas.lunatic.model.chase.chasemc.usermanager.AfterForkUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.AfterLLUNForkUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.AfterLLUNUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.IUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.InteractiveUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.StandardUserManager;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.similarity.SimilarityConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.script.ScriptException;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.exceptions.DAOException;
import speedy.model.database.AttributeRef;
import speedy.persistence.xml.DAOXmlUtility;
import speedy.persistence.xml.operators.TransformFilePaths;

public class DAOLunaticConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(DAOLunaticConfiguration.class);
    ///////////////////// PARTIAL ORDER
    private static final String PARTIAL_ORDER_STANDARD = "Standard";
    private static final String PARTIAL_ORDER_FREQUENCY = "Frequency";
    private static final String PARTIAL_ORDER_GREEDY = "Greedy";
    private static final String PARTIAL_ORDER_FREQUENCY_FO = "Frequency FO";
    ///////////////////// USER MANAGER
    private static final String USER_MANAGER_STANDARD = "Standard";
    private static final String USER_MANAGER_INTERACTIVE = "Interactive";
    private static final String USER_MANAGER_AFTER_LLUN = "AfterLLUN";
    private static final String USER_MANAGER_AFTER_LLUN_FORK = "AfterLLUNFork";
    private static final String USER_MANAGER_AFTER_FORK = "AfterFork";
    private static final String VALUE_COMPARATOR_FLOAT = "floatComparator";
    private static final String VALUE_COMPARATOR_DATE = "dateComparator";
    private static final String VALUE_COMPARATOR_STRING = "stringComparator";
    ///////////////////// 
    private final DAOXmlUtility daoUtility = new DAOXmlUtility();
    private final TransformFilePaths filePathTransformator = new TransformFilePaths();

    public LunaticConfiguration loadConfiguration(String fileScenario) {
        try {
            Document document = daoUtility.buildDOM(fileScenario);
            Element rootElement = document.getRootElement();
            Element configurationElement = rootElement.getChild("configuration");
            return loadConfiguration(configurationElement);
        } catch (Throwable ex) {
            logger.error(ex.getLocalizedMessage());
            ex.printStackTrace();
            String message = "Unable to load egtask from file " + fileScenario;
            if (ex.getMessage() != null && !ex.getMessage().equals("NULL")) {
                message += "\n" + ex.getMessage();
            }
            throw new DAOException(message);
        }
    }

    public LunaticConfiguration loadConfiguration(Element configurationElement) {
        LunaticConfiguration configuration = new LunaticConfiguration();
        if (configurationElement == null || configurationElement.getChildren().isEmpty()) {
            return configuration;
        }
        Element printResultsElement = configurationElement.getChild("printResults");
        if (printResultsElement != null) {
            configuration.setPrintResults(Boolean.parseBoolean(printResultsElement.getValue()));
        }
        Element printStepsElement = configurationElement.getChild("printSteps");
        if (printStepsElement != null) {
            configuration.setPrintSteps(Boolean.parseBoolean(printStepsElement.getValue()));
        }
        Element recreateDBOnStartElement = configurationElement.getChild("recreateDBOnStart");
        if (recreateDBOnStartElement != null) {
            configuration.setRecreateDBOnStart(Boolean.parseBoolean(recreateDBOnStartElement.getValue()));
        }
        Element useLimit1Element = configurationElement.getChild("useLimit1");
        if (useLimit1Element != null) {
            configuration.setUseLimit1ForEGDs(Boolean.parseBoolean(useLimit1Element.getValue()));
        }
        Element useHashForSkolemElement = configurationElement.getChild("useHashForSkolem");
        if (useHashForSkolemElement != null) {
            configuration.setUseHashForSkolem(Boolean.parseBoolean(useHashForSkolemElement.getValue()));
        }
        Element useDictionaryEncodingElement = configurationElement.getChild("useDictionaryEncoding");
        if (useDictionaryEncodingElement != null) {
            configuration.setUseDictionaryEncoding(Boolean.parseBoolean(useDictionaryEncodingElement.getValue()));
        }
        Element optimizeTGDsElement = configurationElement.getChild("optimizeSTTGDs");
        if (optimizeTGDsElement != null) {
            configuration.setOptimizeSTTGDs(Boolean.parseBoolean(optimizeTGDsElement.getValue()));
        }
        Element rewriteSTTGDOverlapsElement = configurationElement.getChild("rewriteSTTGDOverlaps");
        if (rewriteSTTGDOverlapsElement != null) {
            configuration.setRewriteSTTGDOverlaps(Boolean.parseBoolean(rewriteSTTGDOverlapsElement.getValue()));
        }
        Element removeDuplicatesElement = configurationElement.getChild("removeDuplicates");
        if (removeDuplicatesElement != null) {
            configuration.setRemoveDuplicates(Boolean.parseBoolean(removeDuplicatesElement.getValue()));
        }
        Element checkGroundSolutionsElement = configurationElement.getChild("checkGroundSolutions");
        if (checkGroundSolutionsElement != null) {
            configuration.setCheckGroundSolutions(Boolean.parseBoolean(checkGroundSolutionsElement.getValue()));
        }
        Element exportSolutionsElement = configurationElement.getChild("exportSolutions");
        if (exportSolutionsElement != null) {
            configuration.setExportSolutions(Boolean.parseBoolean(exportSolutionsElement.getValue()));
        }
        Element exportSolutionsPathElement = configurationElement.getChild("exportSolutionsPath");
        if (exportSolutionsPathElement != null) {
            configuration.setExportSolutionsPath(exportSolutionsPathElement.getValue());
        }
        Element exportSolutionsTypeElement = configurationElement.getChild("exportSolutionsType");
        if (exportSolutionsTypeElement != null) {
            configuration.setExportSolutionsType(exportSolutionsTypeElement.getValue());
            if (!configuration.getExportSolutionsType().equals(SpeedyConstants.CSV)) {
                throw new it.unibas.lunatic.exceptions.DAOException("Export type not supported");
            }
        }
        Element exportQueryResultsElement = configurationElement.getChild("exportQueryResults");
        if (exportQueryResultsElement != null) {
            configuration.setExportQueryResults(Boolean.parseBoolean(exportQueryResultsElement.getValue()));
        }
        Element exportQueryResultsPathElement = configurationElement.getChild("exportQueryResultsPath");
        if (exportQueryResultsPathElement != null) {
            configuration.setExportQueryResultsPath(exportQueryResultsPathElement.getValue());
        }
        Element exportQueryResultsTypeElement = configurationElement.getChild("exportQueryResultsType");
        if (exportQueryResultsTypeElement != null) {
            configuration.setExportQueryResultsType(exportQueryResultsTypeElement.getValue());
            if (!configuration.getExportQueryResultsType().equals(SpeedyConstants.CSV)) {
                throw new it.unibas.lunatic.exceptions.DAOException("Export type not supported");
            }
        }
        Element exportChangesElement = configurationElement.getChild("exportChanges");
        if (exportChangesElement != null) {
            configuration.setExportChanges(Boolean.parseBoolean(exportChangesElement.getValue()));
        }
        Element exportChangesPathElement = configurationElement.getChild("exportChangesPath");
        if (exportChangesPathElement != null) {
            configuration.setExportChangesPath(exportChangesPathElement.getValue());
        }
        Element autoSelectBestNumberOfThreadsElement = configurationElement.getChild("autoSelectBestNumberOfThreads");
        if (autoSelectBestNumberOfThreadsElement != null) {
            configuration.setAutoSelectBestNumberOfThreads(Boolean.parseBoolean(autoSelectBestNumberOfThreadsElement.getValue()));
        }
        Element maxNumberOfThreadsElement = configurationElement.getChild("maxNumberOfThreads");
        if (maxNumberOfThreadsElement != null) {
            configuration.setMaxNumberOfThreads(Integer.parseInt(maxNumberOfThreadsElement.getValue()));
        }
        if (configuration.isAutoSelectBestNumberOfThreads()) {
            selectBestNumberOfThreads(configuration);
        }
        return configuration;
    }

    public void loadOtherScenarioElements(Element rootElement, Scenario scenario) {
        //ADDITIONAL ATTRIBUTES
        Element additionalAttributesElement = rootElement.getChild("additionalAttributes");
        loadAdditionalAttributes(additionalAttributesElement, scenario);
        //ORDERING ATTRIBUTES
        Element orderingAttributesElement = rootElement.getChild("orderingAttributes");
        loadOrderingAttributes(orderingAttributesElement, scenario);
        //PARTIAL-ORDER
        Element partialOrderElement = rootElement.getChild("partialOrder");
        IPartialOrder partialOrder = loadPartialOrder(partialOrderElement, scenario.getFileName());
        scenario.setPartialOrder(partialOrder);
        //SCRIPT PARTIAL-ORDER
        Element scriptPartialOrderElement = rootElement.getChild("scriptPartialOrder");
        ScriptPartialOrder scriptPartialORder = loadScriptPartialOrder(scriptPartialOrderElement, scenario.getFileName());
        scenario.setScriptPartialOrder(scriptPartialORder);
        //COST-MANAGER
        Element costManagerElement = rootElement.getChild("costManager");
        CostManagerConfiguration costManagerConfiguration = loadCostManagerConfiguration(costManagerElement, scenario.getFileName());
        if (costManagerConfiguration != null) {
            scenario.setCostManagerConfiguration(costManagerConfiguration);
        }
        //USER-MANAGER
        Element userManagerElement = rootElement.getChild("userManager");
        IUserManager userManager = loadUserManager(userManagerElement, scenario);
        scenario.setUserManager(userManager);
    }

    @SuppressWarnings("unchecked")
    private void loadAdditionalAttributes(Element additionalAttributesElement, Scenario scenario) {
        if (additionalAttributesElement == null || additionalAttributesElement.getChildren().isEmpty()) {
            return;
        }
        List<Element> dependencies = additionalAttributesElement.getChildren("dependency");
        for (Element dependencyElement : dependencies) {
            String dependencyId = dependencyElement.getChildText("id");
            String stringAttributeRef = dependencyElement.getChildText("attribute");
            AttributeRef attributeRef = parseAttributeRef(stringAttributeRef);
            Dependency dependency = scenario.getDependency(dependencyId);
            if (dependency == null) {
                throw new it.unibas.lunatic.exceptions.DAOException("Unable to set additional attribute for unkown dependency " + dependencyId);
            }
            dependency.addAdditionalAttribute(attributeRef);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadOrderingAttributes(Element orderingAttributesElement, Scenario scenario) {
        if (orderingAttributesElement == null || orderingAttributesElement.getChildren().isEmpty()) {
            return;
        }
        List<OrderingAttribute> orderingAttributes = new ArrayList<OrderingAttribute>();
        List<Element> orderingAttributeElements = orderingAttributesElement.getChildren("orderingAttribute");
        for (Element orderingAttributeElement : orderingAttributeElements) {
            AttributeRef attribute = parseAttributeRef(orderingAttributeElement.getChildText("attribute"));
            AttributeRef associatedAttribute = parseAttributeRef(orderingAttributeElement.getChildText("associatedAttribute"));
            IValueComparator valueComparator = extractValueComparator(orderingAttributeElement.getChild("valueComparator"), scenario.getFileName());
            OrderingAttribute orderingAttribute = new OrderingAttribute(attribute, associatedAttribute, valueComparator);
            orderingAttributes.add(orderingAttribute);
        }
        scenario.setOrderingAttributes(orderingAttributes);
    }

    private IValueComparator extractValueComparator(Element valueComparatorElement, String fileScenario) {
        if (valueComparatorElement == null) {
            throw new it.unibas.lunatic.exceptions.DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <valueComparator>");
        }
        IValueComparator comparator = null;
        if (valueComparatorElement.getChildren().isEmpty()) {
            comparator = new StringComparatorForIValues();
        } else {
            Element valueComparatorImplElement = (Element) valueComparatorElement.getChildren().get(0);
            String valueComparatorImplName = valueComparatorImplElement.getName();
            if (VALUE_COMPARATOR_FLOAT.equalsIgnoreCase(valueComparatorImplName)) {
                comparator = new FloatComparator();
            } else if (VALUE_COMPARATOR_DATE.equalsIgnoreCase(valueComparatorImplName)) {
                comparator = new DateComparator(valueComparatorImplElement.getAttributeValue("pattern"));
            } else if (VALUE_COMPARATOR_STRING.equalsIgnoreCase(valueComparatorImplName)) {
                comparator = new StringComparatorForIValues();
            }
            if (comparator == null) {
                throw new it.unibas.lunatic.exceptions.DAOException("Unable to load scenario from file " + fileScenario + ". Unknown value comparator " + valueComparatorImplElement.getName());
            }
        }
        String sort = valueComparatorElement.getAttributeValue("sort");
        comparator.setSort(sort);
        return comparator;
    }

    private IPartialOrder loadPartialOrder(Element partialOrderElement, String fileScenario) throws it.unibas.lunatic.exceptions.DAOException {
        if (partialOrderElement == null || partialOrderElement.getChildren().isEmpty()) {
            return new StandardPartialOrder();
        }
        Element typeElement = partialOrderElement.getChild("type");
        if (typeElement == null) {
            throw new it.unibas.lunatic.exceptions.DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <type>");
        }
        String partialOrderType = typeElement.getValue();
        if (PARTIAL_ORDER_STANDARD.equals(partialOrderType)) {
            return new StandardPartialOrder();
        }
        if (PARTIAL_ORDER_FREQUENCY.equals(partialOrderType)) {
            return new FrequencyPartialOrder();
        }
        if (PARTIAL_ORDER_GREEDY.equals(partialOrderType)) {
            return new GreedyPartialOrder();
        }
        if (PARTIAL_ORDER_FREQUENCY_FO.equals(partialOrderType)) {
            return new FrequencyPartialOrder();
//            return new FrequencyPartialOrderFO();
        }
        throw new it.unibas.lunatic.exceptions.DAOException("Unable to load scenario from file " + fileScenario + ". Unknown partial-order type " + partialOrderType);
    }

    private ScriptPartialOrder loadScriptPartialOrder(Element scriptPartialOrderElement, String fileScenario) {
        if (scriptPartialOrderElement == null || scriptPartialOrderElement.getChildren().isEmpty()) {
            return null;
        }
        Element xmlElement = scriptPartialOrderElement.getChild("script");
        if (xmlElement == null) {
            throw new it.unibas.lunatic.exceptions.DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <script>");
        }
        String scriptRelativeFile = xmlElement.getValue();
        String scriptAbsoluteFile = filePathTransformator.expand(fileScenario, scriptRelativeFile);
        try {
            return new ScriptPartialOrder(scriptAbsoluteFile);
        } catch (ScriptException ex) {
            throw new it.unibas.lunatic.exceptions.DAOException("Unable to load partial-order script " + scriptAbsoluteFile + ". " + ex.getLocalizedMessage());
        }
    }

    private CostManagerConfiguration loadCostManagerConfiguration(Element costManagerElement, String fileScenario) throws it.unibas.lunatic.exceptions.DAOException {
        if (costManagerElement == null || costManagerElement.getChildren().isEmpty()) {
            return null;
        }
        Element typeElement = costManagerElement.getChild("type");
        if (typeElement == null) {
            throw new it.unibas.lunatic.exceptions.DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <type>");
        }
        CostManagerConfiguration costManagerConfiguration = new CostManagerConfiguration();
        String costManagerType = typeElement.getValue();
        if (LunaticConstants.COST_MANAGER_STANDARD.equalsIgnoreCase(costManagerType)) {
            costManagerConfiguration.setType(LunaticConstants.COST_MANAGER_STANDARD);
        }
        if (LunaticConstants.COST_MANAGER_GREEDY.equalsIgnoreCase(costManagerType)) {
            costManagerConfiguration.setType(LunaticConstants.COST_MANAGER_GREEDY);
        }
        if (LunaticConstants.COST_MANAGER_SIMILARITY.equalsIgnoreCase(costManagerType)) {
            costManagerConfiguration.setType(LunaticConstants.COST_MANAGER_SIMILARITY);
            Element similarityStrategyElement = costManagerElement.getChild("similarityStrategy");
            if (similarityStrategyElement != null) {
                costManagerConfiguration.getDefaultSimilarityConfiguration().setStrategy(similarityStrategyElement.getValue().trim());
            }
            Element similarityThresholdElement = costManagerElement.getChild("similarityThreshold");
            if (similarityThresholdElement != null) {
                costManagerConfiguration.getDefaultSimilarityConfiguration().setThreshold(Double.parseDouble(similarityThresholdElement.getValue()));
            }
            loadExtraParams(costManagerConfiguration.getDefaultSimilarityConfiguration().getParams(), costManagerElement);
            Element requestMajority = costManagerElement.getChild("requestMajority");
            if (requestMajority != null) {
                costManagerConfiguration.setRequestMajorityInSimilarityCostManager(Boolean.parseBoolean(requestMajority.getValue()));
            }
        }
        if (costManagerConfiguration == null) {
            throw new it.unibas.lunatic.exceptions.DAOException("Unable to load scenario from file " + fileScenario + ". Unknown cost-manager type " + costManagerType);
        }
        Element doBackwardElement = costManagerElement.getChild("doBackward");
        if (doBackwardElement != null) {
            costManagerConfiguration.setDoBackward(Boolean.parseBoolean(doBackwardElement.getValue()));
        }
        Element doPermutationsElement = costManagerElement.getChild("doPermutations");
        if (doPermutationsElement != null) {
            costManagerConfiguration.setDoPermutations(Boolean.parseBoolean(doPermutationsElement.getValue()));
        }
//        Element chaseTreeSizeThresholdElement = costManagerElement.getChild("chaseTreeSizeThreshold");
//        if (chaseTreeSizeThresholdElement != null) {
//            throw new IllegalArgumentException("Replace chase tree size with leavesThreshold");
//        }
        Element chaseBranchingThresholdElement = costManagerElement.getChild("chaseBranchingThreshold");
        if (chaseBranchingThresholdElement != null) {
            costManagerConfiguration.setChaseBranchingThreshold(Integer.parseInt(chaseBranchingThresholdElement.getValue()));
        }
        Element dependencyLimitElement = costManagerElement.getChild("dependencyLimit");
        if (dependencyLimitElement != null) {
            costManagerConfiguration.setDependencyLimit(Integer.parseInt(dependencyLimitElement.getValue()));
        }
        Element potentialSolutionsThresholdElement = costManagerElement.getChild("potentialSolutionsThreshold");
        if (potentialSolutionsThresholdElement != null) {
            costManagerConfiguration.setPotentialSolutionsThreshold(Integer.parseInt(potentialSolutionsThresholdElement.getValue()));
        }
        for (Object noBackwardEl : costManagerElement.getChildren("noBackwardOnDependency")) {
            Element noBackwardElement = (Element) noBackwardEl;
            costManagerConfiguration.addNoBackwardDependency(noBackwardElement.getValue().trim());
        }
        for (Object similarityAttributeEl : costManagerElement.getChildren("similarityForAttribute")) {
            Element similarityAttributeElement = (Element) similarityAttributeEl;
            String tableName = similarityAttributeElement.getAttribute("tableName").getValue().trim();
            String attributeName = similarityAttributeElement.getAttribute("attributeName").getValue().trim();
            AttributeRef attribute = new AttributeRef(tableName, attributeName);
            String similarityStrategy = similarityAttributeElement.getChild("similarityStrategy").getValue().trim();
            double similarityThreshold = Double.parseDouble(similarityAttributeElement.getChild("similarityThreshold").getValue().trim());
            SimilarityConfiguration similarityConfiguration = new SimilarityConfiguration(similarityStrategy, similarityThreshold);
            costManagerConfiguration.setSimilarityConfigurationForAttribute(attribute, similarityConfiguration);
            loadExtraParams(similarityConfiguration.getParams(), similarityAttributeElement);
        }
        return costManagerConfiguration;
    }

    private void loadExtraParams(Map<String, String> params, Element father) {
        Element paramsElement = father.getChild("params");
        if (paramsElement == null) return;
        for (Object children : paramsElement.getChildren()) {
            Element nameElement = (Element) children;
            String key = nameElement.getName();
            String value = nameElement.getValue().trim();
            params.put(key, value);
        }
    }

    private IUserManager loadUserManager(Element userManagerElement, Scenario scenario) {
        if (userManagerElement == null || userManagerElement.getChildren().isEmpty()) {
            return new StandardUserManager();
        }
        Element typeElement = userManagerElement.getChild("type");
        if (typeElement == null) {
            throw new it.unibas.lunatic.exceptions.DAOException("Unable to load scenario from file " + scenario.getAbsolutePath() + ". Missing tag <type>");
        }
        IUserManager userManager = null;
        String userManagerType = typeElement.getValue();
        if (USER_MANAGER_STANDARD.equals(userManagerType)) {
            userManager = new StandardUserManager();
        }
        if (USER_MANAGER_INTERACTIVE.equals(userManagerType)) {
            userManager = new InteractiveUserManager();
        }
        if (USER_MANAGER_AFTER_FORK.equals(userManagerType)) {
            userManager = new AfterForkUserManager();
        }
        if (USER_MANAGER_AFTER_LLUN.equals(userManagerType)) {
            userManager = new AfterLLUNUserManager(OperatorFactory.getInstance().getOccurrenceHandler(scenario));
        }
        if (USER_MANAGER_AFTER_LLUN_FORK.equals(userManagerType)) {
            userManager = new AfterLLUNForkUserManager(OperatorFactory.getInstance().getOccurrenceHandler(scenario));
        }
        return userManager;
    }

    private AttributeRef parseAttributeRef(String stringAttributeRef) {
        String[] tokens = stringAttributeRef.split("\\.");
        if (tokens.length != 2) {
            throw new it.unibas.lunatic.exceptions.DAOException("Unable to parse attribute " + stringAttributeRef);
        }
        return new AttributeRef(tokens[0], tokens[1]);
    }

    private void selectBestNumberOfThreads(LunaticConfiguration configuration) {
        int cores = Runtime.getRuntime().availableProcessors();
        int threads = (cores * 2) - 1;
        if (LunaticConfiguration.isPrintSteps()) System.out.println("Using " + threads + " threads");
        configuration.setMaxNumberOfThreads(threads);
    }

}

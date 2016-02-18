package it.unibas.lunatic.persistence;

import it.unibas.lunatic.exceptions.DAOException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;

public class DAOUtility {

    private static Logger logger = LoggerFactory.getLogger(DAOUtility.class);

    static final String SEPARATOR = "/";

    public BufferedReader getBufferedReader(String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), Charset.forName("UTF-8")));
        return reader;
    }

    public PrintWriter getPrintWriter(String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), Charset.forName("UTF-8")));
        return writer;
    }

    public String generateFolderPath(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf(File.separator));
    }

    public org.jdom.Document buildDOM(String fileName) throws DAOException {
        if (fileName == null || "".equals(fileName)) {
            throw new DAOException("Unable to load file. Null or empty path requested");
        }
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        org.jdom.Document document = null;
        try {
            fileName = checkFilePath(fileName);
            document = builder.build(fileName);
            return document;
        } catch (org.jdom.JDOMException jde) {
            logger.error(jde.getLocalizedMessage());
            throw new DAOException(jde.getMessage());
        } catch (java.io.IOException ioe) {
            logger.error(ioe.getLocalizedMessage());
            throw new DAOException(ioe.getMessage());
        }
    }

    public static String checkFilePath(String path) {
        if (path != null && !path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    public org.w3c.dom.Document buildNewDOM() throws DAOException {
        org.w3c.dom.Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        } catch (ParserConfigurationException pce) {
            logger.error(pce.getLocalizedMessage());
            throw new DAOException(pce.getMessage());
        } catch (DOMException doe) {
            logger.error(doe.getLocalizedMessage());
            throw new DAOException(doe.getMessage());
        }
        return document;
    }

    public void saveDOM(org.w3c.dom.Document document, String filename) throws DAOException {
        try {
            File file = new java.io.File(filename);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", new Integer(2));
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception ex) {
            logger.error("- Exception in saveDOM: \n" + ex);
            throw new DAOException(ex.getMessage());
        }
    }

    public void saveDOM(org.jdom.Document document, String filename) {
        File file = new java.io.File(filename);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));
            XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
            xout.output(document, out);
        } catch (IOException e) {
            logger.error("Error in saving file " + filename + " - " + e.getMessage());
        }
    }

    public static String cleanXmlString(String xmlString) {
        String stringCleaned = xmlString;
        stringCleaned.replaceAll("&gt;", ">");
        stringCleaned.replaceAll("&lt;", "<");
        stringCleaned.replaceAll("&quot;", "\"");
        stringCleaned.replaceAll("&apos;", "'");
        stringCleaned.replaceAll("&amp;", "&");

        return stringCleaned;
    }

    public String relativize(String baseFilePath, String relativeFilePath) {
        baseFilePath = generateFolderPath(baseFilePath);
        List<String> basePathSteps = getPathSteps(baseFilePath);
        if (logger.isDebugEnabled()) logger.debug("Base path steps: " + basePathSteps);
        List<String> filePathSteps = getPathSteps(relativeFilePath);
        if (logger.isDebugEnabled()) logger.debug("File path steps: " + filePathSteps);
        String s = findRelativePathList(basePathSteps, filePathSteps);
        return s;
    }

    public String expand(String baseFilePath, String filePath) {
        if (logger.isDebugEnabled()) logger.debug("Expanding filePath: " + filePath + " wrt base path " + baseFilePath);
        baseFilePath = generateFolderPath(baseFilePath);
        List<String> basePathSteps = getPathSteps(baseFilePath);
        if (logger.isDebugEnabled()) logger.debug("Base path steps: " + basePathSteps);
        List<String> filePathSteps = getPathSteps(filePath);
        if (logger.isDebugEnabled()) logger.debug("File path steps: " + filePathSteps);
        String s = mergePathLists(basePathSteps, filePathSteps);
        return s;
    }

    private List<String> getPathSteps(String filePath) {
        List<String> result = new ArrayList<String>();
        String separators = "/\\";
        StringTokenizer tokenizer = new StringTokenizer(filePath, separators);
        while (tokenizer.hasMoreTokens()) {
            result.add(0, tokenizer.nextToken());
        }
        return result;
    }

    private String findRelativePathList(List basePathSteps, List filePathSteps) {
        int i;
        int j;
        String s = "";
        i = basePathSteps.size() - 1;
        j = filePathSteps.size() - 1;

        // first eliminate common root
        while ((i >= 0) && (j >= 0) && (basePathSteps.get(i).equals(filePathSteps.get(j)))) {
            i--;
            j--;
        }

        // for each remaining level in the base path, add a ..
        for (; i >= 0; i--) {
            s += ".." + SEPARATOR;
        }

        // for each level in the file path, add the path
        for (; j >= 1; j--) {
            s += filePathSteps.get(j) + SEPARATOR;
        }

        // file name
        s += filePathSteps.get(j);
        return s;
    }

    private String mergePathLists(List<String> basePathSteps, List<String> filePathSteps) {
        Collections.reverse(basePathSteps);
        Collections.reverse(filePathSteps);
        List<String> result = new ArrayList<String>(basePathSteps);
        int i = 0;
        while (i < filePathSteps.size() && filePathSteps.get(i).equals("..")) {
            result.remove(result.size() - 1);
            i++;
        }
        for (int j = i; j < filePathSteps.size(); j++) {
            result.add(filePathSteps.get(j));
        }
        StringBuilder resultPath = new StringBuilder();
        for (int k = 0; k < result.size(); k++) {
            resultPath.append(result.get(k));
            if (k != result.size() - 1) {
                resultPath.append(File.separator);
            }
        }
        String resultString = resultPath.toString();
        if (!resultString.startsWith("/")) {
            resultString = "/" + resultString;
        }
        return resultString;
    }
}

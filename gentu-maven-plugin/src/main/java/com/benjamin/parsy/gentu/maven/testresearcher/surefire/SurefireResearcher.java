package com.benjamin.parsy.gentu.maven.testresearcher.surefire;

import com.benjamin.parsy.gentu.maven.testresearcher.ExecutedTestCase;
import com.benjamin.parsy.gentu.maven.testresearcher.TestResearcher;
import com.benjamin.parsy.gentu.maven.testresearcher.TestResearcherException;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Parses Surefire XML reports and returns all test cases found in {@code TEST-*.xml} files.
 */
public class SurefireResearcher implements TestResearcher {

    private final Log log;
    private final Path surefireDirectory;

    /**
     * @param log Maven logger used to report warnings and informational messages
     */
    public SurefireResearcher(Path surefireDirectory, Log log) {
        this.log = log;
        this.surefireDirectory = surefireDirectory;
    }

    private static boolean isSurefireXmlFile(Path path) {
        String name = path.getFileName().toString();
        return name.startsWith("TEST-") && name.endsWith(".xml");
    }

    @Override
    public List<ExecutedTestCase> search() throws TestResearcherException {

        if (!Files.isDirectory(surefireDirectory)) {
            log.info("No Surefire XML reports found in " + surefireDirectory);
            return Collections.emptyList();
        }

        List<Path> xmlFiles;
        try (Stream<Path> stream = Files.list(surefireDirectory)) {
            xmlFiles = stream
                    .filter(SurefireResearcher::isSurefireXmlFile)
                    .toList();
        } catch (IOException e) {
            throw new TestResearcherException("unable to read the surefire folder", e);
        }

        if (xmlFiles.isEmpty()) {
            return Collections.emptyList();
        }

        List<ExecutedTestCase> results = new ArrayList<>();
        for (Path xmlFile : xmlFiles) {
            results.addAll(parseFile(xmlFile));
        }

        return results;
    }

    private List<ExecutedTestCase> parseFile(Path xmlFile) throws TestResearcherException {

        Document doc;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            try (InputStream is = Files.newInputStream(xmlFile)) {
                doc = factory.newDocumentBuilder().parse(is);
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new TestResearcherException("Unable to read the xml file " + xmlFile, e);
        }

        NodeList testCases = doc.getElementsByTagName("testcase");
        List<ExecutedTestCase> results = new ArrayList<>();

        for (int i = 0; i < testCases.getLength(); i++) {

            Element testCase = (Element) testCases.item(i);
            String className = testCase.getAttribute("classname");
            String methodName = extractMethodName(testCase.getAttribute("name"));
            String time = testCase.getAttribute("time");

            results.add(new SurefireExecutedTestCase(className, methodName, time));
        }

        return results;
    }

    /**
     * Strips parameter types appended by JUnit 5: "myMethod(String, int)" → "myMethod"
     */
    private String extractMethodName(String rawName) {
        int idx = rawName.indexOf('(');
        return idx >= 0 ? rawName.substring(0, idx) : rawName;
    }

}

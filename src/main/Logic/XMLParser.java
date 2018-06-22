package main.Logic;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XMLParser {

    public static void parseSettingsFile(String filePath) {
        try {
            Node node = getNode(filePath);
            if (node != null) {
                initiateCore(node);
            } else System.out.println("File is invalid: settings not found");
        } catch (IOException e) {
            System.out.println("File is invalid: some settings are missing");
            Core.getInstance().setText(null);
            Core.getInstance().setPattern(null);
            Core.getInstance().setGrammar(null);
            Core.getInstance().setMode(1);
            Core.getInstance().setAlgorithm("Naive");
        }
    }

    private static Node getNode(String filePath) {
        try {
            File config = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(config);
            doc.getDocumentElement().normalize();
            Node node = doc.getElementsByTagName("settings").item(0);
            return node;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
    }

    public static String getValue(String filePath, String tag) {
        Node node = getNode(filePath);
        if (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
                Node node1 = (Node) nodes.item(0);
                return node1.getNodeValue();
            } else return null;
        } else return null;
    }

    public static void modifyTag(String tag, String filePath, String value) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filePath);

            Node settings = doc.getElementsByTagName("settings").item(0);
            NodeList list = settings.getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {
                Node attribute = list.item(i);
                if (tag.equals(attribute.getNodeName())) {
                    attribute.setTextContent(value);
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initiateCore(Node node) throws IOException {
        Core core = Core.getInstance();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            core.setText(new String(Files.readAllBytes(Paths.get(getValue("text", element)))));
            core.setPattern(TextManipulation.getStringFromFile(new File(getValue("pattern", element))));
            core.setGrammar(GrammarParser.parseGrammarFile(getValue("grammar", element)));
            core.setWordSeparator(getValue("wordSeparator", element));
            core.setAlgorithm(getValue("algorithm", element));
            core.setMode(Integer.parseInt(getValue("mode", element)));
        }
    }
}

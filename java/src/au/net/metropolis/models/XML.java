package au.net.metropolis.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XML {
    private String[] header = {"name", "price", "description", "calories"};
    private Logger logger = Logger.getLogger(getClass().getName());
    private String name;
    private String content;

    public XML(String name, String content) {
        this.name = name;
        this.content = content;
    }

    private Document loadXMLString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    private ArrayList<ArrayList<String>> parse() {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();

        if (this.content != null && !this.content.isEmpty()) {
            try {
                Document doc = loadXMLString(this.content);
                doc.getDocumentElement().normalize();
                NodeList nodes = doc.getElementsByTagName("food");

                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        ArrayList<String> row = new ArrayList<>();
                        Element element = (Element) node;
                        // System.out.println(element.getAttribute("id"));

                        for (String key : this.header) {
                            row.add(element.getElementsByTagName(key).item(0).getTextContent());
                        }
                        rows.add(row);
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        return rows;
    }

    private ArrayList<ArrayList<String>> parseDynamic() {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();

        if (this.content != null && !this.content.isEmpty()) {
            try {
                Document doc = loadXMLString(this.content);
                doc.getDocumentElement().normalize();
                NodeList nodes = doc.getChildNodes();
                // TODO
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        return rows;
    }

    public void show() {
        System.out.println(this.content);
    }

    public CSV toCSV() {
        return new CSV(this.name, this.header, parse());
    }
}

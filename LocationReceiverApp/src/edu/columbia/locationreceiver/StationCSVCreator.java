package edu.columbia.locationreceiver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StationCSVCreator {
	public static void main(String[] args) {
		new StationCSVCreator().createStationsCSV();
	}
	
	public void createStationsCSV() {	
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"src/edu/columbia/locationreceiver/stations.xml"));
			StringBuilder b = new StringBuilder();
			String s = null;
			while((s = reader.readLine()) != null) {
				b.append(s);
			}
			reader.close();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("src/edu/columbia/locationreceiver/stations.csv"));
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(b.toString())));
			if(doc != null) {
				NodeList elem = doc.getElementsByTagName("wx_station_index");
				NodeList children = elem.item(0).getChildNodes();
				for(int i=0;i<children.getLength();i++) {
					Node n = children.item(i);
					if(n.getNodeName().equals("station")) {
						NodeList stationAttr = n.getChildNodes();
						StringBuilder base = new StringBuilder();
						
						for(int j=0; j<stationAttr.getLength(); j++) {
							Node attr = stationAttr.item(j);
							if(attr.getNodeName().equals("station_id")) {
								base.append(attr.getTextContent() + ",");
							} else if (attr.getNodeName().equals("state")) {
								base.append(attr.getTextContent() + ",");
							} else if(attr.getNodeName().equals("latitude")) {
								base.append(attr.getTextContent() + ",");
							} else if(attr.getNodeName().equals("longitude")) {
								base.append(attr.getTextContent());
							}
						}
						writer.write(base.toString() + "\n");
						writer.flush();
						
					}
				}				
			}
			writer.close();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

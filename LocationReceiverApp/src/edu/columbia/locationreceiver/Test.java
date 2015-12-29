package edu.columbia.locationreceiver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class Test {
	public static void main(String[] args) {
		String addr = "http://w1.weather.gov/xml/current_obs/KJFK.xml";
		
		try {
			URL url = new URL(addr);
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String s = null;
			StringBuilder b = new StringBuilder();
			while((s = reader.readLine()) != null) {
				b.append(s);
			}
			System.out.println(b.toString());
			
			String xml = b.toString();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xml)));
			if(doc != null) {
				NodeList elem = doc.getElementsByTagName("current_observation");
				NodeList children = elem.item(0).getChildNodes();
				for(int i=0;i<children.getLength();i++) {
					Node n = children.item(i);
					if(n.getNodeName().equals("pressure_mb")) {
						System.out.println(n.getTextContent());
					}
				}				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}

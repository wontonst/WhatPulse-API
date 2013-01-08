package info.wontonst.whatpulse_api;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Root XML document for a simple XML parse
 * 
 * @author RoyZheng
 */
public class SimpleXML {

	Thread running;// /<the running thread if it's running

	protected volatile boolean ready;// /<xml has been parsed
	protected Document xml;// /< the xml doc

	private Long generatetime;// /<unix timestamp the xml was made

	/**
	 * @brief initializes default variables
	 */
	public SimpleXML() {
		this.ready = false;
	}

	/**
	 * @brief whether or not the xml document is ready to use
	 */
	public boolean isReady() {
		return this.ready;
	}

	/**
	 * @brief user called function to load the xml using a File
	 */
	public void load(InputStream in) {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			System.out.println("Error occurred attempting to create new DocumentBuilder.");
			ex.printStackTrace();
		}
		try {
			xml = dBuilder.parse(in);
			xml.getDocumentElement().normalize();
			ready = true;
			generatetime = System.currentTimeMillis() / 1000;
		} catch (Exception e) {
			System.out.println("An error has occurred while attempting to retrieve XML file");
			e.printStackTrace();
		}
	}

	/**
	 * @brief user called function to load the XML using an asynchronous HTTP
	 *        get
	 */
	public void load(String u) {
		final String url = u;
		this.running = (new Thread(new Runnable() {
			public void run() {
				URL u = null;
				InputStream in = null;
				try {
					u = new URL(url);
				} catch (MalformedURLException ex) {
					Logger.getLogger(SimpleXML.class.getName()).log(Level.SEVERE, null, ex);
				}
				try {
					in = u.openStream();
				} catch (IOException ex) {
					Logger.getLogger(SimpleXML.class.getName()).log(Level.SEVERE, null, ex);
				}
				load(in);
			}
		}));
		this.running.start();
	}

	public void load(File f) {
		final File file = f;
		this.running = (new Thread(new Runnable() {
			public void run() {
				InputStream is = null;
				System.out.println("Start");
				try {
					is = new FileInputStream(file.getPath());
				} catch (FileNotFoundException ex) {
					Logger.getLogger(SimpleXML.class.getName()).log(Level.SEVERE, null, ex);
					return;
				}
				System.out.println("load");
				load(is);
			}
		}));
		this.running.start();
	}

	public SimpleXMLElement get(String tag) {
		NodeList list = this.xml.getElementsByTagName(tag);
		return new SimpleXMLElement((Element) list.item(0));
	}

	/**
	 * @brief a single element of the XML document, used for recursive
	 *        navigation
	 */
	public class SimpleXMLElement {

		Element node;// /< the element being stored

		/**
		 * @brief initializes the object with an element
		 */
		public SimpleXMLElement(Element n) {
			this.node = n;
		}

		/**
		 * @brief returns a child node encapulsated in a SimpleXMLElement
		 */
		public SimpleXMLElement get(String tag) {
			NodeList list = this.node.getElementsByTagName(tag);
			return new SimpleXMLElement((Element) list.item(0));
		}

		/**
		 * @brief returns the current node's tag value If the node does not have
		 *        a tag value because it has child nodes, then weird things that you
		 *        probably won't want will happen.
		 */
		public String toString() {
			return this.node.getChildNodes().item(0).getNodeValue();
		}
	}

	public Long getGenerateTime() {
		return this.generatetime;
	}

	public void sync() {
		try {
			this.running.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "SimpleXML [running=" + running + ", ready=" + ready + ", xml=" + xml + ", generatetime=" + generatetime
				+ "]";
	}

	public static void main(String[] args) {
		SimpleXML x = new SimpleXML();
		System.out.println("Testing FILE INPUT");
		x.load(new File("xmlexample.xml"));
		while (!x.isReady()) {
		}
		System.out.println(x.get("WhatPulse").get("Clicks"));
		x = new SimpleXML();
		System.out.println("TESTING URL INPUT");
		x.load("http://api.whatpulse.org/user.php?user=wontonst");
		while (!x.isReady()) {
		}
		System.out.println(x.get("WhatPulse").get("Clicks"));
	}
}
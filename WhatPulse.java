package info.wontonst.whatpulse_api;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import android.os.StrictMode;
import android.util.Log;

public class WhatPulse {
	String id;
	Document xml;// /<xml document

	Stat clicks;// /<click info
	Stat keys;// /<key info
	Stat download;// /<download info
	Stat upload;// /<upload info

	Double networkratio;// /<download/upload ratio
	String fnetworkratio;// /<download/upload ratio (string formatted)
	Integer network;// /<total network operations in megabytes
	String fnetwork;// /<total network operations in megabytes (string
					// formatted)

	Long age;// /<user account age in seconds
	String fage;// /<user account age in seconds (string formatted)

	Integer uptime;// /<seconds of computer uptime
	Long lastpulse;// /<unix timestamp of last pulse
	Integer lastpulseago;// /<seconds between now and last pulse
	Long join;// /<unix timestamp of join time

	public WhatPulse(String id) throws ParserConfigurationException, SAXException, IOException, ParseException {
		this.id = id;
		this.load();
	}

	public void load() throws ParserConfigurationException, SAXException, IOException, ParseException {
		NumberFormat nf = NumberFormat.getInstance();
		
		///TODO: Very bad noob code to be fixed later, network IO needs to be on separate thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		/////////////////////////////
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		try{
		this.xml = dBuilder.parse("http://whatpulse.org/api/user.php?format=xml&user=" + this.id);
		}catch(Exception e)
		{
			System.out.println("HERE");
			e.printStackTrace();
			Log.v("stack",e.getMessage());
		}
		this.xml.getDocumentElement().normalize();

		NodeList nList = this.xml.getElementsByTagName("WhatPulse");
		Element ele = (Element) nList.item(0);

		// join time/account age
		String join = this.getTagValue("DateJoined", ele);
		SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
		Date d = temp.parse(join);
		this.join = d.getTime() / 1000;
		this.age = System.currentTimeMillis() / 1000 - this.join;
		this.fage = nf.format((age + 0.0) / 86400);
		// lastpulse
		temp = new SimpleDateFormat("yyy-MM-dd kk:mm:ss");
		d = temp.parse(this.getTagValue("LastPulse", ele));
		this.lastpulse = d.getTime() / 1000;
		this.lastpulseago = (int) (System.currentTimeMillis() / 1000 - this.lastpulse);
		// System.out.println(this.lastpulseago);//PRINTS OUT NEGATIVE B/C
		// TIMEZONE ISSUE
		// stats
		this.keys = new Stat(Integer.parseInt(this.getTagValue("Keys", ele)), this.age);
		this.clicks = new Stat(Integer.parseInt(this.getTagValue("Clicks", ele)), this.age);
		this.download = new Stat(Integer.parseInt(this.getTagValue("DownloadMB", ele)), this.age, 3);
		this.upload = new Stat(Integer.parseInt(this.getTagValue("UploadMB", ele)), this.age, 3);

		this.networkratio = (this.download.total() + 0.0) / this.upload.total();
		this.network = this.download.total() + this.upload.total();
		this.fnetwork = nf.format(this.network);
		this.fnetworkratio = nf.format(this.networkratio);
		this.uptime = Integer.parseInt(this.getTagValue("UptimeSeconds", ele)) / 1000;
	}
public Long age()
{
	return this.age;
}
	private String getTagValue(String sTag, Element e) {
		NodeList nlList = e.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
	}

	public static void main(String[] args) throws Exception {

		WhatPulse p = new WhatPulse("wontonst");
System.out.println(p.age);
	}
}

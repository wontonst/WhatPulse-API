package info.wontonst.whatpulse_api;

import info.wontonst.whatpulse_api.SimpleXML.SimpleXMLElement;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class WhatPulse {
	public static boolean debug = true;

	String usr;// /<usrname
	Integer id;// /<usr id
	Integer pulses;// /<number of pulses
	SimpleXML xml;// /<xml document

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
	String fuptime;// /<hours of computer uptime (string formatted)
	Long lastpulse;// /<unix timestamp of last pulse
	Integer lastpulseago;// /<seconds between now and last pulse
	Long join;// /<unix timestamp of join time
	String joindate;// /<string format of join date

	/**
	 * @brief creates the WP object and performs http pull
	 * @param id
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParseException
	 */
	public WhatPulse(String id) throws ParserConfigurationException, SAXException, IOException, ParseException {
		this.usr = id;
		this.load();
	}

	public void load() {
		if (WhatPulse.debug)
			System.out.println("GRABBING DATA FOR USER " + this.usr);
		this.xml = new SimpleXML();
		this.xml.load("http://api.whatpulse.org/user.php?format=xml&user=" + this.usr);
	}

	public void format() throws Exception {
		if (!this.xml.isReady()) {
			throw new Exception("FATAL ERROR: CANNOT format() before sync()!");
		}
		try {
			NumberFormat nf = NumberFormat.getInstance();
			SimpleXMLElement wp = this.xml.get("WhatPulse");
			// join time/account age/id/pulses
			String join = wp.get("DateJoined").toString();
			this.joindate = join;
			SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
			Date d;
			d = temp.parse(join);
			this.join = d.getTime() / 1000;
			this.age = System.currentTimeMillis() / 1000 - this.join;
			this.fage = nf.format((age + 0.0) / 86400);
			this.id = Integer.parseInt(wp.get("UserID").toString());
			this.pulses = Integer.parseInt(wp.get("Pulses").toString());
			// lastpulse
			temp = new SimpleDateFormat("yyy-MM-dd kk:mm:ss");
			d = temp.parse(wp.get("LastPulse").toString());
			this.lastpulse = d.getTime() / 1000;
			this.lastpulseago = (int) (System.currentTimeMillis() / 1000 - this.lastpulse);
			// System.out.println(this.lastpulseago);//PRINTS OUT NEGATIVE B/C
			Long inception = System.currentTimeMillis() / 1000 - 1356156000;
			// this time from when client 2.0 and bandwidth stats were released
			if (inception > this.age)// if the account was created after client 2.0
										// release
				inception = age;
			// TIMEZONE ISSUE
			// stats
			this.keys = new Stat(Integer.parseInt(wp.get("Keys").toString()), this.age);
			this.clicks = new Stat(Integer.parseInt(wp.get("Clicks").toString()), this.age);
			this.download = new Stat(Integer.parseInt(wp.get("DownloadMB").toString()), inception, 3);
			this.upload = new Stat(Integer.parseInt(wp.get("UploadMB").toString()), inception, 3);

			this.networkratio = (this.upload.total() == 0) ? 0 : (this.download.total() + 0.0) / this.upload.total();
			this.network = this.download.total() + this.upload.total();
			this.fnetwork = nf.format(this.network);
			this.fnetworkratio = nf.format(this.networkratio);
			this.uptime = Integer.parseInt(wp.get("UptimeSeconds").toString());
			this.fuptime = nf.format((this.uptime + 0.0) / 3600);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String getTitle() {
		return this.usr + " (id " + this.id + ")";
	}

	/**
	 * @brief Top-left information display box
	 * @return string with information to display
	 */
	public String getTop1() {
		return "Keys: " + this.keys.formattedTotal() + "\n" + this.keys.formattedMinute() + "/minute" + "\n"
				+ this.keys.formattedHour() + "/hour" + "\n" + this.keys.formattedDay() + "/day";

	}

	/**
	 * @brief Top-right information display box
	 * @return string with information to display
	 */
	public String getTop2() {
		return "Clicks: " + this.clicks.formattedTotal() + "\n" + this.clicks.formattedMinute() + "/minute" + "\n"
				+ this.clicks.formattedHour() + "/hour" + "\n" + this.clicks.formattedDay() + "/day";
	}

	/**
	 * @brief Bottom-left information display box
	 * @return string with information to display
	 */
	public String getBottom1() {
		return "Download: " + this.download.formattedTotal() + "MB" + "\n" + this.download.formattedMinute()
				+ "/minute" + "\n" + this.download.formattedHour() + "/hour" + "\n" + this.download.formattedDay()
				+ "/day" + "\nNetwork: " + this.fnetwork + "MB" + "\n" + this.fnetworkratio + " D/U ratio";
	}

	/**
	 * @brief Bottom-left information display box
	 * @return string with information to display
	 */
	public String getBottom2() {
		return "Upload: " + this.upload.formattedTotal() + "MB" + "\n" + this.upload.formattedMinute() + "/minute"
				+ "\n" + this.upload.formattedHour() + "/hour" + "\n" + this.upload.formattedDay() + "/day"
				+ "\nPulses: " + this.pulses + "\nUptime: " + this.fuptime + " hours";
		// + "\nDate joined: " + this.joindate;
	}

	public Long getAge() {
		return this.age;
	}

	public Integer getID() {
		return this.id;
	}

	public String getName() {
		return this.usr;
	}

	public Integer getPulses() {
		return this.pulses;
	}

	public Stat getClicks() {
		return this.clicks;
	}

	public Stat getKeys() {
		return keys;
	}

	public Stat getDownload() {
		return download;
	}

	public Stat getUpload() {
		return upload;
	}

	@Override
	public String toString() {
		return "WhatPulse [usr=" + usr + ", id=" + id + ", pulses=" + pulses + ", xml=" + xml + ", clicks=" + clicks
				+ ", keys=" + keys + ", download=" + download + ", upload=" + upload + ", networkratio=" + networkratio
				+ ", fnetworkratio=" + fnetworkratio + ", network=" + network + ", fnetwork=" + fnetwork + ", age="
				+ age + ", fage=" + fage + ", uptime=" + uptime + ", lastpulse=" + lastpulse + ", lastpulseago="
				+ lastpulseago + ", join=" + join + "]";
	}

	public Long getGenerateTime() {
		return this.xml.getGenerateTime();
	}

	public void sync() {
		this.xml.sync();
	}

	public static void main(String[] args) throws Exception {

		WhatPulse p = new WhatPulse("wontonst");
		p.load();
		Thread.sleep(2500);
		p.format();
		System.out.println(p + "");
	}
}

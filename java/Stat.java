package info.wontonst.whatpulse_api;

import java.text.NumberFormat;

public class Stat {
	Integer total;
	Double perminute;
	Double perhour;
	Double perday;

	String ftotal;
	String fperminute;
	String fperhour;
	String fperday;

	public Stat(Integer num, Long totaltime, Integer precision) {
		this.total = num;
		this.calculate(totaltime);
		this.format(precision);
	}

	public Stat(Integer num, Long totaltime) {
		this.total = num;
		this.calculate(totaltime);
		this.format(2);
	}

	public void calculate(Long time) {
		this.perminute = (this.total + 0.0) / (time / 60);
		this.perhour = (this.total + 0.0) / (time / 3600);
		this.perday = (this.total + 0.0) / (time / 86400);
	}

	private void format(Integer precision) {
		double pm = this.perminute, ph = this.perhour, pd = this.perday;
		for (int i = 0; i != precision; i++) {
			pm *= 10;
			ph *= 10;
			pd *= 10;
		}
		pm = (double) ((int) pm);
		ph = (double) ((int) ph);
		pd = (double) ((int) pd);
		for (int i = 0; i != precision; i++) {
			pm = pm / 10;
			ph = ph / 10;
			pd = pd / 10;
		}
		NumberFormat nf = NumberFormat.getInstance();
		this.ftotal = nf.format(this.total);
		this.fperminute = nf.format(pm);
		this.fperhour = nf.format(ph);
		this.fperday = nf.format(pd);
	}

	public void reformat(Integer precision) {
		this.format(precision);
	}

	public Integer total() {
		return this.total;
	}

	public Double minute() {
		return this.perminute;
	}

	public Double hour() {
		return this.perhour;
	}

	public double day() {
		return this.perday;
	}

	public String formattedTotal() {
		return this.ftotal;
	}

	public String formattedMinute() {
		return this.fperminute;
	}

	public String formattedHour() {
		return this.fperhour;
	}

	public String formattedDay() {
		return this.fperday;
	}

	@Override
	public String toString() {
		return "Stat [total=" + total + ", perminute=" + perminute + ", perhour=" + perhour + ", perday=" + perday
				+ ", ftotal=" + ftotal + ", fperminute=" + fperminute + ", fperhour=" + fperhour + ", fperday="
				+ fperday + "]";
	}
}

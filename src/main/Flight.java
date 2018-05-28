package main;
import java.text.ParseException;
import java.text.SimpleDateFormat;

// Airline Travel Scheduler - Flight
// Bongki Moon (bkmoon@snu.ac.kr)

public class Flight {
	String src;
	String dest;
	String stime;
	String dtime;
	long duration;
	
	// constructor
	public Flight(String src, String dest, String stime, String dtime) {
		this.src = src;
		this.dest = dest;
		this.stime = stime;
		this.dtime = dtime;
		this.duration = TimeUtil.timediff(stime, dtime);
	}

	public void print() {
	}

	public String getSrc() {
		return src;
	}

	public String getDest() {
		return dest;
	}

	public String getStime() {
		return stime;
	}

	public String getDtime() {
		return dtime;
	}

	public long getDuration() {
		return duration;
	}

	@Override
	public String toString() {
		return "Flight [src=" + src + ", dest=" + dest + ", stime=" + stime + ", dtime=" + dtime + ", duration="
				+ duration + "]";
	}
}

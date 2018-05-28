package main;

// Airline Travel Scheduler - Airport
// Bongki Moon (bkmoon@snu.ac.kr)

public class Airport {
	String port;
	String sconnectTime;
	long connectTime;
	
	// constructor
	public Airport(String port, String connectTime) {
		this.port = port;
		this.sconnectTime = connectTime;
		this.connectTime = TimeUtil.timediff("0000", connectTime);
	}
	
	public void print() {
	}
	
	public String getPort() {
		return this.port;
	}
	
	public long getConnectTime() {
		return this.connectTime;
	}
	
	public String getSconnectTime() {
		return this.sconnectTime;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Airport)) {
			return false;
		}
		return ((Airport) o).getPort().equals(this.port); 
	}
	
	@Override
	public int hashCode() {   
		return port.hashCode();
	}

	@Override
	public String toString() {
		return "Airport [port=" + port + ", connectTime=" + connectTime + ", sconnectTime=" + sconnectTime + "]";
	}
	
	
}

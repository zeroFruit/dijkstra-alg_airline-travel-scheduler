package main;
// Airline Travel Scheduler - Itinerary
// Bongki Moon (bkmoon@snu.ac.kr)

public class Itinerary {
	Distance distance;
	Itinerary(Distance distance) {
		this.distance = distance;
	}

	public boolean isFound() {
		if (distance == null) {
			return false;
		} else {
			return true;
		}
	}

	public void print() {
		if (distance == null) {
			System.out.println("No Flight Schedule Found.");
			return;
		}
	
		for (Flight flight  : distance.getPath()) {
			System.out.print("[" + flight.getSrc() + "->" + flight.getDest() + ":" + flight.getStime() + "->" + flight.getDtime() + "]");
		}
		System.out.println();
	}

}

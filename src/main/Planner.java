package main;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Airline Travel Scheduler - Planner
// Bongki Moon (bkmoon@snu.ac.kr)

public class Planner {
	Map<Airport, ArrayList<Flight>> portGraph = new HashMap<Airport, ArrayList<Flight>>();
	Map<String, Airport> portMap = new HashMap<String, Airport>();
	Map<String, Distance> dMap = new HashMap<>(); 	/* Save result */
	MinHeap<Distance> dHeap = new MinHeap<>(); 	/* For updating Edges */
	static Set<String> S = new HashSet<>();
	int portSize;
	
	static boolean isSetSContainsPort(String port) {
		return S.contains(port);
	}
	
	public Planner(LinkedList<Airport> portList, LinkedList<Flight> fltList) {
		init(portList, fltList);
	}
	
	private void init(LinkedList<Airport> portList, LinkedList<Flight> fltList) {
		/* initialize Vertex, dMap */
		for (Airport port : portList) {
			portGraph.put(port, new ArrayList<Flight>());
			portMap.put(port.getPort(), port);
			dMap.put(port.getPort(), new Distance(new ArrayList<Flight>(), Long.MAX_VALUE));
		}
		
		/* initialize Edge */
		for (Flight flight : fltList) {
			String src = flight.getSrc();
			Airport port = portMap.get(src);
			ArrayList<Flight> fList = portGraph.get(port);
			fList.add(flight);
			portGraph.put(port, fList);
		}
		this.portSize = portMap.size();
	}
	

	public Itinerary Schedule(String start, String end, String departure) {
		reset();
		Distance distance = null;
		boolean initSuccess = initSchedule(start, departure);
		
		if(!initSuccess)
			return new Itinerary(null);
		
		while (S.size() != this.portSize) {
			Distance shortestDistance = dHeap.remove();
			while (S.contains(shortestDistance.getDest())) {
				shortestDistance = dHeap.remove();
			}
			
			if (shortestDistance.getDest().equals(end)) {
				distance = shortestDistance;
				break;
			}

			S.add(shortestDistance.getDest());
			
			updateFringeEdges(shortestDistance, end);
		}
		return new Itinerary(distance);
	}
	
	private void reset() {
		S.clear();
		
		dHeap.clear();
		
		for (String key : dMap.keySet()) {
			dMap.put(key, new Distance(new ArrayList<Flight>(), Long.MAX_VALUE));
		}
	}
	
	private void updateFringeEdges(Distance shortestDistance, String end) {
		Airport selectedPort 		= portMap.get(shortestDistance.getDest());
		ArrayList<Flight> fList 	= portGraph.get(selectedPort);
		UpdateCheckBoard board 		= new UpdateCheckBoard();
		String timeAfterConnection = TimeUtil.timeadd(shortestDistance.getLastArrivalTime(), selectedPort.getSconnectTime());

		for (Flight flight : fList) {
			String dest = flight.getDest();
			ArrayList<Flight> shortestDistancePath = new ArrayList<>(shortestDistance.getPath());

			long w = 
					TimeUtil.timediff(timeAfterConnection, flight.getStime()) + 	/* arrival ~ src */
					selectedPort.getConnectTime() +  				/* src connect time */
					flight.getDuration();							/* src ~ dest */
			
			if (!S.contains(dest)) { /* should not update Vertex in set S */
				if (dMap.containsKey(dest)) {
					Distance d = new Distance(dMap.get(dest).getPath(), dMap.get(dest).getMinutes());
					if (d.hasNoPath()) { /* not directly connected with shortestDistance's src */
						shortestDistancePath.add(flight);
						d.setMinutes(shortestDistance.getMinutes() + w);
						d.setPath(shortestDistancePath);
						
						dMap.put(dest, d);
						dHeap.insert(d);
						board.check(dest); /* check to board this Vertex is inserted path */
					}
					else { /* already connected with shortestDistance's src */
						if (shortestDistance.getMinutes() + w < d.getMinutes()) {
							d.setMinutes(shortestDistance.getMinutes() + w);
							if (board.isChecked(dest)) {
								d.updatePath(flight);
							} else {
								shortestDistancePath.add(flight);
								d.setPath(shortestDistancePath);
							}
							
							dMap.put(dest, d);
							dHeap.insert(d);
						}
					}
					
				} 
			}
		}
	}
	
	private boolean initSchedule(String start, String departure) {
		Airport port = portMap.get(start);
		ArrayList<Flight> fList = portGraph.get(port);
		if (fList == null) {
			return false;
		}
		/* start point */
		S.add(start);

		/* the points connected with start point */
		for (Flight flight : fList) {
			String dest = flight.getDest();
			Distance d = new Distance(dMap.get(dest).getPath(), dMap.get(dest).getMinutes());
			long minutes = 
					TimeUtil.timediff(departure, flight.getStime()) + 	/* departure ~ src */
					port.getConnectTime() +  					/* src connect time */
					flight.getDuration();						/* src ~ dest */
			
			if (dMap.containsKey(dest)) {
				if (d.hasNoPath()) {
					d.setMinutes(minutes);
					d.addPath(flight);
					dMap.put(dest, d);
					dHeap.insert(d);
				}
				else {
					if(minutes < d.getMinutes()) { /* if minutes are less than old value replace */
						d.setMinutes(minutes);
						d.updatePath(flight);
						dMap.put(dest, d);
						dHeap.insert(d);
					}
				}
				
			} 
		}
		
		return true;
	}
	
	public void print() {
		for (Airport port : portGraph.keySet()) {
			System.out.println(port);
			ArrayList<Flight> fHeap = portGraph.get(port);
			for (Flight flight : fHeap) {
				System.out.println(flight);
			}
			System.out.println();
		}
	}
	
	public void printDMap() {
		for (String dest : dMap.keySet()) {
			Distance distance = dMap.get(dest);
			System.out.println("dest=" + dest + ", distance=" + distance);
		}
	}
	
	public void printDHeap() {
		dHeap.print();
	}
	
	public void printSSet() {
		for (String string : S) {
			System.out.println(string + ", ");
		}
		System.out.println();
	}

}

class Distance implements Comparable<Distance> {
	private List<Flight> path;
	private long minutes;
	
	Distance(Flight flight, long minutes) {
		this.path = new ArrayList<>();
		this.path.add(flight);
		this.minutes = minutes;
	}
	Distance(List<Flight> path, long minutes) {
		this.path = path;
		this.minutes = minutes;
	}
	
	public String getSrc() {
		return path.get(0).getSrc();
	}
	public String getDest() {
		return path.get(path.size()-1).getDest();
	}
	public String getLastArrivalTime() {
		return path.get(path.size()-1).getDtime();
	}
	
	public void insertPath(Flight flight) {
		path.add(path.size()-1, flight);
		
	}
	
	public void setPath(List<Flight> path) {
		this.path = path;
	}
	
	public void addPath(Flight flight) {
		path.add(flight);
	}
	
	public void updatePath(Flight flight) {
		path.set(path.size() - 1, flight);
	}
	
	public List<Flight> getPath() {
		return this.path;
	}
	
	public boolean hasNoPath() {
		return this.path.size() == 0;
	}
	
	public long getMinutes() {
		return minutes;
	}
	public void setMinutes(long minutes) {
		this.minutes = minutes;
	}
	@Override
	public String toString() {
		return "Distance [path=" + path + ", minutes=" + minutes + "]";
	}
	@Override
	public int compareTo(Distance d) {
		if (path.size() == 0) /* if path is empty */ 
			return 1; 
		if (minutes < d.getMinutes()) {
			return -1;
		} else if (minutes > d.getMinutes()) {
			return 1;
		} else {
			return getDest().compareTo(d.getDest());
		}
	}
}

class UpdateCheckBoard {
	HashMap<String, Integer> board;
	public UpdateCheckBoard() {
		board = new HashMap<>();
	}
	public void check(String port) {
		board.put(port, 1);
	}
	public boolean isChecked(String port) {
		if (!board.containsKey(port)) {
			return false;
		}
		return true;
	}
}

class MinHeap<E extends Distance> {
	private ArrayList<E> heap;
	
	MinHeap() {
		heap = new ArrayList<>();
	}
	
	public void insert(E item) {
		heap.add(item);
		int i = heap.size() - 1;
		int parent = parent(i);
		
		while (parent != i && heap.get(i).compareTo(heap.get(parent)) < 0) {
			swap(i, parent);
			i = parent;
			parent = parent(i);
		}
	}
	
	public E remove() {
		E root;
		if (size() == 0) 
			return null;
		if (size() == 1) {
			root = heap.remove(0);
			return root;
		}
		
		root = heap.get(0);
		E lastItem = heap.remove(size() - 1);
		heap.set(0, lastItem);
		
		heapifyDown(0);
		
		return root;
	}
	
	private void heapifyDown(int i) {
		int left = left(i);
		int right = right(i);
		int smallest = i;
		if (left < size() && heap.get(left).compareTo(heap.get(i)) < 0) {
			smallest = left;
		}
		if (right < size() && heap.get(right).compareTo(heap.get(smallest)) < 0) {
			smallest = right;
		}
		if (smallest != i) {
			swap(i, smallest);
			heapifyDown(smallest);
		}
	}
	public void print() {
		for (int i = 0; i < heap.size(); i++) {
			System.out.println("i=" + i + ", dest=" + heap.get(i).getDest() + ", value=" + heap.get(i));
		}
	}
	
	public boolean validate() {
		for (int i = 1; i < heap.size(); i++) {
			if (heap.get(i).compareTo(heap.get(parent(i))) < 0) {
				print();
				return false;
			}
		}
		print();
		return true;
	}
	
	public int size() {
		return heap.size();
	}
	
	public void clear() {
		heap.clear();
	}
	
	private int parent(int i) {
		if (i == 0) { /* if i is already a root node */
			return 0;
		}
		return (i - 1) / 2;
	}
	
	private int left(int i) {
		return (2 * i + 1);
	}
	
	private int right(int i) {
		return (2 * i + 2);
	}
	
	private void swap(int i, int parent) {
		E tmp = heap.get(parent); 
		heap.set(parent, heap.get(i));
		heap.set(i, tmp);
	}
}

class TimeUtil {
	static private SimpleDateFormat formatter = new SimpleDateFormat("HHmm");
	static private Calendar c = Calendar.getInstance();
	
	static public long timediff(String stime, String dtime) {
		int mindiff, hourdiff;
		int shour = Integer.parseInt(stime.substring(0, 2));
		int smin = Integer.parseInt(stime.substring(2));
		int dhour = Integer.parseInt(dtime.substring(0, 2));
		int dmin = Integer.parseInt(dtime.substring(2));
		
		if (dmin - smin < 0) {
			mindiff = dmin - smin + 60;
			dhour -= 1;
		} else {
			mindiff = dmin - smin;
		}
		
		if (dhour - shour < 0) {
			hourdiff = dhour - shour + 24; 
		} else {
			hourdiff = dhour - shour; 
		}
		
		return (hourdiff * 60) + mindiff;
	}
	
	static public String timeadd(String stime, String dtime) {
		try {
			String shours, sminutes;
			Date sDate = formatter.parse(stime);
			Date dDate = formatter.parse(dtime);
			
			int minutes = (sDate.getMinutes() + dDate.getMinutes());
			int overflow = minutes / 60;
			minutes %= 60;
			int hours = (sDate.getHours() + dDate.getHours() + overflow) % 24;
			
			if (minutes < 10) sminutes = "0" + String.valueOf(minutes);
			else sminutes = String.valueOf(minutes);
			
			if (hours < 10) shours = "0" + String.valueOf(hours);
			else shours = String.valueOf(hours);
			
			return shours + sminutes;
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		
	}
}









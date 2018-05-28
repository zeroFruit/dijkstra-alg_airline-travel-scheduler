# airline-travel-scheduler

Implement Dijkstra’s single-source shortest path algorithm to develop an airline travel scheduler. The airline travel scheduler will find an itinerary (i.e., a sequence of flights) that allows one to depart from an origin airport and arrive at a destination airport at the earliest possible time (i.e., the shortest travel time).

## Problem Spec

* Given the information from a traveler, the scheduler will find a sequence of flights that allow him/her to arrive at the final destination at the earliest possible time when departing from the origin at or after the earliest departure time. Minimum connection times at intermediate airports (i.e., excluding the origin and destination airports) should be observed. Airline travelers are willing to take a next-day flight if no same-day flight is available or the next-day flight allows them to arrive at the final destination earlier. Do not assume that an itinerary will always be found for any given origin and destination, because it may not exist at all.
* Airport names are encoded in the standard 3-letter symbols (e.g., ICN for Seoul-Incheon). There may be an airport with outgoing or incoming flights only. All flight times are given in the 24-hour notation, also referred to as military time (e.g., 1330 for 1:30pm).1 Assume that the international community has grown tired of different time zones and abolished the whole concept.
* Your code is expected to be an Airline Travel Scheduler ofproduction quality. This implies that your scheduler should be both efficient and scalable. You should be able to produce an itinerary quickly for hundreds of airports and thousands of flights without increasing the initial memory allocation of JVM running on a Linux platform (i.e., with neither Xms nor Xmx option).

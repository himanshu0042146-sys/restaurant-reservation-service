package com.shuru.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

}
/*
* Map<timeSlot: List<availbaleTables>>
* */

/*
* Restaurant
*   id
*   Table
*       guestOccupancy
*
*
*   timeSlot
*   reservationTime
* */

/*
Customer
id, name
* */

/*
* availbaleTimeSlot:
* date:
*
* */


/*
* guestCount:
* timeslot:
*
* */
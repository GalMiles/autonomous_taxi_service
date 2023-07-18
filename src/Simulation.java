import java.util.*;

public class Simulation {
    private static final int INTERVAL = 20000; // Interval in milliseconds
    private int simulationDuration = 0; // Duration in miliseconds
    private static final int NUM_OF_TAXIS = 10;

    Taxi[] taxis;
    Queue<Ride> orderingQueue;
    Timer timer;

    Simulation() {
        taxis = new Taxi[NUM_OF_TAXIS];
        orderingQueue = new LinkedList<Ride>();
        timer = new Timer();
    }

    void initialTaxis() {

        //generate random (x,y) coordinates to every taxi between 20km x 20km
        for (int i = 0; i < NUM_OF_TAXIS; i++) {
            Coordinate randomCoordinate = Coordinate.generateRandomCoordinate();
            taxis[i] = new Taxi(randomCoordinate, i+1);
        }
        System.out.println("Initial taxis locations:");
        printTaxis();
        System.out.println("\n");
    }

    void startSimulator() {
        //timer for every 20 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                simulationDuration = simulationDuration + INTERVAL;
                System.out.println("After "+ (simulationDuration/1000) + " seconds:");

                //add new ride request to queue with random coordinate
                Coordinate startCoordinate = Coordinate.generateRandomCoordinate();
                Coordinate endCoordinate = Coordinate.generateRandomCoordinate();

                //check if the distance is less than 2km
                while(!isDistanceValid(startCoordinate, endCoordinate)) {
                    endCoordinate = Coordinate.generateRandomCoordinate();
                }

                Ride newRide = new Ride(startCoordinate, endCoordinate);
//                System.out.println("New ride request: " + newRide.toString());

                //add ride request to queue
                orderingQueue.add(newRide);
                //peek ride request so if there is no taxi available it will stay in queue
                Ride rideRequest = orderingQueue.peek();

                //allocate taxi for ride request
                Taxi closestTaxi = findClosestTaxi(rideRequest);
                if(closestTaxi != null) {
                    rideRequest = orderingQueue.poll();
                    closestTaxi.ride = rideRequest;
                    closestTaxi.available = false;
                    closestTaxi.isStanding = false;
                    closestTaxi.drivingToStart = true;
//                    System.out.println("Taxi " + closestTaxi.id + " is allocated for ride request ");
                }
                //update taxi state
                updateTaxiState();

                //print ordering queue
                System.out.println("Ordering queue: " + orderingQueue.toString());

                //print current state of all taxis
                System.out.println("Taxi locations:");
                printTaxis();
                System.out.println("\n");
            }
        }, 0, INTERVAL);
    }

    void updateTaxiState() {
        for(Taxi taxi : taxis) {
            if(!taxi.isStanding) {
                //if taxi is not standing and not riding to start point, it's riding to end point of request
                if(!taxi.drivingToStart) {
                    taxi.drivingByRequest = true;
                }

                //check where taxi riding to? start point or end point?
                //taxi is riding to start point
                if(taxi.drivingToStart) {
                    //arrived to x
                    if (taxi.position.x == taxi.ride.start.x) {
                        if(taxi.position.y != taxi.ride.start.y) {
                            if (taxi.position.y > taxi.ride.start.y) {
                                taxi.direction = Direction.DOWN;
                            }
                            else {
                                taxi.direction = Direction.UP;
                            }
                        }
                    }
                    //not arrived to x
                    else {
                        if (taxi.position.x > taxi.ride.start.x) {
                            taxi.direction = Direction.LEFT;
                        }
                        else {
                            taxi.direction = Direction.RIGHT;
                        }
                    }
                    move(taxi, taxi.ride.start);
                }

                //taxi is riding to end point
                else {
                    //check direction
                    //arrived to x point
                    if (taxi.ride.end.x == taxi.position.x){
                        if (taxi.ride.end.y == taxi.position.y){ //arrived to (x,y) point-finished
                            taxi.isStanding = true;
                            taxi.drivingByRequest = false;
                            taxi.available = true;
                            return;
                        }
                        //arrived to x point but not to y point
                        else{
                            if (taxi.ride.end.y < taxi.position.y) {
                                taxi.direction = Direction.DOWN;
                            } else {
                                taxi.direction = Direction.UP;
                            }
                        }
                    }
                    //didn't arrive to x point yet
                    else{
                        if (taxi.ride.end.x < taxi.position.x) {
                            taxi.direction = Direction.LEFT;
                        }
                        else {
                            taxi.direction = Direction.RIGHT;
                        }
                    }
                    move(taxi, taxi.ride.end);
                }
            }
        }
    }

    //move taxi to destination
    void move(Taxi taxi, Coordinate destination) {
        //calculate movement
        if(taxi.direction == Direction.RIGHT) {
            //taxi should move and stop after this move
            if(taxi.position.x + 0.4 >= destination.x) {
                taxi.position.x  = destination.x;

                //change direction if needed
                if(taxi.position.y < destination.y) {
                    taxi.direction = Direction.UP;
                }
                else {
                    taxi.direction = Direction.DOWN;
                }
            }
            //taxi continue moving right
            else {
                taxi.position.x += 0.4;
            }
        }
        else if(taxi.direction == Direction.LEFT) {
            //taxi should move and stop after this move
            if(taxi.position.x - 0.4 <= destination.x) {
                taxi.position.x  = destination.x;

                //change direction if needed
                if(taxi.position.y < destination.y) {
                    taxi.direction = Direction.UP;
                }
                else {
                    taxi.direction = Direction.DOWN;
                }
            }
            //taxi continue moving left
            else {
                taxi.position.x -= 0.4;
            }
        }
        else if(taxi.direction == Direction.UP) {
            //taxi should move and stop after this move
            if(taxi.position.y + 0.4 >= destination.y) {
                taxi.position.y  = destination.y;
                taxi.drivingToStart = false;
            }
            //taxi continue moving up
            else {
                taxi.position.y += 0.4;
            }
        }
        else if(taxi.direction == Direction.DOWN) {
            //taxi should move and stop
            if(taxi.position.y - 0.4 <= destination.y) {
                taxi.position.y  = destination.y;
                taxi.drivingToStart = false;
            }
            //taxi continue moving down
            else {
                taxi.position.y -= 0.4;
            }
        }
    }

    void stopSimulator() {
        timer.cancel();
    }

    Taxi findClosestTaxi(Ride request) {
        double minDistance = Integer.MAX_VALUE;
        Taxi res = null;

        //iterate over available taxis and find the closest one to the start position
        for(Taxi taxi : taxis) {
            if(taxi.available) {
                //calculate distance between taxi and start position
                double distance = calcDistanceBetweenCoordinates(taxi.position, request.start);
                if(distance < minDistance) {
                    minDistance = distance;
                    res = taxi;
                }
            }
        }
        return res;
    }

    void printTaxis() {
        for(int i = 0; i < NUM_OF_TAXIS; i++) {
            System.out.println("Taxi  "+ (i+1) + ": " + taxis[i].toString());
        }
    }

    boolean isDistanceValid(Coordinate startCoordinate, Coordinate endCoordinate) {
        double distance = calcDistanceBetweenCoordinates(startCoordinate, endCoordinate);
        return distance <= 2;
    }

    double calcDistanceBetweenCoordinates(Coordinate startCoordinate, Coordinate endCoordinate) {
        return Math.sqrt(Math.pow((endCoordinate.x - startCoordinate.x), 2) + Math.pow((endCoordinate.y - startCoordinate.y), 2));
    }



}

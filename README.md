# Bus Route Challenge

### Problem
Service that will answer if two given stations are connected by a bus route.
A file in the form of a **bus route data file** is given as input

### Bus Route Data File

The bus route data file provided by the bus provider contains a list of bus
routes. These routes consist of an unique identifier and a list of stations
(also just unique identifiers). A bus route **connects** its list of stations.

The first line of the data gives you the number **N** of bus routes, followed by
**N** bus routes. For each bus route there will be **one** line containing a
space separated list of integers. This list contains at least three integers. The
**first** integer represents the bus **route id**. The bus route id is unique
among all other bus route ids in the input. The remaining integers in the list
represent a list of **station ids**. A station id may occur in multiple bus
routes, but can never occur twice within the same bus route.

Assuming 100,000 as upper limit for the number of bus routes, 1,000,000 as
upper limit for the number of stations, and 1,000 as upper limit for the number
of station of one bus route. Therefore, the internal data structure should
still fit into memory on a suitable machine.

*Note: The bus route data file will be a local file and your service will get
the path to file as the first command line argument. The service will get
restarted if the file or its location changes.*


### REST API

The micro service implements a REST-API supporting a single URL and only
GET requests. It serves
`http://localhost:8088/api/direct?dep_sid={}&arr_sid={}`. The parameters
`dep_sid` (departure) and `arr_sid` (arrival) are two station ids (sid)
represented by 32 bit integers.

The response is a single JSON Object:

```
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "type": "object",
  "properties": {
    "dep_sid": {
      "type": "integer"
    },
    "arr_sid": {
      "type": "integer"
    },
    "direct_bus_route": {
      "type": "boolean"
    }
  },
  "required": [
    "dep_sid",
    "arr_sid",
    "direct_bus_route"
  ]
}
```

The `direct_bus_route` field has to be set to `true` if there exists a bus route
in the input data that connects the stations represented by `dep_sid` and
`arr_sid`. Otherwise `direct_bus_route` must be set to `false`.




### Example Data

Bus Routes Data File:
```
3
0 0 1 2 3 4
1 3 1 6 5
2 0 6 4
```

Query:
````
http://localhost:8088/api/direct?dep_sid=3&arr_sid=6
```

Response:
```
{
    "dep_sid": 3,
    "arr_sid": 6,
    "direct_bus_route": true
}
```


### Implementation

The solution is implemented in Java 8 using spring boot and maven.

### How to run it

Simply run
```
mvn clean package
```
and
```
java -jar target/bus-route-1.0-SNAPSHOT.jar /path/to/the/bus/route/data/file

```

Or else run
```
bash ./build.sh
```
to build and package the application and then
```
bash ./service.sh start|stop|block /path/to/the/bus/route/data/file
```

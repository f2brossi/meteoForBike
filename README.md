# meteoForBike
Android app that displays the rain forecast over the next hour ( minute by minute ) along your ride.
The goal is to have a very fast and simple application for preparing your ride.
For now, limited to Paris and its suburban. 

see in attachment under /doc the reference document bdclim-stations-france-20072408.ods from meteo-france, referencing all the station codes, city by city, used to retrieve the information with the endpoint:

"http://www.meteofrance.com/mf3-rpc-portlet/rest/pluie/stationcode"

Information is available only for none mountainous zones due to the radar technology used.

## Features to come:
- Extend the coverage with sqllite to cover 75% of the french population ( none mountainous zones ).
- Geolocation without gps to determine the start location.
- Add a satellite view around the path. 
- Add for paris and idf : ratp traffic, sytadin, velib station status. 



**Note:** This project was originally forked from https://github.com/caarmen/MeteoFranceDemo.git and powered by meteo-france and nominatim.openstreetmap.

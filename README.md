# Apache Ignite Market Orders Streaming Demo

The demo starts a simple application that simulates trading activities of a stock exchange. 
The application receives simulated market orders in real-time via the following data providor:

[PubNub Market Order Data Stream]
(https://www.pubnub.com/developers/realtime-data-streams/financial-securities-market-orders/)

The received data is subsequently forwarded to a locally running Ignite cluster.

The demo was created for the Control Center 

[written tutorial](https://www.gridgain.com/docs/tutorials/management-monitoring/overview) 

and 

[instructor-led foundation course](https://www.gridgain.com/products/services/training/how-monitor-and-manage-apache-ignite-gridgain-control-center).

## Dependencies

This demo is configured to execute against GridGain Ultimate Edition version 8.9.0  

## Changes for your Environment

You will need to change the snapshot path in the ignite-config-node.xml  
file to a valid path for your system before starting your server nodes.  

You will need to obtain a demo license and change the license file  
location to a valid path for your environment in the ignite-config-node.xml  
file before starting your server nodes.  

## Running for your Environment
Launch one or more server nodes each in its own command window.  
The Cluster node configurations enable native persistence, open census
API, snapshots, ...  

## Program Arguments  
The program supports 2 command line arguments described as follows:  
execTime=x where x is the desired number of minutes to run the program for  
If the execTime argument is not supplied then the default execTime is 15 minutes  

execCount=y where y is the minimum number of Trade records to save   
Note that if the execCount argument is supplied then this argument overrides the    
execTime argument and the system will check the saved records every 30 seconds.
The program will stop after y number of records or more have been saved.  

## Example Invocations
Example invocations are listed below (windows):  
java -cp .\target\ignite-streaming-app.jar org.gridgain.demo.StreamingApplication  
java -cp .\target\ignite-streaming-app.jar org.gridgain.demo.StreamingApplication execTime=10  
java -cp .\target\ignite-streaming-app.jar org.gridgain.demo.StreamingApplication execCount=1000  

Example invocations are listed below (linux):  
java -cp ./target/ignite-streaming-app.jar org.gridgain.demo.StreamingApplication  
java -cp ./target/ignite-streaming-app.jar org.gridgain.demo.StreamingApplication execTime=10 
java -cp ./target/ignite-streaming-app.jar org.gridgain.demo.StreamingApplication execCount=1000  



Owlet monitor service.
=======================================

Overview
--------
This project is a service that monitors the endpoint of your website or API and sends a PING to check your page status. If it detects a failure, it will send a fall message to your Discord channel through webhook.

How to configure
--------
First, the config.properties file must be in the same directory where the run command will be executed.
Below we have a table with an explanation of all properties.

Property                                                | Data type | Description                                                                                                                    
--------------------------------------------------------|-----------|--------------------------------------------------------------------------------------------------------------------------------
notify.service.ping.url                                 | URL       | URL where the ping request will be sent.                                                                                       
notify.service.ping.httpMethod                          | String    | The HTTP method used to send the ping request. The default value is POST.                                                      
notify.service.ping.sendPayload                         | Boolean   | Indicates if the payload will be sent in the body of the ping request. The default value is true                               
notify.service.fall.discord.webhook.url                 | URL       | The webhook URL generated by Discord.                                                                                          
notify.service.fall.discord.webhook.username            | String    | Custom username that will be sent in the message.                                                                              
notify.service.fall.discord.webhook.avatarUrl           | URL       | The avatar URL for the user. The URL should be an image resource.                                                              
notify.service.fall.discord.webhook.messageTemplate     | String    | The template message that will be sent. This template should have three parameters {serviceName} {applicationName} and {time}. 
notify.service.fall.discord.webhook.dateTimePattern     | String    | The date and time pattern that will be used to format the DateTime in the parameter {time} of the message template.            
client.service.{n}.url                                  | URL       | URL where the health check request will be sent.                                                                               |
client.service.{n}.httpMethod                           | String    | The HTTP method used to send the health check request. The default value is GET.
client.service.{n}.serviceName                          | String    | The name of the service.
client.service.{n}.applicationName                      | String    | The name of the application.
client.service.{n}.httpResponseCodeForCheckIfServiceIsUp| Number    | The response code for checking if service is up. The default value is 200.
client.service.{n}.checkPeriodInMilliseconds            | Number    | The period in milliseconds for each new health check request. The default value is 10.000.
client.service.{n}.timeoutConnectionInMilliseconds      | Number    | The connection timeout in milliseconds for the health check request. The default value is 10.000.
client.service.{n}.maxFailureForCheckIfServiceIsDown    | Number    | The maximum number of health check request responses with a different code, as defined in the ```httpResponseCodeForCheckIfServiceIsUp``` property. When the maximum number is achieved, the service is considered down. The default value is 2. 
client.service.{n}.periodForNewCheckAfterFailure        | Number    | Period in milliseconds for a new health check request after the service is considered down. The default value is 10.000.

Observation : For each service, you need to increment the sequence number in the {n}.

How to install
--------
For a native installation on Linux using Systemd. <br/>
First, we are going to run a Maven command called ```package```. As a result, the file ```owlet.jar``` and ```runtime``` directory will be created in the ```target``` directory. <br/>
Create the directory called ```owlet``` in ```/opt``` directory using the command ```mkdir /opt/owlet```. Then, copy the files and directory ```owlet.jar```,  ```runtime```  and ```startup.sh``` to ```/opt/owlet``` after that, copy the ```owlet.service``` file to ```/etc/systemd/system``` directory.<br/>
Once the files and directory have been copied, run the following Systemd commands:<br/>
```sudo systemctl daemon-reload```</br>
```sudo systemctl start owlet.service```</br>
# AWS Billing Monitoring Extension

## Use Case
Captures Billing statistics from Amazon CloudWatch and displays them in the AppDynamics Metric Browser.

**Note : By default, the Machine agent can only send a fixed number of metrics to the controller. This extension potentially reports thousands of metrics, so to change this limit, please follow the instructions mentioned [here](https://docs.appdynamics.com/display/PRO40/Metrics+Limits).** 

## Prerequisites
1. Please give the following permissions to the account being used to with the extension.
   **cloudwatch:ListMetrics**
   **cloudwatch:GetMetricStatistics**
2. In order to use this extension, you do need a [Standalone JAVA Machine Agent](https://docs.appdynamics.com/display/PRO44/Standalone+Machine+Agents) or [SIM Agent](https://docs.appdynamics.com/display/PRO44/Server+Visibility).  For more details on downloading these products, please  visit [here](https://download.appdynamics.com/).
3. The extension needs to be able to connect to AWS Cloudwatch in order to collect and send metrics. To do this, you will have to either establish a remote connection in between the extension and the product, or have an agent on the same machine running the product in order for the extension to collect and send the metrics.

## Agent Compatibility
<p><strong>Note: This extension is compatible with Machine Agent version 4.5.13 or later.</strong></p>
<ol>
<li>
<p>If you are seeing warning messages while starting the Machine Agent, update the http-client and http-core JARs in <code>{MACHINE_AGENT_HOME}/monitorsLibs</code> to <code>httpclient-4.5.9</code> and <code>httpcore-4.4.12</code> to make this warning go away.</p>
</li>
<li>
<p>To make this extension work on Machine Agent &lt; 4.5.13, the http-client and http-core JARs in <code>{MACHINE_AGENT_HOME}/monitorsLibs</code> need to be updated to <code>httpclient-4.5.9</code> and <code>httpcore-4.4.12</code>.</p>
</li>
</ol>

## Installation
1. Run 'mvn clean install' from aws-billing-monitoring-extension
2. Copy and unzip AWSBillingMonitor-\<version\>.zip from 'target' directory into \<machine_agent_dir\>/monitors/
3. Edit config.yml file in AWSBillingMonitor and provide the required configuration (see Configuration section).
4. Restart the Machine Agent.
Please place the extension in the "**monitors**" directory of your Machine Agent installation directory. Do not place the extension in the "**extensions**" directory of your Machine Agent installation directory.


## Configuration
In order to use the extension, you need to update the config.yml file that is present in the extension folder. The following is a step-by-step explanation of the configurable fields that are present in the config.yml file.

1. If SIM is enabled, then use the following metricPrefix
        ```
        metricPrefix: "Custom Metrics|Amazon Billing|"
        ```
   Else, configure the "COMPONENT_ID" under which the metrics need to be reported. This can be done by changing the value of `<COMPONENT_ID>` in
        metricPrefix: "Server|Component:<COMPONENT_ID>|Custom Metrics|Amazon Billing|".

        For example,
        ```
        metricPrefix: "Server|Component:100|Custom Metrics|Amazon Billing|"
        ```

2. Provide accessKey(required) and secretKey(required) of our account(s), also provide displayAccountName(any name that represents your account) and
   regions(required). If you are running this extension inside an EC2 instance which has IAM profile configured then you don't have to configure these values,
   extension will use IAM profile to authenticate.
   ~~~
   accounts:
     - awsAccessKey: "XXXXXXXX1"
       awsSecretKey: "XXXXXXXXXX1"
       displayAccountName: "TestAccount_1"
       regions: ["us-east-1","us-west-1","us-west-2"]

     - awsAccessKey: "XXXXXXXX2"
       awsSecretKey: "XXXXXXXXXX2"
       displayAccountName: "TestAccount_2"
       regions: ["eu-central-1","eu-west-1"]
   ~~~

3. Provide the list of service names that needs to be monitored. This list accepts regular expressions.
   
   ~~~
   serviceNames: ["AmazonApiGateway", "AmazonEC2", "AmazonECR", "ElasticMapReduce", "AWSQueueService"]
   ~~~   
   
## AWS Credentials Encryption
To set an encrypted awsAccessKey and awsSecretKey in config.yaml, follow the steps below:

1. Download the util jar to encrypt the AWS Credentials from [here](https://github.com/Appdynamics/maven-repo/blob/master/releases/com/appdynamics/appd-exts-commons/1.1.2/appd-exts-commons-1.1.2.jar).
2. Run command:

   	~~~   
   	java -cp appd-exts-commons-1.1.2.jar com.appdynamics.extensions.crypto.Encryptor EncryptionKey CredentialToEncrypt
   	
   	For example: 
   	java -cp "appd-exts-commons-1.1.2.jar" com.appdynamics.extensions.crypto.Encryptor test myAwsAccessKey
   	
   	java -cp "appd-exts-commons-1.1.2.jar" com.appdynamics.extensions.crypto.Encryptor test myAwsSecretKey
   	~~~
   	
3. Set the decryptionKey field in config.yaml with the encryption key used, as well as the resulting encrypted awsAccessKey and awsSecretKey in their respective fields.

## Metrics
Typical metric path: **Application Infrastructure Performance|\<Tier\>|Custom Metrics|Amazon Billing|\<Account Name\>|\<Region\>|Service Name|\<Service Name\>|Currency|\<Currency\>** followed by the metrics defined in the link below:

- [Billing Metrics](http://docs.aws.amazon.com/AmazonCloudWatch/latest/DeveloperGuide/billing-metricscollected.html)

## Contributing

Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/Appdynamics/aws-billing-monitoring-extension).

## Community

Find out more in the [AppSphere](https://www.appdynamics.com/community/exchange/extension/aws-billing-monitoring-extension) community.

## Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:help@appdynamics.com).

## Version
   |          Name            |  Version   |
   |--------------------------|------------|
   |Extension Version         |2.0.2       |
   |Controller Compatibility  |4.5 or Later|
   |Agent Compatibility  |4.5.13 or Later|
   |Last Update               |Apr 5, 2021 |

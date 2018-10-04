
# Template: Salesforce and SAP Opportunity Aggregation

This template aggregates opportunities from Salesforce and SAP into a CSV file. You can modify this basic pattern to collect from more or different sources and to produce formats other than CSV. Trigger with an HTTP call either manually or programmatically. 

Opportunities are sorted such that the opportunities only in Salesforce appear first, followed by the ones only in SAP, and lastly by the ones found in both systems. The custom sort or merge logic can be easily modified to present the data as needed. This template also serves as a great base for building APIs using the Anypoint Platform.

Aggregation templates can be easily extended to return a multitude of data in mobile friendly form to power your mobile initiatives by providing easily consumable data structures from otherwise complex backend systems.

![e4c3b69d-9602-43ee-a4b2-33889922e4e1-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/e4c3b69d-9602-43ee-a4b2-33889922e4e1-image.png)

### License Agreement

This template is subject to the conditions of the [MuleSoft License Agreement](https://s3.amazonaws.com/templates-examples/AnypointTemplateLicense.pdf "MuleSoft License Agreement").

Review the terms of the license before downloading and using this template. You can use this template for free with the Mule Enterprise Edition, CloudHub, or as a trial in Anypoint Studio.

# Use Case

I want to aggregate Opportunities from Salesforce with sales orders from SAP and compare them to see which can only be found in one of the two and which are in both instances.

For practical purposes this template generates the result in the format of a CSV Report sent by email.

This template serves as a foundation for extracting data from two systems, aggregating data, comparing values of fields for the objects, and generating a report on the differences.

As implemented, it gets opportunities from Salesforce and sales orders from SAP, compares them by  name, and generates a CSV file which shows opportunity in Salesforce, Sales Order in SAP, and opportunities from Salesforce and matching SAP. The report is then e-mailed to a configured group of e-mail addresses.

# Considerations

To make this Anypoint template run, there are certain preconditions that must be considered. All of them deal with the preparations in both, that must be made for all to run smoothly.

Failing to do so could lead to unexpected behavior of the template.

## Disclaimer

This Anypoint template uses a few private Maven dependencies from MuleSoft to work. If you intend to run this template with Maven support, you need to add three extra dependencies for SAP to the pom.xml file.

## SAP Considerations

Here's what you need to know to get this template to work with SAP.

### As a Data Destination

The SAP sales order name is stored in the text FORM HEADER of the SO.

## Salesforce Considerations

Here's what you need to know about Salesforce to get this template to work.

### FAQ

- Where can I check that the field configuration for my Salesforce instance is the right one? See: [Salesforce: Checking Field Accessibility for a Particular Field](https://help.salesforce.com/HTViewHelpDoc?id=checking_field_accessibility_for_a_particular_field.htm&language=en_US "Salesforce: Checking Field Accessibility for a Particular Field")
- Can I modify the Field Access Settings? How? See: [Salesforce: Modifying Field Access Settings](https://help.salesforce.com/HTViewHelpDoc?id=modifying_field_access_settings.htm&language=en_US "Salesforce: Modifying Field Access Settings")

### As a Data Source

If the user who configured the template for the source system does not have at least _read only_ permissions for the fields that are fetched, then an _InvalidFieldFault_ API fault displays.

```
java.lang.RuntimeException: [InvalidFieldFault [ApiQueryFault [ApiFault  
exceptionCode='INVALID_FIELD'
exceptionMessage='
Account.Phone, Account.Rating, Account.RecordTypeId, Account.ShippingCity
^
ERROR at Row:1:Column:486
No such column 'RecordTypeId' on entity 'Account'. If you are attempting 
to use a custom field, be sure to append the '__c' after the custom field 
name. Reference your WSDL or the describe call for the appropriate names.'
]
row='1'
column='486'
]
]
```

# Run it!

Simple steps to get Salesforce opportunity and SAP Sales Order Aggregation running.

## Running On Premises

In this section we help you run your template on your computer.

### Where to Download Anypoint Studio and the Mule Runtime

If you are a newcomer to Mule, here is where to get the tools.

- [Download Anypoint Studio](https://www.mulesoft.com/platform/studio)
- [Download Mule runtime](https://www.mulesoft.com/lp/dl/mule-esb-enterprise)

### Importing a Template into Studio

In Studio, click the Exchange X icon in the upper left of the taskbar, log in with your

Anypoint Platform credentials, search for the template, and click **Open**.

### Running on Studio

After you import your template into Anypoint Studio, follow these steps to run it:

- Locate the properties file `mule.dev.properties`, in src/main/resources.
- Complete all the properties required as per the examples in the "Properties to Configure" section.
- Right click the template project folder.
- Hover your mouse over `Run as`
- Click `Mule Application (configure)`
- Inside the dialog, select Environment and set the variable `mule.env` to the value `dev`
- Click `Run`To make this template run in Studio there are a few extra steps that needs to be made. Search for "Enabling Your Studio Project for SAP" in [https://docs.mulesoft.com](https://docs.mulesoft.com).

### Running on Mule Standalone

Complete all properties in one of the property files, for example in mule.prod.properties and run your app with the corresponding environment variable. To follow the example, this is `mule.env=prod`.

After this, to trigger the use case, browse to the HTTP endpoint with the port you configured in your file. For example if you set the port to `9090`, browse to `http://localhost:9090/generatereport` to create the CSV report and send it to the emails set.

## Running on CloudHub

While creating your application on CloudHub (or you can do it later as a next step), go to Runtime Manager > Manage Application > Properties to set the environment variables listed in "Properties to Configure" as well as the **mule.env**.

After you get your application to run, if you chose `template-sfdc2sap-opportunity-aggregation` as the domain name to trigger the use case, browse to `http://template-sfdc2sap-opportunity-aggregation.cloudhub.io/generatereport` and the report gets sent to the email addresses you configured.

### Deploying your Anypoint Template on CloudHub

Studio provides an easy way to deploy your template directly to CloudHub, for the specific steps to do so check this

## Properties to Configure

To use this template, configure properties (credentials, configurations, etc.) in the properties file or in CloudHub from Runtime Manager > Manage Application > Properties. The sections that follow list example values.

### Application Configuration

- http.port `9090` 

### Salesforce Connector Configuration

- sfdc.username `bob.dylan@sfdc`
- sfdc.password `DylanPassword123`
- sfdc.securityToken `avsfwCUl7apQs56Xq2AKi3X`

### SAP Connector Configuration

- sap.jco.ashost `your.sap.address.com`
- sap.jco.user `SAP_USER`
- sap.jco.passwd `SAP_PASS`
- sap.jco.sysnr `14`
- sap.jco.client `800`
- sap.jco.lang `EN`
- sap.customer.number `0000000124`

### SMTP Services Configuration

- smtp.host `smtp.example.com`
- smtp.port `587`
- smtp.user `exampleuser@example.com`
- smtp.password `ExamplePassword456`

### Email Details

- mail.from `exampleuser@example.com`
- mail.to `woody.guthrie@example.com`
- mail.subject `SFDC Opportunities/sales orders Report`
- mail.body `Report comparing opportunities from SFDC and sales orders from SAP`
- attachment.name `OrderedReport.csv`

# API Calls

Salesforce imposes limits on the number of API calls that can be made. However, this template only

makes one API call to Salesforce during aggregation.

# Customize It!

This brief guide intends to give a high level idea of how this template is built and how you can change it according to your needs.

As Mule applications are based on XML files, this page describes the XML files used with this template.

More files are available such as test classes and Mule application files, but to keep it simple, we focus on these XML files:

- config.xml
- businessLogic.xml
- endpoints.xml
- errorHandling.xml

## config.xml

Configuration for connectors and configuration properties are set in this file. Even change the configuration here, all parameters that can be modified are in properties file, which is the recommended place to make your changes. However if you want to do core changes to the logic, you need to modify this file.

In the Studio visual editor, the properties are on the _Global Element_ tab.

## businessLogic.xml

The functional aspect of the template is implemented in this XML, directed by one flow responsible of conducting the aggregation of data, comparing records, and finally formatting the output, in this case the CSV report.

Using the Scatter-Gather component the template queries the data in different systems. After that the aggregation is implemented in a DataWeave 2 script using the Transform component.

Aggregated results are sorted by:

1. Opportunities only in Salesforce.
2. Opportunities (Sales Orders) only in SAP.
3. Opportunities (Sales Orders) in both Salesforce and SAP.

These are transformed to CSV format. The final report in CSV format is sent to the email addresses, that you configured in the `mule.*.properties` file.

## endpoints.xml

This is the file where you can find the endpoint to start the aggregation. This template uses an HTTP Listener as the way to trigger the use case.

### Trigger Flow

**HTTP Listener** - Start Report Generation

- `${http.port}` is set as a property to be defined either on a property file or in CloudHub environment variables.
- The path configured by default is `generatereport` and you are free to change this to one you prefer.
- The host name for all endpoints in your CloudHub configuration is `localhost`. CloudHub then routes requests from your application domain URL to the endpoint.

## errorHandling.xml

This is the right place to handle how your integration reacts depending on the different exceptions.

This file provides error handling that is referenced by the main flow in the business logic.


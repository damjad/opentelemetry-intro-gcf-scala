# opentelemetry-intro-gcf-scala

This repository contains sample code for configuring OpenTelemetry in Scala Google Cloud Functions.
It sets up the metrics client using OpenTelemetry APIs and send the metrics data periodically to Google Monitoring Service backend.

## Setup and Build
- Create a new project with existing resources and use `sbt`  as a build tool in IntelliJ.
- Open the `sbt` shell via IntelliJ or command line.
	- run `sbt` for cli
- Clean the build directories
	- run `clean` in sbt shell
- Build the zip file by running `zipTask` in sbt shell.

## Deploy - UI

- Go to the Google Cloud Console and go to Cloud Functions.
- Create a new function named `opentelemetry-intro-gcf-scala`.
- ![](./images/3181020.png)
- Click next
- ![](./images/3181656.png)
- After Deployment, a URL will appear at the top that represents the function invocation URL. 
- ![](./images/3182309.png)

## Deploy - CLI

- Upload the zip file containing fat JAR to cloud storage
```bash
gcloud storage cp target/opentelemetry-intro-gcf-scala.zip gs://<bucket_name>/functions/opentelemetry-intro-gcf-scala.zip
```
- Deploy the google cloud function
```bash
gcloud functions deploy opentelemetry-intro-gcf-scala --gen2 --runtime=java11 --region=europe-west1 --source=gs://<bucket_name>/functions/opentelemetry-intro-gcf-scala.zip --entry-point=com.danish.world.HelloWorldScala --memory=256MB --trigger-http
```
- Replace `<bucket_name>` with bucket of your choice.

## Testing
- Running the following curl command invokes the cloud function
```bash
curl -m 70 -X POST https://europe-west1-<project_id>.cloudfunctions.net/opentelemetry-intro-gcf-scala -H "Authorization: bearer $(gcloud auth print-identity-token)" -H "Content-Type: application/json" -d '{  "name": "Dino Master" }'
```
- Expected output
```
Hello Dino Master!
```
- Replace `<project_id>` with your project id.
## Exploring Metrics in Google Monitoring Service

### Data generation
Run the following to generate data 

```bash
seq 100 | xargs -n 1 -P 10 -I {} curl -m 70 -X POST https://europe-west1-<project_id>.cloudfunctions.net/opentelemetry-intro-gcf-scala -H "Authorization: bearer $(gcloud auth print-identity-token)" -H "Content-Type: application/json" -d '{  "name": "Dino Master" }'
```

Replace `<project_id>` with your project id.

### Metrics Explorer
Go to Metrics Explorer in Google Monitoring in the cloud console. Change to PromQL and add the following query.

```
custom_googleapis_com:function_opentelemetry_intro_gcf_scala_danish_requests{monitored_resource="generic_task"}
```

You may see the following chart.
![](./images/7140938.png)

**Note:** If the metrics are not visible, see the logs, there might be `DEADLINE_EXCEEDED` error messages or any other errors from the exporter. They are intermittent and are resolved automatically. If the application is critical, one can use Collector as a proxy between the app and the Google Monitoring Service backend.  
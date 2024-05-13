package com.danish.world

import com.google.cloud.opentelemetry.metric.{GoogleCloudMetricExporter, MetricConfiguration}
import com.google.cloud.opentelemetry.shadow.semconv.ResourceAttributes
import io.opentelemetry.api.metrics.{LongCounter, Meter}
import io.opentelemetry.contrib.gcp.resource.GCPResourceProvider
import io.opentelemetry.sdk.autoconfigure.spi.internal.DefaultConfigProperties
import io.opentelemetry.sdk.metrics.SdkMeterProvider
import io.opentelemetry.sdk.metrics.`export`.{MetricReader, PeriodicMetricReader}

import java.time.Duration
import java.util.Collections

object Telemetry {

  private val meter = _setupTelemetry()
  val requestCounter: LongCounter = meter.counterBuilder("danish-requests")
    .setDescription("Processed jobs")
    .setUnit("1")
    .build

  private def _setupTelemetry(): Meter = {

    // Detect context and resource in which the code is executed. Set resource attributes accordingly.
    // These attributes are appear as dimensions (metadata) in the metrics.
    val defaultConfigProperties = DefaultConfigProperties.createFromMap(Collections.emptyMap())
    val resource = new GCPResourceProvider().createResource(defaultConfigProperties)
    println(resource.getAttributes.asMap())


    // Create a backend exporter for sending metrics.
    val metricConfiguration = MetricConfiguration
      .builder()
      .setDeadline(Duration.ofSeconds(60))
      .setPrefix(s"custom.googleapis.com/function/${resource.getAttribute(ResourceAttributes.FAAS_NAME)}")
      .build()
    val metricExporter = GoogleCloudMetricExporter.createWithConfiguration(metricConfiguration)

    // Attach the exporter to the provider with a periodic reader. It periodically triggers the metrics pipeline.
    // i.e. collect metrics, process them and export.
    val metricsReader: MetricReader = PeriodicMetricReader.builder(metricExporter).setInterval(Duration.ofSeconds(60)).build

    // Set up the metrics provider
    val meter_provider = SdkMeterProvider.builder.
      registerMetricReader(metricsReader)
      .addResource(resource)
      .build

    val meter = meter_provider.meterBuilder("com.danish.world").setInstrumentationVersion("semver:1.0.0").build
    meter
  }

}

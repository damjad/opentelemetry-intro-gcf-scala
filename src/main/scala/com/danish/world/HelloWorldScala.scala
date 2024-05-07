package com.danish.world

import com.google.cloud.functions.{HttpFunction, HttpRequest, HttpResponse}
import com.google.gson.{Gson, JsonObject}

class HelloWorldScala extends HttpFunction {
  private val gson = new Gson

  override def service(request: HttpRequest, response: HttpResponse): Unit = {
    Thread.sleep(5000)
    Telemetry.requestCounter.add(10)

    val writer = response.getWriter
    writer.write(s"Hello ${process_request(request)}!")

  }

  private def process_request(request: HttpRequest): String = {
    val body = gson.fromJson(request.getReader, classOf[JsonObject])
    if (body.has("name"))
      body.get("name").getAsString
    else
      "World"
  }
}

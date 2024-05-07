import sbt.*
import sbt.Keys.*

import java.io.*
import java.util.zip.*
import sbtassembly.AssemblyKeys.{assembly, assemblyOutputPath}


object ZipTask {
  val zipTask = taskKey[Unit]("Creates a zip file containing the assembled JAR.")

  val settings: Seq[Def.Setting[_]] = Seq(
    zipTask := {
      val jarFile = (assemblyOutputPath / assembly).value
      val zipFileName = jarFile.getName.stripSuffix(".jar") + ".zip"
      val zipFile = jarFile.getParentFile / zipFileName

      streams.value.log.info("Creating zip file: " + zipFile.getAbsolutePath)

      val zip = new ZipOutputStream(new FileOutputStream(zipFile))
      zip.putNextEntry(new ZipEntry(jarFile.getName))
      IO.transfer(new FileInputStream(jarFile), zip)
      zip.closeEntry()
      zip.close()

      streams.value.log.info("Zip file created at " + zipFile.getAbsolutePath)
    },
    zipTask := (zipTask dependsOn assembly).value
  )
}

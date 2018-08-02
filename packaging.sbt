import sbt._
import sbt.Keys._

javaOptions in Universal ++= Seq(
  "-J-Xmx1536m",
  "-J-Xms1536m",
  "-J-XX:+HeapDumpOnOutOfMemoryError",
  "-J-XX:HeapDumpPath=/tmp/heap.hprof",
  "-Dfile.encoding=UTF-8",
  "-Dhttp.port=8080",
  "-Dconfig.file=/usr/share/mosaic/conf/mosaic.conf",
  "-Dpidfile.path=/dev/null",
  "-Dplay.evolutions.db.default.autoApply=true"
)

//toevoegen van custom entries aan de Debian control file
//dit zorgt ervoor dat de reverse-proxy wordt aangepast
debianControlFile in Debian ~= { (controlFile: File) =>
  IO.append(controlFile,
    """Depends: oracle-java8, node-exporter
      |XBS-Private-BaseUrl: http://{{ ip_address }}:8080/mosaic
      |XBS-Private-Db-Extensions: ['hstore']
      |XBS-External-Dependencies: thor
      |XBS-Private-Extra-Mappings-Json: [{ "pad": "mosaic", "url": "http://{{ ip_address }}:8080/mosaic" }]
      |""".stripMargin)
  controlFile
}

/**
 * Dit mapt de deploy directory in de zip file van het artifact dat via het 'dist' commando gebouwd wordt via de univeral packager. Deze
 * mapping MOET in het root build.sbt bestand staan! Deze mapping is statisch t.o.v. sbt, dus na het wijzigen van bestanden onder de
 * deploy folder moet je een reload uitvoeren op het sbt project voor je het bouwen van de dist start.
 *
 * sbt> reload
 * sbt> dist
 *
 */
//dit maakte deel uit van het oude ansible build systeem, vermits we nu alles via debian postinst doen vervalt dit
//mappings in Universal ++= directory(baseDirectory.value / "deploy")

linuxPackageMappings in Debian += {
  val pname = (name in Universal).value
  val dir   = (baseDirectory in Debian).value
  (
    packageMapping(
    (dir / "debian/changelog") -> "/usr/share/doc/$pname/changelog.Debian.gz"
    ) withUser "root" withGroup "root" withPerms "0644" gzipped
  ) asDocs()
}

linuxPackageMappings in Debian += {
  val pname = (name in Universal).value
  val dir   = (baseDirectory in Debian).value
  packageMapping(
    (dir / "debian/mosaic_beaver.conf") -> "/etc/beaver/conf.d/mosaic_beaver.conf"
  ) withUser "root" withGroup "root" withPerms "0644"
}

publishTo in ThisBuild := version { (v: String) =>
  val nexus = "https://collab.mow.vlaanderen.be/artifacts/repository/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("collab snapshots" at nexus + "maven-snapshots")
  else
    Some("collab releases"  at nexus + "maven-releases")
}.value

publishMavenStyle in Debian := false
publishMavenStyle := true

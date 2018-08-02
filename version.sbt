val snapshotSuffix = "SNAPSHOT"

val base_version = "0.1.0"

version in ThisBuild := base_version + "-" + sys.props.getOrElse("bamboo_buildNumber", snapshotSuffix)

isSnapshot := version.value.endsWith(snapshotSuffix)

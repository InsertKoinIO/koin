import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.publish.maven.MavenPom

fun MavenPom.useDependencyResolvedVersions(configurations: ConfigurationContainer) {
    withXml {
        (((asNode()["dependencies"] as? NodeList)?.firstOrNull() as? Node)?.value() as? NodeList)?.let { dependencies ->
            val resolvedVersions = configurations.getResolverVersions()

            dependencies.map { it as Node }.forEach {
                val groupId = it.getChildValue("groupId")
                val artifactId = it.getChildValue("artifactId")
                val scope = it.getChildValue("scope")
                resolvedVersions["${scope}Classpath"]?.let { entries ->
                    entries["$groupId:$artifactId"]?.let { version ->
                        it.setChildValue("version", version)
                    }
                }
            }
        }
    }
}

fun ConfigurationContainer.getResolverVersions() = filter {
    return@filter it.name.endsWith("Classpath") && (!it.name.startsWith("debug") && !it.name.startsWith("release"))
}.mapNotNull { cfg ->
    val resolvedConfiguration = cfg.resolvedConfiguration
    val resolvedArtifacts = resolvedConfiguration.resolvedArtifacts
    resolvedArtifacts.mapNotNull {
        val mvi = it.moduleVersion.id
        "${mvi.group}:${mvi.name}" to mvi.version
    }.takeUnless {
        it.isEmpty()
    }?.let {
        cfg.name to it.toMap()
    }
}.toMap()

fun Node.getChildValue(key: String) = (((get(key) as NodeList).first() as Node).value() as NodeList).first() as String

fun Node.setChildValue(key: String, value: String) = (((get(key) as NodeList).firstOrNull() as? Node) ?: appendNode(key)).setValue(value)

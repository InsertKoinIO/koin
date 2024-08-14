apply(plugin = "signing")

fun isReleaseBuild(): Boolean = System.getenv("IS_RELEASE") == "true" || false

fun getSigningKeyId(): String = findProperty("SIGNING_KEY_ID")?.toString() ?: System.getenv("SIGNING_KEY_ID") ?: ""

fun getSigningKey(): String = findProperty("SIGNING_KEY")?.toString() ?: System.getenv("SIGNING_KEY") ?: ""

fun getSigningPassword(): String =
    findProperty("SIGNING_PASSWORD")?.toString() ?: System.getenv("SIGNING_PASSWORD") ?: ""

if (isReleaseBuild()) {
    configure<SigningExtension> {
        useInMemoryPgpKeys(
            getSigningKeyId(),
            getSigningKey(),
            getSigningPassword(),
        )

        sign(extensions.getByType<PublishingExtension>().publications)
    }
}
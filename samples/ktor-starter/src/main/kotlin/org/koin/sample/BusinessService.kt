package org.koin.sample

import org.koin.sample.Properties.BYE_MSG
import org.koin.sample.Properties.HELLO_MSG
import org.koin.standalone.KoinComponent
import org.koin.standalone.property

/**
 * Sample Service interface
 */
interface BusinessService {
    fun doJob(job: String): String
}

/**
 * Service implementation class which implements KoinComponent marker (empty) interface in order to be able to
 * use Koin DI mechanism such as demonstrated here "by property()".
 *
 * See https://github.com/Ekito/koin/blob/master/koin-core/src/main/kotlin/org/koin/standalone/KoinComponent.kt for more
 * details.
 */
class BusinessServiceImpl : BusinessService, KoinComponent {

    // Inject properties with default values
    private val hi by property(HELLO_MSG, "Hi")
    private val bye by property(BYE_MSG, "Ciao")

    override fun doJob(job: String): String {
        return when (job) {
            HI_JOB -> "$hi from Ktor and Koin"
            BYE_JOB -> "$bye from Ktor and Koin"
            else -> throw Exception("Unknown job")
        }
    }

    companion object {
        const val HI_JOB = "HI_JOB"
        const val BYE_JOB = "BYE_JOB"
    }
}
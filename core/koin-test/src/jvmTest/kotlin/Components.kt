import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatformTools

@Suppress("unused")
class Simple {
    class ComponentA
    class ComponentB(val a: ComponentA)
    class ComponentC(val b: ComponentB)
    class MyString(val s: String)

    class UUIDComponent {
        fun getUUID() = KoinPlatformTools.generateId()
    }
}

object UpperCase : Qualifier {
    override val value: String = "UpperCase"
}

import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Provided
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatformTools
import org.koin.mp.generateId

@Suppress("unused")
class Others {
    class ComponentA
    class ComponentB(val a: ComponentA)
    class ComponentBParam(@InjectedParam val a: ComponentA)
    class ComponentBProvided(@Provided val a: ComponentA)
    class ComponentBProvided2(@Provided val a: ComponentA)
    class ComponentC(val b: ComponentB)
    class MyString(val s: String)

    class UUIDComponent {
        fun getUUID() = KoinPlatformTools.generateId()
    }
}

object UpperCase : Qualifier {
    override val value: String = "UpperCase"
}

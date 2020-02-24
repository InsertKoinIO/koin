package org.koin.test

import com.benasher44.uuid.uuid4
import org.koin.core.qualifier.Qualifier

@Suppress("unused")
class Simple {
    class ComponentA
    class ComponentB(val a: ComponentA)
    class MyString(val s: String)

    class UUIDComponent {
        fun getUUID() = uuid4().toString()
    }
}

object UpperCase : Qualifier {
    override val value: String = "UpperCase"

}

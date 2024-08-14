package org.koin.core.instance

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ComponentA
class ComponentA2
class ComponentB(val a: ComponentA)

interface Component {
    val a: ComponentA
}

class ComponentD(override val a: ComponentA) : Component

class View : KoinComponent {
    val presenter: Presenter by inject()
}

class Presenter(val repository: Repository)
class Repository(val datasource: Datasource)
interface Datasource
class DebugDatasource : Datasource

package org.koin.experimental.builder

import org.koin.core.KoinComponent
import org.koin.core.inject

class ComponentA
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
package org.koin.benchmark

import org.koin.core.module.Module
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

private val level0Modules = List(100) {
  module {}
}

private val level1Modules = List(100) {
  val q = qualifier("q-$it")
  module {
    includes(level0Modules)
    single(q) { A1() }
    single(q) { A2() }
    single(q) { A3() }
    single(q) { A4() }
    single(q) { A5() }
  }
}

private val level2Modules = List(100) {
  val q = qualifier("q-$it")
  module {
    includes(level0Modules)
    includes(level1Modules[it])

    single(q) { A6() }
    single(q) { A7() }
    single(q) { A8() }
    single(q) { A9() }
    single(q) { A10() }
  }
}

private val level3Modules = List(100) {
  val q = qualifier("q-$it")
  module {
    includes(level0Modules)
    includes(level2Modules[it])

    single(q) { A11() }
    single(q) { A12() }
    single(q) { A13() }
    single(q) { A14() }
    single(q) { A15() }
  }
}

private val level4Modules = List(100) {
  val q = qualifier("q-$it")
  module {
    includes(level0Modules)
    includes(level3Modules[it])

    single(q) { A16() }
    single(q) { A17() }
    single(q) { A18() }
    single(q) { A19() }
    single(q) { A20() }
  }
}

internal val nestedModules: List<Module> = List(100) {
  val q = qualifier("q-$it")
  module {
    includes(level0Modules)
    includes(level1Modules)
    includes(level2Modules)
    includes(level3Modules)
    includes(level4Modules)

    single(q) { A16() }
    single(q) { A17() }
    single(q) { A18() }
    single(q) { A19() }
    single(q) { A20() }
  }
}
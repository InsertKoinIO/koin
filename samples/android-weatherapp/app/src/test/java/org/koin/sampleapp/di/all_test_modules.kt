package org.koin.sampleapp.di


val testRemoteDatasource = listOf(RxTestModule, weatherModule, RemoteDataSourceModule)
val testLocalDatasource = listOf(RxTestModule, weatherModule, TestDataSourceModule)
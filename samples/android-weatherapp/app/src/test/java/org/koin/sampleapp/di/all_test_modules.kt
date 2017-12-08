package org.koin.sampleapp.di


val testRemoteDatasource = listOf(RxTestModule, WeatherModule, RemoteDataSourceModule)
val testLocalDatasource = listOf(RxTestModule, WeatherModule, TestDataSourceModule)
package com.example.jetnews.di

import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.impl.FakeInterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.FakePostsRepository
import com.example.jetnews.ui.NavigationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // If need DI, uncomment NavigationViewModel
//    viewModel { NavigationViewModel(get()) }
    single<InterestsRepository> { FakeInterestsRepository() }
    single<PostsRepository> { FakePostsRepository(androidContext().resources) }
}
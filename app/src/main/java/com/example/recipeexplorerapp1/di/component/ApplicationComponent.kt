package com.example.recipeexplorerapp1.di.component

import android.content.Context
import com.google.gson.Gson
import com.example.recipeexplorerapp1.Application
import com.example.recipeexplorerapp1.common.IRepository
import com.example.recipeexplorerapp1.di.module.ApplicationModule

import com.example.recipeexplorerapp1.di.module.DatabaseModule


import com.example.recipeexplorerapp1.di.module.NetModule
import com.example.recipeexplorerapp1.utils.SchedulerProvider

import dagger.Component
import javax.inject.Singleton

@Singleton


@Component(modules = arrayOf(ApplicationModule::class,NetModule::class,DatabaseModule::class))




interface ApplicationComponent {
    fun app(): Application

    fun context(): Context

    fun repository(): IRepository

    fun scheduler(): SchedulerProvider

    fun gson(): Gson

}

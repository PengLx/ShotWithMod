package com.chsteam.shotwithmod.api

interface Gun {

    fun shot()

    fun shotOnAim()

    fun reload() : Boolean

    fun unload()
}
package dev.cloudants.iulat.lib.providers

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cloudants.iulat.lib.services.UserService
import dev.cloudants.iulat.lib.services.UserServiceImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun bindUserService(userServiceImpl: UserServiceImpl): UserService
}
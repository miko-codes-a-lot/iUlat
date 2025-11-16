package dev.cloudants.iulat.lib.providers

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cloudants.iulat.lib.services.AddressService
import dev.cloudants.iulat.lib.services.AuthService
import dev.cloudants.iulat.lib.services.GarbageDisposalService
import dev.cloudants.iulat.lib.services.UserService
import dev.cloudants.iulat.lib.services_impl.AddressServiceImpl
import dev.cloudants.iulat.lib.services_impl.AuthServiceImpl
import dev.cloudants.iulat.lib.services_impl.GarbageDisposalServiceImpl
import dev.cloudants.iulat.lib.services_impl.UserServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    @Singleton
    abstract fun bindUserService(userServiceImpl: UserServiceImpl): UserService

    @Binds
    @Singleton
    abstract fun bindAuthService(authServiceImpl: AuthServiceImpl): AuthService

    @Binds
    @Singleton
    abstract fun bindAddressService(addressServiceImpl: AddressServiceImpl): AddressService

    @Binds
    @Singleton
    abstract fun bindGarbageDisposal(garbageDisposalServiceImpl: GarbageDisposalServiceImpl): GarbageDisposalService
}
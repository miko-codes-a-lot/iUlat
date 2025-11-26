package dev.cloudants.iulat.lib.providers

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cloudants.iulat.lib.services.AddressService
import dev.cloudants.iulat.lib.services.AdminReportService
import dev.cloudants.iulat.lib.services.AuthService
import dev.cloudants.iulat.lib.services.BrokenStreetLightsService
import dev.cloudants.iulat.lib.services.ChatService
import dev.cloudants.iulat.lib.services.GarbageDisposalService
import dev.cloudants.iulat.lib.services.MyLogsService
import dev.cloudants.iulat.lib.services.NoWaterSupplyService
import dev.cloudants.iulat.lib.services.OthersService
import dev.cloudants.iulat.lib.services.PublicDisturbanceService
import dev.cloudants.iulat.lib.services.RoadRepairService
import dev.cloudants.iulat.lib.services.RobberiesService
import dev.cloudants.iulat.lib.services.UserService
import dev.cloudants.iulat.lib.services.VehicleCrashService
import dev.cloudants.iulat.lib.services_impl.AddressServiceImpl
import dev.cloudants.iulat.lib.services_impl.AdminReportServicelmpl
import dev.cloudants.iulat.lib.services_impl.AuthServiceImpl
import dev.cloudants.iulat.lib.services_impl.BrokenStreetLightServicelmpl
import dev.cloudants.iulat.lib.services_impl.ChatServiceImpl
import dev.cloudants.iulat.lib.services_impl.GarbageDisposalServiceImpl
import dev.cloudants.iulat.lib.services_impl.MyLogsServiceImpl
import dev.cloudants.iulat.lib.services_impl.NoWaterSupplyServicelmpl
import dev.cloudants.iulat.lib.services_impl.OthersServicelmpl
import dev.cloudants.iulat.lib.services_impl.PublicDisturbanceServicelmpl
import dev.cloudants.iulat.lib.services_impl.RoadRepairServicelmpl
import dev.cloudants.iulat.lib.services_impl.RobberiesServicelmpl
import dev.cloudants.iulat.lib.services_impl.UserServiceImpl
import dev.cloudants.iulat.lib.services_impl.VehicleCrashServicelmpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    @Singleton
    abstract fun bindUserService(userServiceImpl: UserServiceImpl): UserService

    @Binds
    @Singleton
    abstract fun bindChatService(chatServiceImpl: ChatServiceImpl): ChatService

    @Binds
    @Singleton
    abstract fun bindMyLogsService(myLogsServiceImpl: MyLogsServiceImpl): MyLogsService

    @Binds
    @Singleton
    abstract fun bindAuthService(authServiceImpl: AuthServiceImpl): AuthService

    @Binds
    @Singleton
    abstract fun bindAddressService(addressServiceImpl: AddressServiceImpl): AddressService

    @Binds
    @Singleton
    abstract fun bindGarbageDisposal(garbageDisposalServiceImpl: GarbageDisposalServiceImpl): GarbageDisposalService

    @Binds
    @Singleton
    abstract fun bindBrokenStreetLightsService(brokenStreetLightServicelmpl: BrokenStreetLightServicelmpl): BrokenStreetLightsService

    @Binds
    @Singleton
    abstract fun bindNoWaterSupplyService(noWaterSupplyServicelmpl: NoWaterSupplyServicelmpl): NoWaterSupplyService

    @Binds
    @Singleton
    abstract fun bindOthersService(othersServicelmpl: OthersServicelmpl): OthersService

    @Binds
    @Singleton
    abstract fun bindPublicDisturbanceService(publicDisturbanceServicelmpl: PublicDisturbanceServicelmpl): PublicDisturbanceService

    @Binds
    @Singleton
    abstract fun bindRoadRepairService(roadRepairServicelmpl: RoadRepairServicelmpl): RoadRepairService

    @Binds
    @Singleton
    abstract fun bindRobberiesService(robberiesServicelmpl: RobberiesServicelmpl): RobberiesService

    @Binds
    @Singleton
    abstract fun bindVehicleCrashService(vehicleCrashServicelmpl: VehicleCrashServicelmpl): VehicleCrashService

    @Binds
    @Singleton
    abstract fun bindAdminReportService(adminReportServicelmpl: AdminReportServicelmpl): AdminReportService
}
package insulator.di.dagger

import dagger.Component
import insulator.views.configurations.ListClusterView
import javax.inject.Singleton

@Singleton
@Component(modules = [RootModule::class])
interface Insulator {
    fun getListClusterView(): ListClusterView
}
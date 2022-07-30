package di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import utils.AppResourcesProvider
import utils.ResourceProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ResourceModule {

    @Singleton
    @Binds
    abstract fun provideProvideResource(appResourcesProvider: AppResourcesProvider): ResourceProvider

    /*
        @Provides
        fun provideProvideResource(@ApplicationContext context: Context): AppResourcesProvider =
        AppResourcesProvider(context)
     */

}
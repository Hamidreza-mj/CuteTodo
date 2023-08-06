package di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import data.datastore.PrefsDataStore
import hlv.cute.todo.App
import kotlinx.coroutines.CoroutineDispatcher
import ui.activity.MainActivity
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApp(@ApplicationContext context: Context): App = context as App

    @Singleton
    @Provides
    fun provideMainActivity(activity: MainActivity): MainActivity = activity

    @Singleton
    @Provides
    fun providePrefDataStore(
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): PrefsDataStore {
        return PrefsDataStore(
            dispatcher = dispatcher,
        )
    }

}
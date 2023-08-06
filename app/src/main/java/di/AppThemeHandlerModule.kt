package di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import data.datastore.PrefsDataStore
import ui.util.AppThemeHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppThemeHandlerModule {

    @Singleton
    @Provides
    fun provideAppThemeHandler(
        @ApplicationContext context: Context,
        prefsDataStore: PrefsDataStore
    ): AppThemeHandler {
        return AppThemeHandler(
            context = context,
            prefsDataStore = prefsDataStore,
        )
    }

}
package di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import repo.dao.CategoryDao
import repo.dao.NotificationDao
import repo.dao.TodoDao
import repo.database.TodoDatabase
import utils.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Singleton
    @Provides
    fun provideTodoDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            Constants.Names.DB_NAME
        )
            .fallbackToDestructiveMigration()
            //.addCallback(roomCallBack)
            //.allowMainThreadQueries() this allow room to run db process in main thread (UI Thread)
            //it is recommended to be disallowed
            .build()
    }

    @Singleton
    @Provides
    fun provideCategoryDao(db: TodoDatabase): CategoryDao = db.categoryDao

    @Singleton
    @Provides
    fun provideNotificationDao(db: TodoDatabase): NotificationDao = db.notificationDao

    @Singleton
    @Provides
    fun provideTodoDao(db: TodoDatabase): TodoDao = db.todoDao

}
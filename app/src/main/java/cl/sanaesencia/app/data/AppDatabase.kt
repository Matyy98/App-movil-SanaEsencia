package cl.sanaesencia.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Base de datos local de la app (Room).
// Cada integrante agrega su tabla en "entities" y su DAO abajo.
@Database(entities = [UsuarioEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        // Se guarda aquí para crear la base de datos una sola vez
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            val actual = instancia
            if (actual != null) return actual
            val nueva = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "sanaesencia.db"
            )
                // Si cambia la versión, borra y vuelve a crear las tablas (solo datos de prueba)
                .fallbackToDestructiveMigration(true)
                .build()
            instancia = nueva
            return nueva
        }
    }
}
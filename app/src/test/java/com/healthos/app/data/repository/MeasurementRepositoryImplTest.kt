package com.healthos.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.healthos.app.data.local.AppDatabase
import com.healthos.app.data.local.entity.UserEntity
import com.healthos.app.domain.model.Measurement
import com.healthos.app.domain.model.MeasurementType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class MeasurementRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: MeasurementRepositoryImpl
    private val userId = 1L

    @Before
    fun setUp() = runTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        database.userDao().upsert(
            UserEntity(id = userId, name = "Ana", birthDate = 0, height = 165f, createdAt = 0),
        )
        repository = MeasurementRepositoryImpl(database.measurementDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `observeHistory only returns entries for the requested type`() = runTest {
        repository.addMeasurement(
            Measurement(userId = userId, type = MeasurementType.ARM, value = 30f, date = LocalDate.of(2026, 1, 1)),
        )
        repository.addMeasurement(
            Measurement(userId = userId, type = MeasurementType.WAIST, value = 80f, date = LocalDate.of(2026, 1, 1)),
        )

        val armHistory = repository.observeHistory(userId, MeasurementType.ARM).first()

        assertEquals(1, armHistory.size)
        assertTrue(armHistory.all { it.type == MeasurementType.ARM })
    }

    @Test
    fun `deleteMeasurement removes it from the history`() = runTest {
        repository.addMeasurement(
            Measurement(userId = userId, type = MeasurementType.ARM, value = 30f, date = LocalDate.of(2026, 1, 1)),
        )
        val saved = repository.observeHistory(userId, MeasurementType.ARM).first().first()

        repository.deleteMeasurement(saved.id)

        assertEquals(emptyList<Measurement>(), repository.observeHistory(userId, MeasurementType.ARM).first())
    }
}

package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.*
import com.example.adoption_and_childcare.data.db.entities.SyncQueueEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildBehaviorAssessmentDao {
    @Query("SELECT * FROM child_behavior_assessments ORDER BY assessment_date DESC")
    fun observeAll(): Flow<List<ChildBehaviorAssessmentEntity>>
    @Query("SELECT * FROM child_behavior_assessments WHERE child_id = :childId ORDER BY assessment_date DESC")
    fun observeByChildId(childId: Int): Flow<List<ChildBehaviorAssessmentEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChildBehaviorAssessmentEntity): Long
    @Update
    suspend fun update(entity: ChildBehaviorAssessmentEntity)
    @Delete
    suspend fun delete(entity: ChildBehaviorAssessmentEntity)
}

@Dao
interface ChildWelfareIncidentDao {
    @Query("SELECT * FROM child_welfare_incidents ORDER BY incident_date DESC")
    fun observeAll(): Flow<List<ChildWelfareIncidentEntity>>
    @Query("SELECT * FROM child_welfare_incidents WHERE child_id = :childId ORDER BY incident_date DESC")
    fun observeByChildId(childId: Int): Flow<List<ChildWelfareIncidentEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChildWelfareIncidentEntity): Long
    @Update
    suspend fun update(entity: ChildWelfareIncidentEntity)
    @Delete
    suspend fun delete(entity: ChildWelfareIncidentEntity)
}

@Dao
interface VaccinationRecordDao {
    @Query("SELECT * FROM vaccination_records ORDER BY administration_date DESC")
    fun observeAll(): Flow<List<VaccinationRecordEntity>>
    @Query("SELECT * FROM vaccination_records WHERE child_id = :childId ORDER BY administration_date DESC")
    fun observeByChildId(childId: Int): Flow<List<VaccinationRecordEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: VaccinationRecordEntity): Long
    @Update
    suspend fun update(entity: VaccinationRecordEntity)
    @Delete
    suspend fun delete(entity: VaccinationRecordEntity)
}

@Dao
interface SiblingDao {
    @Query("SELECT * FROM siblings ORDER BY created_at DESC")
    fun observeAll(): Flow<List<SiblingEntity>>
    @Query("SELECT * FROM siblings WHERE child_id = :childId")
    fun observeByChildId(childId: Int): Flow<List<SiblingEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SiblingEntity): Long
    @Update
    suspend fun update(entity: SiblingEntity)
    @Delete
    suspend fun delete(entity: SiblingEntity)
}

@Dao
interface ConsentRecordDao {
    @Query("SELECT * FROM consent_records ORDER BY consent_date DESC")
    fun observeAll(): Flow<List<ConsentRecordEntity>>
    @Query("SELECT * FROM consent_records WHERE child_id = :childId ORDER BY consent_date DESC")
    fun observeByChildId(childId: Int): Flow<List<ConsentRecordEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ConsentRecordEntity): Long
    @Update
    suspend fun update(entity: ConsentRecordEntity)
    @Delete
    suspend fun delete(entity: ConsentRecordEntity)
}

@Dao
interface ChildServicesReferralDao {
    @Query("SELECT * FROM child_services_referrals ORDER BY referral_date DESC")
    fun observeAll(): Flow<List<ChildServicesReferralEntity>>
    @Query("SELECT * FROM child_services_referrals WHERE child_id = :childId ORDER BY referral_date DESC")
    fun observeByChildId(childId: Int): Flow<List<ChildServicesReferralEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChildServicesReferralEntity): Long
    @Update
    suspend fun update(entity: ChildServicesReferralEntity)
    @Delete
    suspend fun delete(entity: ChildServicesReferralEntity)
}

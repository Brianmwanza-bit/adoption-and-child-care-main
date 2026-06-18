package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestigationDao {
    @Query("SELECT * FROM investigations ORDER BY opened_date DESC")
    fun observeAll(): Flow<List<InvestigationEntity>>
    @Query("SELECT * FROM investigations WHERE child_id = :childId ORDER BY opened_date DESC")
    fun observeByChildId(childId: Int): Flow<List<InvestigationEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: InvestigationEntity): Long
    @Update
    suspend fun update(entity: InvestigationEntity)
    @Delete
    suspend fun delete(entity: InvestigationEntity)
}

@Dao
interface ServicePlanDao {
    @Query("SELECT * FROM service_plans ORDER BY start_date DESC")
    fun observeAll(): Flow<List<ServicePlanEntity>>
    @Query("SELECT * FROM service_plans WHERE child_id = :childId ORDER BY start_date DESC")
    fun observeByChildId(childId: Int): Flow<List<ServicePlanEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ServicePlanEntity): Long
    @Update
    suspend fun update(entity: ServicePlanEntity)
    @Delete
    suspend fun delete(entity: ServicePlanEntity)
}

@Dao
interface ServicePlanGoalDao {
    @Query("SELECT * FROM service_plan_goals ORDER BY target_date")
    fun observeAll(): Flow<List<ServicePlanGoalEntity>>
    @Query("SELECT * FROM service_plan_goals WHERE plan_id = :planId ORDER BY target_date")
    fun observeByPlanId(planId: Int): Flow<List<ServicePlanGoalEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ServicePlanGoalEntity): Long
    @Update
    suspend fun update(entity: ServicePlanGoalEntity)
    @Delete
    suspend fun delete(entity: ServicePlanGoalEntity)
}

@Dao
interface VisitationScheduleDao {
    @Query("SELECT * FROM visitation_schedules ORDER BY visitation_date DESC")
    fun observeAll(): Flow<List<VisitationScheduleEntity>>
    @Query("SELECT * FROM visitation_schedules WHERE child_id = :childId ORDER BY visitation_date DESC")
    fun observeByChildId(childId: Int): Flow<List<VisitationScheduleEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: VisitationScheduleEntity): Long
    @Update
    suspend fun update(entity: VisitationScheduleEntity)
    @Delete
    suspend fun delete(entity: VisitationScheduleEntity)
}

@Dao
interface ReferralDao {
    @Query("SELECT * FROM referrals ORDER BY referral_date DESC")
    fun observeAll(): Flow<List<ReferralEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ReferralEntity): Long
    @Update
    suspend fun update(entity: ReferralEntity)
    @Delete
    suspend fun delete(entity: ReferralEntity)
}

@Dao
interface AftercarePlanDao {
    @Query("SELECT * FROM aftercare_plans ORDER BY start_date DESC")
    fun observeAll(): Flow<List<AftercarePlanEntity>>
    @Query("SELECT * FROM aftercare_plans WHERE child_id = :childId ORDER BY start_date DESC")
    fun observeByChildId(childId: Int): Flow<List<AftercarePlanEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AftercarePlanEntity): Long
    @Update
    suspend fun update(entity: AftercarePlanEntity)
    @Delete
    suspend fun delete(entity: AftercarePlanEntity)
}

@Dao
interface OrganizationPartnerDao {
    @Query("SELECT * FROM organization_partners ORDER BY partner_name")
    fun observeAll(): Flow<List<OrganizationPartnerEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: OrganizationPartnerEntity): Long
    @Update
    suspend fun update(entity: OrganizationPartnerEntity)
    @Delete
    suspend fun delete(entity: OrganizationPartnerEntity)
}

@Dao
interface ServiceProviderDao {
    @Query("SELECT * FROM service_providers ORDER BY provider_name")
    fun observeAll(): Flow<List<ServiceProviderEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ServiceProviderEntity): Long
    @Update
    suspend fun update(entity: ServiceProviderEntity)
    @Delete
    suspend fun delete(entity: ServiceProviderEntity)
}

@Dao
interface DonorFundingDao {
    @Query("SELECT * FROM donor_funding ORDER BY donation_date DESC")
    fun observeAll(): Flow<List<DonorFundingEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DonorFundingEntity): Long
    @Update
    suspend fun update(entity: DonorFundingEntity)
    @Delete
    suspend fun delete(entity: DonorFundingEntity)
}

@Dao
interface BudgetAllocationDao {
    @Query("SELECT * FROM budget_allocations ORDER BY financial_year DESC")
    fun observeAll(): Flow<List<BudgetAllocationEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BudgetAllocationEntity): Long
    @Update
    suspend fun update(entity: BudgetAllocationEntity)
    @Delete
    suspend fun delete(entity: BudgetAllocationEntity)
}

@Dao
interface CountyDao {
    @Query("SELECT * FROM counties ORDER BY county_name")
    fun observeAll(): Flow<List<CountyEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CountyEntity): Long
    @Update
    suspend fun update(entity: CountyEntity)
    @Delete
    suspend fun delete(entity: CountyEntity)
}

@Dao
interface CountyOfficeDao {
    @Query("SELECT * FROM county_offices ORDER BY office_name")
    fun observeAll(): Flow<List<CountyOfficeEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CountyOfficeEntity): Long
    @Update
    suspend fun update(entity: CountyOfficeEntity)
    @Delete
    suspend fun delete(entity: CountyOfficeEntity)
}

@Dao
interface PlacementDisruptionDao {
    @Query("SELECT * FROM placement_disruptions ORDER BY disruption_date DESC")
    fun observeAll(): Flow<List<PlacementDisruptionEntity>>
    @Query("SELECT * FROM placement_disruptions WHERE child_id = :childId ORDER BY disruption_date DESC")
    fun observeByChildId(childId: Int): Flow<List<PlacementDisruptionEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlacementDisruptionEntity): Long
    @Update
    suspend fun update(entity: PlacementDisruptionEntity)
    @Delete
    suspend fun delete(entity: PlacementDisruptionEntity)
}

@Dao
interface FosterFamilyTrainingDao {
    @Query("SELECT * FROM foster_family_training ORDER BY training_date DESC")
    fun observeAll(): Flow<List<FosterFamilyTrainingEntity>>
    @Query("SELECT * FROM foster_family_training WHERE family_id = :familyId ORDER BY training_date DESC")
    fun observeByFamilyId(familyId: Int): Flow<List<FosterFamilyTrainingEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FosterFamilyTrainingEntity): Long
    @Update
    suspend fun update(entity: FosterFamilyTrainingEntity)
    @Delete
    suspend fun delete(entity: FosterFamilyTrainingEntity)
}

@Dao
interface ReportGeneratedDao {
    @Query("SELECT * FROM reports_generated ORDER BY generated_date DESC")
    fun observeAll(): Flow<List<ReportGeneratedEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ReportGeneratedEntity): Long
    @Update
    suspend fun update(entity: ReportGeneratedEntity)
    @Delete
    suspend fun delete(entity: ReportGeneratedEntity)
}

@Dao
interface EmergencyEventDao {
    @Query("SELECT * FROM emergency_events ORDER BY event_date DESC")
    fun observeAll(): Flow<List<EmergencyEventEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EmergencyEventEntity): Long
    @Update
    suspend fun update(entity: EmergencyEventEntity)
    @Delete
    suspend fun delete(entity: EmergencyEventEntity)
}

@Dao
interface GlobalDocumentStorageDao {
    @Query("SELECT * FROM global_document_storage ORDER BY uploaded_at DESC")
    fun observeAll(): Flow<List<GlobalDocumentStorageEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GlobalDocumentStorageEntity): Long
    @Update
    suspend fun update(entity: GlobalDocumentStorageEntity)
    @Delete
    suspend fun delete(entity: GlobalDocumentStorageEntity)
}

@Dao
interface InterCountyTransferDao {
    @Query("SELECT * FROM inter_county_transfers ORDER BY transfer_date DESC")
    fun observeAll(): Flow<List<InterCountyTransferEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: InterCountyTransferEntity): Long
    @Update
    suspend fun update(entity: InterCountyTransferEntity)
    @Delete
    suspend fun delete(entity: InterCountyTransferEntity)
}

@Dao
interface WorkerLocationTrackingDao {
    @Query("SELECT * FROM worker_location_tracking ORDER BY tracking_time DESC")
    fun observeAll(): Flow<List<WorkerLocationTrackingEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WorkerLocationTrackingEntity): Long
    @Update
    suspend fun update(entity: WorkerLocationTrackingEntity)
    @Delete
    suspend fun delete(entity: WorkerLocationTrackingEntity)
}

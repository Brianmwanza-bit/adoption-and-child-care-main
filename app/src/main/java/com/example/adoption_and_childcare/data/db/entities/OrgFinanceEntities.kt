package com.example.adoption_and_childcare.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "organization_partners")
data class OrganizationPartnerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "partner_id") val partnerId: Int = 0,
    @ColumnInfo(name = "partner_name") val partnerName: String,
    @ColumnInfo(name = "partner_type") val partnerType: String? = null,
    @ColumnInfo(name = "contact_person") val contactPerson: String? = null,
    @ColumnInfo(name = "phone") val phone: String? = null,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "mou_date") val mouDate: String? = null,
    @ColumnInfo(name = "mou_expiry") val mouExpiry: String? = null,
    @ColumnInfo(name = "services_provided") val servicesProvided: String? = null,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "service_providers")
data class ServiceProviderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "provider_id") val providerId: Int = 0,
    @ColumnInfo(name = "provider_name") val providerName: String,
    @ColumnInfo(name = "provider_type") val providerType: String? = null,
    @ColumnInfo(name = "contact_person") val contactPerson: String? = null,
    @ColumnInfo(name = "phone") val phone: String? = null,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "address") val address: String? = null,
    @ColumnInfo(name = "county") val county: String? = null,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "contract_start") val contractStart: String? = null,
    @ColumnInfo(name = "contract_end") val contractEnd: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "donor_funding")
data class DonorFundingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "donation_id") val donationId: Int = 0,
    @ColumnInfo(name = "donor_name") val donorName: String,
    @ColumnInfo(name = "donor_type") val donorType: String? = null,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "donation_date") val donationDate: String,
    @ColumnInfo(name = "purpose") val purpose: String? = null,
    @ColumnInfo(name = "reference_number") val referenceNumber: String? = null,
    @ColumnInfo(name = "received_by") val receivedBy: Int? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "budget_allocations")
data class BudgetAllocationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "allocation_id") val allocationId: Int = 0,
    @ColumnInfo(name = "financial_year") val financialYear: String? = null,
    @ColumnInfo(name = "category") val category: String? = null,
    @ColumnInfo(name = "allocated_amount") val allocatedAmount: Double? = null,
    @ColumnInfo(name = "utilized_amount") val utilizedAmount: Double? = 0.0,
    @ColumnInfo(name = "remaining_amount") val remainingAmount: Double? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "counties")
data class CountyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "county_id") val countyId: Int = 0,
    @ColumnInfo(name = "county_name") val countyName: String,
    @ColumnInfo(name = "county_code") val countyCode: String? = null,
    @ColumnInfo(name = "region") val region: String? = null,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(tableName = "county_offices")
data class CountyOfficeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "office_id") val officeId: Int = 0,
    @ColumnInfo(name = "office_name") val officeName: String,
    @ColumnInfo(name = "office_code") val officeCode: String? = null,
    @ColumnInfo(name = "county") val county: String,
    @ColumnInfo(name = "sub_county") val subCounty: String? = null,
    @ColumnInfo(name = "address") val address: String? = null,
    @ColumnInfo(name = "po_box") val poBox: String? = null,
    @ColumnInfo(name = "phone") val phone: String? = null,
    @ColumnInfo(name = "alt_phone") val altPhone: String? = null,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "head_officer_name") val headOfficerName: String? = null,
    @ColumnInfo(name = "head_officer_title") val headOfficerTitle: String? = null,
    @ColumnInfo(name = "head_officer_user_id") val headOfficerUserId: Int? = null,
    @ColumnInfo(name = "head_officer_phone") val headOfficerPhone: String? = null,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "latitude") val latitude: Double? = null,
    @ColumnInfo(name = "longitude") val longitude: Double? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String? = null
)

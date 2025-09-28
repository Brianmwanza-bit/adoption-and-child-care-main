package com.example.adoption_and_childcare.data.db

import android.content.Context
import com.example.adoption_and_childcare.data.db.entities.AdoptionApplicationEntity
import com.example.adoption_and_childcare.data.db.entities.CaseReportEntity
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import com.example.adoption_and_childcare.data.db.entities.FamilyEntity
import com.example.adoption_and_childcare.data.db.entities.HomeStudyEntity
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity
import com.example.adoption_and_childcare.data.db.entities.PlacementEntity
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.data.security.Security
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseInitializer {
    
    fun initializeDatabase(context: Context) {
        val db = AppDatabase.getInstance(context)
        
        CoroutineScope(Dispatchers.IO).launch {
            // Check if database is already initialized
            val userCount = db.userDao().count()
            if (userCount > 0) return@launch // Already initialized
            
            // Create sample users
            val adminUser = UserEntity(
                username = "admin",
                passwordHash = Security.hashPassword("admin123"),
                role = "Admin",
                email = "admin@adoption.com"
            )
            val socialWorkerUser = UserEntity(
                username = "social_worker",
                passwordHash = Security.hashPassword("worker123"),
                role = "Social Worker",
                email = "worker@adoption.com"
            )
            
            val adminId = db.userDao().insert(adminUser).toInt()
            val workerId = db.userDao().insert(socialWorkerUser).toInt()
            
            // Create sample families
            val family1 = FamilyEntity(
                primaryContactName = "John Smith",
                secondaryContactName = "Jane Smith",
                email = "smiths@email.com",
                phone = "555-0123",
                city = "Springfield",
                state = "IL",
                country = "USA"
            )
            val family2 = FamilyEntity(
                primaryContactName = "Mike Johnson",
                email = "mike.j@email.com",
                phone = "555-0456",
                city = "Chicago",
                state = "IL",
                country = "USA"
            )
            
            val family1Id = db.familyDao().insert(family1).toInt()
            val family2Id = db.familyDao().insert(family2).toInt()
            
            // Create sample children
            val child1 = ChildEntity(
                firstName = "Emma",
                lastName = "Wilson",
                gender = "Female",
                dateOfBirth = "2015-03-15"
            )
            val child2 = ChildEntity(
                firstName = "Alex",
                lastName = "Brown",
                gender = "Male",
                dateOfBirth = "2012-08-22"
            )
            val child3 = ChildEntity(
                firstName = "Sofia",
                lastName = "Garcia",
                gender = "Female",
                dateOfBirth = "2018-11-10"
            )
            
            val child1Id = db.childDao().insert(child1).toInt()
            val child2Id = db.childDao().insert(child2).toInt()
            val child3Id = db.childDao().insert(child3).toInt()
            
            // Create sample adoption applications
            val app1 = AdoptionApplicationEntity(
                familyId = family1Id,
                childId = child1Id,
                status = "Under Review",
                notes = "Initial application submitted"
            )
            val app2 = AdoptionApplicationEntity(
                familyId = family2Id,
                childId = child2Id,
                status = "Approved",
                notes = "Background checks completed"
            )
            
            db.adoptionApplicationDao().insert(app1)
            db.adoptionApplicationDao().insert(app2)
            
            // Create sample home studies
            val homeStudy1 = HomeStudyEntity(
                familyId = family1Id,
                result = "Approved",
                notes = "Home meets all safety requirements"
            )
            
            db.homeStudyDao().insert(homeStudy1)
            
            // Create sample documents
            val doc1 = DocumentEntity(
                childId = child1Id,
                documentType = "Birth Certificate",
                fileName = "emma_birth_cert.pdf",
                filePath = "/documents/emma_birth_cert.pdf"
            )
            val doc2 = DocumentEntity(
                childId = child2Id,
                documentType = "Medical Records",
                fileName = "alex_medical.pdf",
                filePath = "/documents/alex_medical.pdf"
            )
            
            db.documentDao().insert(doc1)
            db.documentDao().insert(doc2)
            
            // Create sample placements
            val placement1 = PlacementEntity(
                childId = child1Id,
                startDate = "2024-01-15",
                placementType = "Foster Care"
            )
            
            db.placementDao().insert(placement1)
            
            // Create sample case reports
            val report1 = CaseReportEntity(
                childId = child1Id,
                userId = workerId,
                reportDate = "2024-01-20",
                reportTitle = "Monthly Check-in",
                content = "Child is adjusting well to placement. No concerns noted."
            )
            
            db.caseReportDao().insert(report1)
            
            // Create sample education record
            val edu1 = EducationRecordEntity(
                childId = child2Id,
                schoolName = "Lincoln Elementary",
                grade = "5th Grade"
            )
            
            db.educationRecordDao().insert(edu1)
            
            // Create sample medical record
            val med1 = MedicalRecordEntity(
                childId = child1Id,
                visitDate = "2024-01-10",
                diagnosis = "Routine checkup - healthy"
            )
            
            db.medicalRecordDao().insert(med1)
            
            // Create sample financial record
            val money1 = MoneyRecordEntity(
                childId = child1Id,
                amount = 250.00,
                date = "2024-01-01",
                description = "Monthly allowance"
            )
            
            db.moneyRecordDao().insert(money1)
        }
    }
}

package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.db.dao.DocumentDao
import com.example.adoption_and_childcare.data.db.dao.EducationRecordDao
import com.example.adoption_and_childcare.data.db.dao.MedicalRecordDao
import com.example.adoption_and_childcare.data.db.dao.SiblingDao
import com.example.adoption_and_childcare.data.db.dao.ChildDao
import com.example.adoption_and_childcare.data.db.entities.DocumentEntity
import com.example.adoption_and_childcare.data.db.entities.EducationRecordEntity
import com.example.adoption_and_childcare.data.db.entities.MedicalRecordEntity
import com.example.adoption_and_childcare.data.db.entities.SiblingEntity
import com.example.adoption_and_childcare.data.db.entities.ChildEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for providing detailed data for a specific child's profile.
 *
 * @property medicalRecordDao DAO for accessing medical records.
 * @property educationRecordDao DAO for accessing education records.
 * @property documentDao DAO for accessing documents.
 * @property siblingDao DAO for accessing sibling relationships.
 * @property childDao DAO for accessing child data.
 */
@HiltViewModel
class ChildProfileViewModel @Inject constructor(
    private val medicalRecordDao: MedicalRecordDao,
    private val educationRecordDao: EducationRecordDao,
    private val documentDao: DocumentDao,
    private val siblingDao: SiblingDao,
    private val childDao: ChildDao,
) : ViewModel() {

    /**
     * Internal state for medical records.
     */
    private val _medicalRecords = MutableStateFlow<List<MedicalRecordEntity>>(emptyList())
    /**
     * A flow of medical records associated with the child.
     */
    val medicalRecords: StateFlow<List<MedicalRecordEntity>> = _medicalRecords.asStateFlow()

    /**
     * Internal state for education records.
     */
    private val _educationRecords = MutableStateFlow<List<EducationRecordEntity>>(emptyList())
    /**
     * A flow of education records associated with the child.
     */
    val educationRecords: StateFlow<List<EducationRecordEntity>> = _educationRecords.asStateFlow()

    /**
     * Internal state for documents.
     */
    private val _documents = MutableStateFlow<List<DocumentEntity>>(emptyList())
    /**
     * A flow of documents associated with the child.
     */
    val documents: StateFlow<List<DocumentEntity>> = _documents.asStateFlow()

    /**
     * Internal state for sibling links.
     */
    private val _siblings = MutableStateFlow<List<SiblingEntity>>(emptyList())
    /**
     * A flow of sibling links associated with the child.
     */
    val siblings: StateFlow<List<SiblingEntity>> = _siblings.asStateFlow()

    /**
     * Internal state for sibling child entities.
     */
    private val _siblingChildren = MutableStateFlow<List<ChildEntity>>(emptyList())
    /**
     * A flow of child entities representing the siblings.
     */
    val siblingChildren: StateFlow<List<ChildEntity>> = _siblingChildren.asStateFlow()

    /**
     * Loads all data related to a specific child from the database.
     *
     * @param childId The ID of the child whose data should be loaded.
     */
    fun loadData(childId: Int) {
        viewModelScope.launch {
            medicalRecordDao.observeForChild(childId).collectLatest {
                _medicalRecords.value = it
            }
        }
        viewModelScope.launch {
            educationRecordDao.observeForChild(childId).collectLatest {
                _educationRecords.value = it
            }
        }
        viewModelScope.launch {
            documentDao.observeByChild(childId).collectLatest {
                _documents.value = it
            }
        }
        viewModelScope.launch {
            siblingDao.observeByChildId(childId).collectLatest {
                _siblings.value = it
                val siblingIds = it.map(SiblingEntity::siblingChildId)
                if (siblingIds.isNotEmpty()) {
                    val children = mutableListOf<ChildEntity>()
                    for (id in siblingIds) {
                        childDao.findById(id)?.let(children::add)
                    }
                    _siblingChildren.value = children
                } else {
                    _siblingChildren.value = emptyList()
                }
            }
        }
    }
}

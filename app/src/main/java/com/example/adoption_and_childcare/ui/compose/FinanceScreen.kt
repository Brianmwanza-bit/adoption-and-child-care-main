package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.adoption_and_childcare.R
import com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity
import com.example.adoption_and_childcare.data.repository.MoneyRecordRepositoryImpl
import com.example.adoption_and_childcare.utils.AuthManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.adoption_and_childcare.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch

/**
 * Screen for managing financial records related to child care.
 * Provides functionality to view, add, edit, and delete transactions.
 *
 * @param onBack Callback invoked when the user navigates back.
 * @param viewModel ViewModel for managing finance state and operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    onBack: () -> Unit = {},
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val items by viewModel.moneyRecords.collectAsState(initial = emptyList())
    val children by viewModel.children.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    
    // UI State from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<MoneyRecordEntity?>(null) }
    
    // Resources
    val txnTypeAllowance = stringResource(R.string.txn_type_allowance)
    val payMethodCash = stringResource(R.string.pay_method_cash)
    val payMethodMpesa = stringResource(R.string.pay_method_mpesa)
    val payMethodBank = stringResource(R.string.pay_method_bank)
    val payMethodMobile = stringResource(R.string.pay_method_mobile)
    val payMethodCheque = stringResource(R.string.pay_method_cheque)
    val financeRecordTitleFormat = stringResource(R.string.finance_record_title)
    val financeDateLabelFormat = stringResource(R.string.finance_date_label)
    val naVal = stringResource(R.string.search_na)

    // Form fields
    var selectedChildId by remember { mutableStateOf<Int?>(null) }
    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var date by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var transactionType by remember { mutableStateOf(txnTypeAllowance) }
    var paymentMethod by remember { mutableStateOf(payMethodCash) }
    var mpesaPhoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var mpesaTransactionId by remember { mutableStateOf(TextFieldValue("")) }
    var bankAccount by remember { mutableStateOf(TextFieldValue("")) }
    var bankReference by remember { mutableStateOf(TextFieldValue("")) }

    val transactionTypes = listOf(
        txnTypeAllowance,
        stringResource(R.string.txn_type_education),
        stringResource(R.string.txn_type_medical),
        stringResource(R.string.txn_type_clothing),
        stringResource(R.string.txn_type_other)
    )
    val paymentMethods = listOf(
        payMethodCash,
        payMethodMpesa,
        payMethodBank,
        payMethodMobile,
        payMethodCheque
    )
    var showTransactionTypeDropdown by remember { mutableStateOf(false) }
    var showPaymentMethodDropdown by remember { mutableStateOf(false) }

    val currentSelected = selectedItem
    if (showDetails && currentSelected != null) {
        FinanceDetailScreen(
            recordId = currentSelected.moneyId,
            onBack = { showDetails = false; selectedItem = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.finance_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.finance_back_desc))
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshFromApi() }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.finance_refresh_desc))
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreate = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.finance_add_txn_desc))
                }
            }
        ) { paddingValues ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Financial Summary Section
                if (items.isNotEmpty()) {
                    FinancialSummaryCard(items)
                }

                if (items.isEmpty() && !isLoading) {
                    Box(Modifier.fillMaxSize().height(400.dp), contentAlignment = Alignment.Center) { 
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(stringResource(R.string.finance_no_transactions), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    items.forEachIndexed { index, moneyRecord ->
                        FormRecordCard(
                            title = financeRecordTitleFormat.format(moneyRecord.moneyId),
                            subtitle = financeDateLabelFormat.format(moneyRecord.date),
                            pageNumber = index + 1,
                            onEdit = {
                                selectedItem = moneyRecord
                                showEditDialog = true
                                selectedChildId = moneyRecord.childId
                                amount = TextFieldValue(moneyRecord.amount.toString())
                                date = TextFieldValue(moneyRecord.date)
                                description = TextFieldValue(moneyRecord.description ?: "")
                                transactionType = moneyRecord.transactionType ?: txnTypeAllowance
                                paymentMethod = moneyRecord.paymentMethod ?: payMethodCash
                                mpesaPhoneNumber = TextFieldValue(moneyRecord.mpesaPhoneNumber ?: "")
                                mpesaTransactionId = TextFieldValue(moneyRecord.mpesaTransactionId ?: "")
                                bankAccount = TextFieldValue(moneyRecord.bankAccount ?: "")
                                bankReference = TextFieldValue(moneyRecord.bankReference ?: "")
                            },
                            onDelete = {
                                selectedItem = moneyRecord
                                showDeleteDialog = true
                            },
                            onDownloadPdf = {
                                scope.launch {
                                    // PDF Generation Logic
                                }
                            },
                            headerIcon = Icons.Default.AttachMoney,
                            onClick = {
                                selectedItem = moneyRecord
                                showDetails = true
                            }
                        ) {
                        FormDetailRow(label = stringResource(R.string.reports_label_child_id), value = moneyRecord.childId.toString())
                        val child = children.find { it.childId == moneyRecord.childId }
                        if (child != null) {
                            FormDetailRow(label = stringResource(R.string.reports_label_child_name), value = "${child.firstName} ${child.lastName}")
                        }
                        FormDetailRow(label = stringResource(R.string.finance_amount_field), value = stringResource(R.string.finance_amount_label, moneyRecord.amount), valueColor = MaterialTheme.colorScheme.primary)
                        FormDetailRow(label = stringResource(R.string.finance_type_field), value = moneyRecord.transactionType ?: naVal)
                        FormDetailRow(label = stringResource(R.string.finance_payment_method_field), value = moneyRecord.paymentMethod ?: naVal)
                        
                        if (!moneyRecord.mpesaTransactionId.isNullOrBlank()) {
                            FormDetailRow(label = stringResource(R.string.finance_mpesa_txn_id_field), value = moneyRecord.mpesaTransactionId)
                        }
                        
                        if (!moneyRecord.description.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.finance_desc_field),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = moneyRecord.description,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        FinancialLegacyDetails(moneyRecord)
                    }
                }
            }
        }
    }
}

    // Create Transaction Dialog
    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text(stringResource(R.string.finance_add_txn_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SearchableChildSelector(
                        children = children,
                        selectedChildId = selectedChildId,
                        onChildSelected = { selectedChildId = it.childId }
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(stringResource(R.string.finance_amount_field)) },
                        prefix = { Text(stringResource(R.string.finance_kes_prefix)) },
                        singleLine = true
                    )
                    ExposedDropdownMenuBox(
                        expanded = showTransactionTypeDropdown,
                        onExpandedChange = { showTransactionTypeDropdown = !showTransactionTypeDropdown }
                    ) {
                        OutlinedTextField(
                            value = transactionType,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.finance_type_field)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTransactionTypeDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showTransactionTypeDropdown,
                            onDismissRequest = { showTransactionTypeDropdown = false }
                        ) {
                            transactionTypes.forEach { typeOption ->
                                DropdownMenuItem(text = { Text(typeOption) }, onClick = {
                                    transactionType = typeOption
                                    showTransactionTypeDropdown = false
                                })
                            }
                        }
                    }
                    ExposedDropdownMenuBox(
                        expanded = showPaymentMethodDropdown,
                        onExpandedChange = { showPaymentMethodDropdown = !showPaymentMethodDropdown }
                    ) {
                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.finance_payment_method_field)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPaymentMethodDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showPaymentMethodDropdown,
                            onDismissRequest = { showPaymentMethodDropdown = false }
                        ) {
                            paymentMethods.forEach { methodOption ->
                                DropdownMenuItem(text = { Text(methodOption) }, onClick = {
                                    paymentMethod = methodOption
                                    showPaymentMethodDropdown = false
                                })
                            }
                        }
                    }
                    // M-Pesa fields
                    if (paymentMethod == payMethodMpesa) {
                        OutlinedTextField(
                            value = mpesaPhoneNumber,
                            onValueChange = { mpesaPhoneNumber = it },
                            label = { Text(stringResource(R.string.finance_mpesa_phone_field)) },
                            prefix = { Text("+254 ") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = mpesaTransactionId,
                            onValueChange = { mpesaTransactionId = it },
                            label = { Text(stringResource(R.string.finance_mpesa_txn_id_field)) },
                            singleLine = true
                        )
                    }
                    // Bank fields
                    if (paymentMethod == payMethodBank || 
                        paymentMethod == payMethodMobile || 
                        paymentMethod == payMethodCheque) {
                        OutlinedTextField(
                            value = bankAccount,
                            onValueChange = { bankAccount = it },
                            label = { Text(stringResource(R.string.finance_bank_account_field)) },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = bankReference,
                            onValueChange = { bankReference = it },
                            label = { Text(stringResource(R.string.finance_bank_ref_field)) },
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text(stringResource(R.string.finance_date_field)) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(R.string.finance_desc_field)) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val cid = selectedChildId
                    val amt = amount.text.toDoubleOrNull()
                    if (cid != null && amt != null && date.text.isNotBlank()) {
                        viewModel.insertMoneyRecord(
                            MoneyRecordEntity(
                                childId = cid,
                                amount = amt,
                                transactionType = transactionType,
                                date = date.text,
                                description = description.text.ifBlank { null },
                                paymentMethod = paymentMethod,
                                mpesaPhoneNumber = if (paymentMethod == payMethodMpesa) mpesaPhoneNumber.text else null,
                                mpesaTransactionId = if (paymentMethod == payMethodMpesa && mpesaTransactionId.text.isNotBlank()) mpesaTransactionId.text else null,
                                bankAccount = if (paymentMethod == payMethodBank || paymentMethod == payMethodMobile || paymentMethod == payMethodCheque) bankAccount.text else null,
                                bankReference = if (paymentMethod == payMethodBank || paymentMethod == payMethodMobile || paymentMethod == payMethodCheque) bankReference.text else null
                            )
                        )
                        showCreate = false
                        // Reset fields
                        selectedChildId = null
                        amount = TextFieldValue("")
                        date = TextFieldValue("")
                        description = TextFieldValue("")
                        mpesaPhoneNumber = TextFieldValue("")
                        mpesaTransactionId = TextFieldValue("")
                        bankAccount = TextFieldValue("")
                        bankReference = TextFieldValue("")
                        transactionType = txnTypeAllowance
                        paymentMethod = payMethodCash
                    }
                }) { Text(stringResource(R.string.finance_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text(stringResource(R.string.finance_cancel)) }
            }
        )
    }

    // Edit Transaction Dialog
    if (showEditDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.finance_edit_txn_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SearchableChildSelector(
                        children = children,
                        selectedChildId = selectedChildId,
                        onChildSelected = { selectedChildId = it.childId },
                        label = stringResource(R.string.finance_child_readonly)
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(stringResource(R.string.finance_amount_field)) },
                        prefix = { Text(stringResource(R.string.finance_kes_prefix)) },
                        singleLine = true
                    )
                    ExposedDropdownMenuBox(
                        expanded = showTransactionTypeDropdown,
                        onExpandedChange = { showTransactionTypeDropdown = !showTransactionTypeDropdown }
                    ) {
                        OutlinedTextField(
                            value = transactionType,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.finance_type_field)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTransactionTypeDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showTransactionTypeDropdown,
                            onDismissRequest = { showTransactionTypeDropdown = false }
                        ) {
                            transactionTypes.forEach { typeOption ->
                                DropdownMenuItem(text = { Text(typeOption) }, onClick = {
                                    transactionType = typeOption
                                    showTransactionTypeDropdown = false
                                })
                            }
                        }
                    }
                    ExposedDropdownMenuBox(
                        expanded = showPaymentMethodDropdown,
                        onExpandedChange = { showPaymentMethodDropdown = !showPaymentMethodDropdown }
                    ) {
                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(stringResource(R.string.finance_payment_method_field)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPaymentMethodDropdown) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showPaymentMethodDropdown,
                            onDismissRequest = { showPaymentMethodDropdown = false }
                        ) {
                            paymentMethods.forEach { methodOption ->
                                DropdownMenuItem(text = { Text(methodOption) }, onClick = {
                                    paymentMethod = methodOption
                                    showPaymentMethodDropdown = false
                                })
                            }
                        }
                    }
                    // M-Pesa fields
                    if (paymentMethod == payMethodMpesa) {
                        OutlinedTextField(
                            value = mpesaPhoneNumber,
                            onValueChange = { mpesaPhoneNumber = it },
                            label = { Text(stringResource(R.string.finance_mpesa_phone_field)) },
                            prefix = { Text("+254 ") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = mpesaTransactionId,
                            onValueChange = { mpesaTransactionId = it },
                            label = { Text(stringResource(R.string.finance_mpesa_txn_id_field)) },
                            singleLine = true
                        )
                    }
                    // Bank fields
                    if (paymentMethod == payMethodBank || 
                        paymentMethod == payMethodMobile || 
                        paymentMethod == payMethodCheque) {
                        OutlinedTextField(
                            value = bankAccount,
                            onValueChange = { bankAccount = it },
                            label = { Text(stringResource(R.string.finance_bank_account_field)) },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = bankReference,
                            onValueChange = { bankReference = it },
                            label = { Text(stringResource(R.string.finance_bank_ref_field)) },
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text(stringResource(R.string.finance_date_field)) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(R.string.finance_desc_field)) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amt = amount.text.toDoubleOrNull()
                    val cid = selectedChildId
                    if (amt != null && date.text.isNotBlank() && cid != null) {
                        val currentItem = selectedItem ?: return@TextButton
                        val updated = currentItem.copy(
                            childId = cid,
                            amount = amt,
                            transactionType = transactionType,
                            date = date.text,
                            description = description.text.ifBlank { null },
                            paymentMethod = paymentMethod,
                            mpesaPhoneNumber = if (paymentMethod == payMethodMpesa) mpesaPhoneNumber.text else null,
                            mpesaTransactionId = if (paymentMethod == payMethodMpesa && mpesaTransactionId.text.isNotBlank()) mpesaTransactionId.text else null,
                            bankAccount = if (paymentMethod == payMethodBank || paymentMethod == payMethodMobile || paymentMethod == payMethodCheque) bankAccount.text else null,
                            bankReference = if (paymentMethod == payMethodBank || paymentMethod == payMethodMobile || payMethodCheque == paymentMethod) bankReference.text else null
                        )
                        viewModel.updateMoneyRecord(updated)
                        showEditDialog = false
                        selectedItem = null
                        selectedChildId = null
                    }
                }) { Text(stringResource(R.string.finance_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; selectedItem = null }) { Text(stringResource(R.string.finance_cancel)) }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && selectedItem != null) {
        val currentItem = selectedItem ?: return
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; selectedItem = null },
            title = { Text(stringResource(R.string.finance_delete_txn_title)) },
            text = { Text(stringResource(R.string.finance_delete_txn_confirm, currentItem.moneyId, currentItem.amount)) },
            confirmButton = {
                TextButton(onClick = {
                    selectedItem?.let {
                        viewModel.deleteMoneyRecord(it.moneyId)
                    }
                    showDeleteDialog = false
                    selectedItem = null
                }) { 
                    Text(stringResource(R.string.finance_delete), color = MaterialTheme.colorScheme.error) 
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; selectedItem = null }) { Text(stringResource(R.string.finance_cancel)) }
            }
        )
    }
}

/**
 * Helper function to fetch finance records from API.
 * 
 * @param repository The finance repository implementation.
 * @param authManager Manager for authentication tokens.
 * @param scope Coroutine scope for network operations.
 * @param onLoading Callback to update loading state and error messages.
 */
fun fetchFromApi(
    repository: MoneyRecordRepositoryImpl,
    authManager: AuthManager,
    scope: kotlinx.coroutines.CoroutineScope,
    onLoading: (Boolean, String?) -> Unit
) {
    scope.launch {
        onLoading(true, null)
        try {
            val token = authManager.getAuthToken() ?: ""
            if (token.isNotEmpty()) {
                val result = repository.fetchFromApi(token)
                if (result.isFailure) {
                    onLoading(false, result.exceptionOrNull()?.message)
                } else {
                    onLoading(false, null)
                }
            } else {
                onLoading(false, null)
            }
        } catch (_: Exception) {
            onLoading(false, null)
        }
    }
}

/**
 * Card displaying a summary of financial data.
 *
 * @param items List of financial records.
 */
@Composable
fun FinancialSummaryCard(items: List<MoneyRecordEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.finance_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val total = items.sumOf { it.amount }
                Text(stringResource(R.string.finance_amount_field))
                Text(stringResource(R.string.finance_amount_label, total), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val count = items.size
                Text(stringResource(R.string.dashboard_items_count, count))
                Text("$count", fontWeight = FontWeight.Medium)
            }
        }
    }
}

/**
 * Displays legacy financial details if available.
 *
 * @param moneyRecord The financial record entity.
 */
@Composable
fun FinancialLegacyDetails(moneyRecord: MoneyRecordEntity) {
    if (!moneyRecord.bankAccount.isNullOrBlank()) {
        FormDetailRow(label = stringResource(R.string.finance_bank_account_field), value = moneyRecord.bankAccount)
    }
    if (!moneyRecord.bankReference.isNullOrBlank()) {
        FormDetailRow(label = stringResource(R.string.finance_bank_ref_field), value = moneyRecord.bankReference)
    }
}

package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.yourdomain.adoptionchildcare.R
import com.example.adoption_and_childcare.data.db.AppDatabase
import com.example.adoption_and_childcare.data.db.entities.MoneyRecordEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for managing financial records related to child care.
 * Provides functionality to view, add, edit, and delete transactions.
 *
 * @param onBack Callback invoked when the user navigates back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var items by remember { mutableStateOf<List<MoneyRecordEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<MoneyRecordEntity?>(null) }
    
    // Form fields
    var childId by remember { mutableStateOf(TextFieldValue("")) }
    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var date by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var transactionType by remember { mutableStateOf(context.getString(R.string.txn_type_allowance)) }
    var paymentMethod by remember { mutableStateOf(context.getString(R.string.pay_method_cash)) }
    var mpesaPhoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var mpesaTransactionId by remember { mutableStateOf(TextFieldValue("")) }
    var bankAccount by remember { mutableStateOf(TextFieldValue("")) }
    var bankReference by remember { mutableStateOf(TextFieldValue("")) }

    val transactionTypes = listOf(
        stringResource(R.string.txn_type_allowance),
        stringResource(R.string.txn_type_education),
        stringResource(R.string.txn_type_medical),
        stringResource(R.string.txn_type_clothing),
        stringResource(R.string.txn_type_other)
    )
    val paymentMethods = listOf(
        stringResource(R.string.pay_method_cash),
        stringResource(R.string.pay_method_mpesa),
        stringResource(R.string.pay_method_bank),
        stringResource(R.string.pay_method_mobile),
        stringResource(R.string.pay_method_cheque)
    )
    var showTransactionTypeDropdown by remember { mutableStateOf(false) }
    var showPaymentMethodDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.moneyRecordDao().observeAll().collectLatest { items = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.finance_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.finance_back_desc))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.finance_add_txn_desc))
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(16.dp).padding(padding)) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.finance_no_transactions), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { moneyRecord ->
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.padding(12.dp).weight(1f)) {
                                    Text(stringResource(R.string.finance_txn_id, moneyRecord.moneyId), style = MaterialTheme.typography.titleMedium)
                                    Text(stringResource(R.string.finance_child_id_label, moneyRecord.childId), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(stringResource(R.string.finance_amount_label, moneyRecord.amount), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                                    Text(stringResource(R.string.finance_type_label, moneyRecord.transactionType ?: ""), style = MaterialTheme.typography.bodySmall)
                                    Text(stringResource(R.string.finance_date_label, moneyRecord.date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    moneyRecord.description?.let { Text(stringResource(R.string.finance_note_label, it), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline) }
                                }
                                Row(verticalAlignment = Alignment.Top) {
                                    IconButton(onClick = {
                                        selectedItem = moneyRecord
                                        showEditDialog = true
                                        childId = TextFieldValue(moneyRecord.childId.toString())
                                        amount = TextFieldValue(moneyRecord.amount.toString())
                                        date = TextFieldValue(moneyRecord.date)
                                        description = TextFieldValue(moneyRecord.description ?: "")
                                        transactionType = moneyRecord.transactionType ?: context.getString(R.string.txn_type_allowance)
                                        paymentMethod = moneyRecord.paymentMethod ?: context.getString(R.string.pay_method_cash)
                                        mpesaPhoneNumber = TextFieldValue(moneyRecord.mpesaPhoneNumber ?: "")
                                        mpesaTransactionId = TextFieldValue(moneyRecord.mpesaTransactionId ?: "")
                                        bankAccount = TextFieldValue(moneyRecord.bankAccount ?: "")
                                        bankReference = TextFieldValue(moneyRecord.bankReference ?: "")
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.finance_edit_desc), tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = {
                                        selectedItem = moneyRecord
                                        showDeleteDialog = true
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.finance_delete_desc), tint = MaterialTheme.colorScheme.error)
                                    }
                                    IconButton(onClick = {
                                        // Save to file implementation
                                        scope.launch {
                                            // TODO: Save to device storage
                                        }
                                    }) {
                                        Icon(Icons.Default.Download, contentDescription = stringResource(R.string.finance_download_desc), tint = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
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
                    OutlinedTextField(
                        value = childId,
                        onValueChange = { childId = it },
                        label = { Text(stringResource(R.string.finance_child_id_field)) },
                        singleLine = true
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
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showTransactionTypeDropdown,
                            onDismissRequest = { showTransactionTypeDropdown = false }
                        ) {
                            transactionTypes.forEach { type ->
                                DropdownMenuItem(text = { Text(type) }, onClick = {
                                    transactionType = type
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
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showPaymentMethodDropdown,
                            onDismissRequest = { showPaymentMethodDropdown = false }
                        ) {
                            paymentMethods.forEach { method ->
                                DropdownMenuItem(text = { Text(method) }, onClick = {
                                    paymentMethod = method
                                    showPaymentMethodDropdown = false
                                })
                            }
                        }
                    }
                    // M-Pesa fields
                    if (paymentMethod == stringResource(R.string.pay_method_mpesa)) {
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
                    if (paymentMethod == stringResource(R.string.pay_method_bank) || 
                        paymentMethod == stringResource(R.string.pay_method_mobile) || 
                        paymentMethod == stringResource(R.string.pay_method_cheque)) {
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
                    val cid = childId.text.toIntOrNull()
                    val amt = amount.text.toDoubleOrNull()
                    if (cid != null && amt != null && date.text.isNotBlank()) {
                        scope.launch {
                            val moneyRecord = MoneyRecordEntity(
                                childId = cid,
                                amount = amt,
                                transactionType = transactionType,
                                date = date.text,
                                description = description.text.ifBlank { null },
                                paymentMethod = paymentMethod,
                                mpesaPhoneNumber = if (paymentMethod == context.getString(R.string.pay_method_mpesa)) mpesaPhoneNumber.text else null,
                                mpesaTransactionId = if (paymentMethod == context.getString(R.string.pay_method_mpesa) && mpesaTransactionId.text.isNotBlank()) mpesaTransactionId.text else null,
                                bankAccount = if (paymentMethod == context.getString(R.string.pay_method_bank) || paymentMethod == context.getString(R.string.pay_method_mobile) || paymentMethod == context.getString(R.string.pay_method_cheque)) bankAccount.text else null,
                                bankReference = if (paymentMethod == context.getString(R.string.pay_method_bank) || paymentMethod == context.getString(R.string.pay_method_mobile) || paymentMethod == context.getString(R.string.pay_method_cheque)) bankReference.text else null
                            )
                            db.moneyRecordDao().insert(moneyRecord)
                            showCreate = false
                            // Reset fields
                            childId = TextFieldValue("")
                            amount = TextFieldValue("")
                            date = TextFieldValue("")
                            description = TextFieldValue("")
                            mpesaPhoneNumber = TextFieldValue("")
                            mpesaTransactionId = TextFieldValue("")
                            bankAccount = TextFieldValue("")
                            bankReference = TextFieldValue("")
                            transactionType = context.getString(R.string.txn_type_allowance)
                            paymentMethod = context.getString(R.string.pay_method_cash)
                        }
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
                    OutlinedTextField(
                        value = childId,
                        onValueChange = { childId = it },
                        label = { Text(stringResource(R.string.finance_child_id_field)) },
                        singleLine = true,
                        enabled = false // Cannot change child ID
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
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showTransactionTypeDropdown,
                            onDismissRequest = { showTransactionTypeDropdown = false }
                        ) {
                            transactionTypes.forEach { type ->
                                DropdownMenuItem(text = { Text(type) }, onClick = {
                                    transactionType = type
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
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showPaymentMethodDropdown,
                            onDismissRequest = { showPaymentMethodDropdown = false }
                        ) {
                            paymentMethods.forEach { method ->
                                DropdownMenuItem(text = { Text(method) }, onClick = {
                                    paymentMethod = method
                                    showPaymentMethodDropdown = false
                                })
                            }
                        }
                    }
                    // M-Pesa fields
                    if (paymentMethod == stringResource(R.string.pay_method_mpesa)) {
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
                    if (paymentMethod == stringResource(R.string.pay_method_bank) || 
                        paymentMethod == stringResource(R.string.pay_method_mobile) || 
                        paymentMethod == stringResource(R.string.pay_method_cheque)) {
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
                    if (amt != null && date.text.isNotBlank()) {
                        scope.launch {
                            val currentItem = selectedItem ?: return@launch
                            val updated = currentItem.copy(
                                amount = amt,
                                transactionType = transactionType,
                                date = date.text,
                                description = description.text.ifBlank { null },
                                paymentMethod = paymentMethod,
                                mpesaPhoneNumber = if (paymentMethod == context.getString(R.string.pay_method_mpesa)) mpesaPhoneNumber.text else null,
                                mpesaTransactionId = if (paymentMethod == context.getString(R.string.pay_method_mpesa) && mpesaTransactionId.text.isNotBlank()) mpesaTransactionId.text else null,
                                bankAccount = if (paymentMethod == context.getString(R.string.pay_method_bank) || paymentMethod == context.getString(R.string.pay_method_mobile) || paymentMethod == context.getString(R.string.pay_method_cheque)) bankAccount.text else null,
                                bankReference = if (paymentMethod == context.getString(R.string.pay_method_bank) || paymentMethod == context.getString(R.string.pay_method_mobile) || context.getString(R.string.pay_method_cheque) == paymentMethod) bankReference.text else null
                            )
                            db.moneyRecordDao().update(updated)
                            showEditDialog = false
                            selectedItem = null
                        }
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
                    scope.launch {
                        db.moneyRecordDao().deleteById(currentItem.moneyId)
                        showDeleteDialog = false
                        selectedItem = null
                    }
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

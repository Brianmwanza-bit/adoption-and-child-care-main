package com.example.adoption_and_childcare.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TermsAndConditionsScreen(onAccept: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Terms & Conditions",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = """
                    Welcome to Adoption & Child Care. By using this application, you agree to the following terms:
                    
                    1. Data Privacy: All data entered regarding children and families is strictly confidential and protected under child welfare regulations.
                    
                    2. Professional Use: This app is intended for authorized case workers, social workers, and foster parents. Unauthorized access is prohibited.
                    
                    3. Reporting: Users are responsible for the accuracy of reports and case notes entered into the system.
                    
                    4. Security: You agree not to share your login credentials and to notify administration of any security breaches.
                    
                    5. Compliance: Users must comply with all local and international laws regarding child adoption and foster care management.
                    
                    By clicking Accept, you confirm that you have read and understood these terms.
                """.trimIndent(),
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAccept,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("I Accept the Terms", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PermissionsScreen(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(80.dp))
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Permissions & Regulations",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "To ensure full functionality and legal compliance, this app requires access to certain device features.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        PermissionItem("Location Access", "Used for mapping family homes and case worker tracking.")
        PermissionItem("Camera & Gallery", "Required for uploading child and document photos.")
        PermissionItem("Storage", "Used to save and export case reports safely.")

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Allow and Continue", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PermissionItem(title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("•", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF2196F3))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(description, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

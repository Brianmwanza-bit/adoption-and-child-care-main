package com.example.adoption_and_childcare

import org.junit.Test
import java.io.File

class ExampleUnitTest {
    @Test
    fun replaceThemeColorsWithBlack() {
        val composeDir = File("src/main/java/com/example/adoption_and_childcare/ui/compose")
        val targetDir = if (composeDir.exists()) composeDir else File("app/src/main/java/com/example/adoption_and_childcare/ui/compose")
        if (!targetDir.exists()) return

        targetDir.walkTopDown().forEach { file ->
            if (file.isFile && file.name.endsWith(".kt")) {
                var content = file.readText()
                var modified = false

                val secondaryRegex = Regex("""\bMaterialTheme\.colorScheme\.secondary\b(?!Container|Variant)""")
                if (secondaryRegex.containsMatchIn(content)) {
                    content = secondaryRegex.replace(content, "Color.Black")
                    modified = true
                }

                val onSurfaceVariantRegex = Regex("""\bMaterialTheme\.colorScheme\.onSurfaceVariant\b""")
                if (onSurfaceVariantRegex.containsMatchIn(content)) {
                    content = onSurfaceVariantRegex.replace(content, "Color.Black")
                    modified = true
                }

                val onSurfaceRegex = Regex("""\bMaterialTheme\.colorScheme\.onSurface\b""")
                if (onSurfaceRegex.containsMatchIn(content)) {
                    content = onSurfaceRegex.replace(content, "Color.Black")
                    modified = true
                }

                val outlineRegex = Regex("""\bMaterialTheme\.colorScheme\.outline\b""")
                if (outlineRegex.containsMatchIn(content)) {
                    content = outlineRegex.replace(content, "Color.Black")
                    modified = true
                }

                if (modified) {
                    if (!content.contains("import androidx.compose.ui.graphics.Color")) {
                        val pkgIdx = content.indexOf("package ")
                        if (pkgIdx != -1) {
                            val endLineIdx = content.indexOf("\n", pkgIdx)
                            if (endLineIdx != -1) {
                                content = content.substring(0, endLineIdx + 1) + "\nimport androidx.compose.ui.graphics.Color\n" + content.substring(endLineIdx + 1)
                            }
                        }
                    }
                    file.writeText(content)
                    println("Updated theme colors in: ${file.name}")
                }
            }
        }
    }
}

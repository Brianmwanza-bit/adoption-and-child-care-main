# Dashboard UI Specification & Visual Guide

## Header Section

```
┌──────────────────────────────────────────────────────────────┐
│ ☰ MENU  │  🔍 Search cases, children, families...     │ 🔔 👤 │
└──────────────────────────────────────────────────────────────┘
```

---

## Status Bar (Blue Header)

```
┌────────────────────────────────────────────────────────────┐
│ Color: #2196F3 (Blue)                                       │
├─────────────┬──────────────┬─────────────┬─────────────────┤
│             │              │             │                 │
│   3 Urgent  │  5 Overdue   │  4 Scheduled│  1 Message     │
│   Cases     │  Tasks       │  Today      │                 │
│             │              │             │                 │
├─────────────┴──────────────┴─────────────┴─────────────────┤
│  Created by: HeaderStatCard components                       │
│  Spacing: 16dp between items                                 │
│  Font: WhiteSpace(0.9f) alpha text                          │
└────────────────────────────────────────────────────────────┘
```

---

## Search Bar Section

```
┌────────────────────────────────────────────────────────────┐
│ 🔍 Search cases, children, families...           ✕        │
└────────────────────────────────────────────────────────────┘
  Height: 48dp
  Shape: RoundedCornerShape(12.dp)
  Border: #2196F3 when focused, #BDBDBD when unfocused
  Leading Icon: Search icon (Blue)
  Trailing Icon: Clear (X) when text entered
```

---

## Urgent Alerts Section

```
┌────────────────────────────────────────────────────────────┐
│ 🚨 Require Immediate Attention                             │
├────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────────────────┐ Badge: RED               │
│  │ Emma Smith          [WAITING] │ Border: RED              │
│  │ Smith Family                 │                          │
│  │ Days: 45  • Deadline: 2024...│                          │
│  │ Assigned: Sarah Chen  ❯      │                          │
│  └──────────────────────────────┘                          │
│                                                              │
│  ┌──────────────────────────────┐ Badge: ORANGE            │
│  │ Noah Johnson        [IN PLACE]│ Border: ORANGE           │
│  │ Foster Kellers               │                          │
│  │ Days: 15  • Deadline: 2024...│                          │
│  │ Assigned: Mike Wilson  ❯     │                          │
│  └──────────────────────────────┘                          │
│                                                              │
│  ⚠️ 3 Tasks Overdue                                        │
│  Action required today              ❯                      │
│  Background: #FFE BEE (light red)                          │
│  Border: #E91E63 (red)                                     │
│                                                              │
└────────────────────────────────────────────────────────────┘
```

---

## Today's Workload Section

```
┌────────────────────────────────────────────────────────────┐
│ 📋 Today's Workload                                         │
├────────────────────────────────────────────────────────────┤
│                                                              │
│  ● Home Study Review - Smith Family                        │
│    Emma S. • Today                                         │
│    Background: White                                       │
│    Left dot: RED (urgent)                                  │
│                                                              │
│  ● Medical Follow-up - Noah J.                            │
│    Noah J. • Tomorrow                                      │
│    Background: White                                       │
│    Left dot: RED (urgent)                                  │
│                                                              │
└────────────────────────────────────────────────────────────┘
```

---

## All Action Items Section

```
┌────────────────────────────────────────────────────────────┐
│ All Action Items                                            │
├────────────────────────────────────────────────────────────┤
│                                                              │
│  ● Home Study Review - Smith Family          Urgent       │
│    Case #001 • Today • Sarah Chen                          │
│                                                              │
│  ● Medical Follow-up - Noah J.              Urgent        │
│    Case #002 • Tomorrow • Dr. Brown                        │
│                                                              │
│  ● Document Collection - Sophia B.          High          │
│    Case #003 • In 2 days • You                            │
│                                                              │
│  ● Placement Stability Check                Normal        │
│    Case #002 • In 7 days • Mike Wilson                    │
│                                                              │
└────────────────────────────────────────────────────────────┘
```

---

## System Modules Grid

```
┌────────────────────────────────────────────────────────────┐
│ System Modules                                              │
├────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────┬──────────────┐                           │
│  │ 👶           │ 👨‍👩‍👧‍👦      │                           │
│  │ 12            │ 8             │                          │
│  │ Active Cases  │ Awaiting      │                          │
│  │ Total active  │ Placement     │                          │
│  │              │              │                           │
│  │ Color: Blue  │ Color: Orange │                          │
│  ├──────────────┼──────────────┤                           │
│  │ 🏠           │ 📋            │                          │
│  │ 5             │ 3             │                          │
│  │ In Placement  │ Home Studies  │                          │
│  │ Currently     │ Pending       │                          │
│  │              │              │                           │
│  │ Color: Green  │ Color: Purple │                          │
│  └──────────────┴──────────────┘                           │
│                                                              │
│  ... (continues for all 10 modules)                        │
│                                                              │
│  Each card:                                                │
│  - Width: 50% of screen (2-column)                        │
│  - Spacing: 10dp between cards                            │
│  - Border: 1dp color.copy(alpha=0.5)                      │
│  - Background: color.copy(alpha=0.1)                      │
│  - Padding: 12dp                                          │
│  - Rounded corners: 12dp                                  │
│                                                              │
└────────────────────────────────────────────────────────────┘
```

---

## Footer Action Bar (Always Visible)

```
┌──────────────────────────────────────────────────────────────┐
│                                                                │
│  ➕      ✎      ✓      ⬆      ≡                             │
│ New Case  Log    Approve Upload  Home                        │
│          Visit              Study                            │
│                                                                │
│  Color 1  Color 2  Color 3  Color 4  Color 5               │
│  #2196F3  #4CAF50  #9C27B0  #FF9800  #E91E63               │
│                                                                │
│  Height: 75dp                                               │
│  Background: White                                          │
│  Elevation: 8dp (shadow)                                    │
│  Each button: 1/5 width, centered content                 │
│  Icons: 22dp size                                           │
│  Font: 9sp (labelSmall)                                     │
│  Max lines: 1 (truncated if needed)                        │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

---

## Color Swatches

### Critical/Urgent (#E91E63)
```
████████ Card background: #FFE BEE (light pink, alpha 0.1)
████████ Border: #E91E63 (solid)
████████ Text: #E91E63 (bold)
████████ Badge: #E91E63 (white text on color)
```

### High Priority (#FF9800)
```
████████ Card background: #FFF3E0 (light orange, alpha 0.1)
████████ Border: #FF9800 (solid)
████████ Text: #FF9800 (semi-bold)
████████ Badge: #FF9800 (white text on color)
```

### Normal/Success (#4CAF50)
```
████████ Card background: #F1F8E9 (light green, alpha 0.1)
████████ Border: #4CAF50 (solid)
████████ Text: #4CAF50 (normal)
████████ Badge: #4CAF50 (white text on color)
```

---

## Responsive Breakpoints

### Mobile (< 600dp)
```
- Full width cards
- 1-column module grid (stack vertically)
- Search bar: Full width
- Footer: Horizontal scroll if needed
```

### Tablet (600dp - 900dp)
```
- Max width: 600dp, centered
- 2-column module grid
- Larger padding: 20dp
- Search bar: Full width
```

### Desktop (> 900dp)
```
- Max width: 800dp, centered
- 3-column module grid
- Larger padding: 32dp
- Sidebar navigation option
```

---

## Animation & Transitions

### Dashboard Load:
```
1. Header fades in (200ms)
2. Search bar slides down (300ms)
3. Urgent alerts fade in staggered (400ms each)
4. Modules fade in (600ms)
5. Footer appears from bottom (400ms)
```

### Button Interactions:
```
- Tap feedback: Color darken by 20% for 100ms
- Press effect: Scale 0.95 during press
- Ripple: Material ripple effect with color
```

### List Item Reveal:
```
- Slide in from left: 200ms
- Fade in with slide: Staggered by 50ms
```

---

## Typography

### Header Text
```
Font: Jetpack Compose default
Style: Bold
Size: 14sp (titleMedium/titleSmall)
Color: #2196F3 or specific card color
```

### Body Text
```
Font: Jetpack Compose default
Style: Normal
Size: 12sp-14sp (bodySmall/bodyMedium)
Color: #333 or #666 (gray text)
```

### Labels
```
Font: Jetpack Compose default
Style: Normal
Size: 8sp-10sp (labelSmall)
Color: #999 (light gray)
```

### Counts (Numbers)
```
Font: Jetpack Compose default
Style: Bold
Size: 16sp-24sp (headlineSmall to headlineMedium)
Color: Card-specific color
```

---

## Spacing Standards

```
Margin (outside container):      16dp
Padding (inside container):      12dp
Between sections:               16dp
Between cards:                  10dp-12dp
Between items:                  8dp
Card corner radius:             10dp-12dp
Border width:                   1dp-1.5dp
Icon size (large):              28dp
Icon size (medium):             22dp-24dp
Icon size (small):              18dp-20dp
Touch target minimum:           44x44dp
```

---

## Component Library

### DashboardCard Data Class
```kotlin
data class DashboardCard(
    val title: String,           // "Children", "Families", etc.
    val icon: ImageVector,       // Icons.Default.ChildCare
    val count: Int,              // 12, 8, 5, etc.
    val summary: String,         // "Total children", "Registered..."
    val color: Color,            // Color(0xFF4CAF50)
    val route: String? = null    // "children_list", "families"
)
```

### ActionItem Data Class
```kotlin
data class ActionItem(
    val id: String,
    val title: String,           // "Home Study Review - Smith Family"
    val priority: String,        // "urgent", "high", "normal"
    val dueDate: String,        // "Today", "Tomorrow", "In 2 days"
    val assignee: String,       // "You", "Sarah Chen", "Dr. Brown"
    val caseId: String = "",    // "001", "002", etc.
    val childName: String = ""  // "Emma S.", "Noah J.", etc.
)
```

### CaseStatus Data Class
```kotlin
data class CaseStatus(
    val id: String,
    val childName: String,       // "Emma Smith"
    val status: String,          // "Waiting for match", "In placement"
    val urgency: String,         // "critical", "high", "normal"
    val daysInStatus: Int,       // 45, 15, 7
    val nextDeadline: String,    // "2024-07-30"
    val assignee: String,        // "Sarah Chen"
    val familyName: String = ""  // "Smith Family"
)
```

---

## Accessibility

### Color Independence
- Don't rely only on color; use icons and text labels
- Include text badges ("URGENT", "HIGH", etc.)
- Use emoji for quick scanning (🚨, ⚠️, 📋)

### Text Size
- Minimum 14sp for body text
- Minimum 12sp for labels
- Zoomable up to 200%

### Touch Targets
- All buttons minimum 44x44dp
- Adequate spacing between interactive elements
- Visual feedback on interaction

### Screen Reader
- All icons have content descriptions
- Headings properly nested (H1, H2, H3)
- Links have descriptive text
- Form fields have labels

---

## Dark Mode Support

```kotlin
if (isDarkMode) {
    // Apply dark theme
    CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    TextColor = Color.White
    BorderColor = Color(0xFF404040)
} else {
    // Apply light theme
    CardDefaults.cardColors(containerColor = Color.White)
    TextColor = Color.Black
    BorderColor = Color(0xFFBDBDBD)
}
```

---

## Final Notes

- Dashboard prioritizes **action over information**
- **Visual hierarchy** guides user attention
- **One-tap access** to frequent tasks
- **Color consistency** aids recognition
- **Responsive design** adapts to all screen sizes
- **Accessibility first** - inclusive design for all users

**Design Ready for Development!** ✨
# Placement and Family System Documentation

## Overview
The Adoption & Child Care system models child placements and family relationships to track:
1. **Source Family** - The family from which a child is removed (due to care issues, abuse, neglect, etc.)
2. **Destination Family** - The family to which a child is placed or adopted (foster home, adoptive family, kinship care, etc.)

## Core Concepts

### Family Profile
All families (source or destination) are stored in the `family_profile` table:

```sql
CREATE TABLE family_profile (
  family_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  address VARCHAR(255),
  household_size INT,
  notes TEXT,
  latitude DOUBLE,
  longitude DOUBLE,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

**Fields:**
- `family_id`: Unique identifier for the family profile
- `user_id`: Links to the system user account managing/representing the family
- `address`: Family residence address
- `household_size`: Number of household members
- `notes`: Additional notes about family (capacity, preferences, restrictions, etc.)
- `latitude/longitude`: Geographic location for mapping/analysis

### Placement Record
Placements track child movements between families and care environments:

```sql
CREATE TABLE placements (
  placement_id INT AUTO_INCREMENT PRIMARY KEY,
  child_id INT NOT NULL,
  source_family_id INT DEFAULT NULL,
  destination_family_id INT NOT NULL,
  placement_type VARCHAR(50) CHECK (placement_type IN (
    'Foster Home', 'Group Home', 'Kinship Care', 'Institution', 'Adoption'
  )),
  start_date DATE NOT NULL,
  end_date DATE DEFAULT NULL,
  organization VARCHAR(150),
  placement_address TEXT,
  contact_person VARCHAR(100),
  contact_phone VARCHAR(20),
  contact_email VARCHAR(100),
  notes TEXT,
  is_current TINYINT(1) DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by INT,
  FOREIGN KEY (child_id) REFERENCES children(child_id) ON DELETE CASCADE,
  FOREIGN KEY (source_family_id) REFERENCES family_profile(family_id),
  FOREIGN KEY (destination_family_id) REFERENCES family_profile(family_id),
  FOREIGN KEY (created_by) REFERENCES users(user_id)
);
```

**Fields:**
- `placement_id`: Unique identifier for the placement
- `child_id`: Foreign key to the child being placed (NOT NULL)
- `source_family_id`: Family profile ID of the family from which child is removed (can be NULL for initial placements)
- `destination_family_id`: Family profile ID of the family receiving the child (NOT NULL)
- `placement_type`: Type of placement (Foster Home, Group Home, Kinship Care, Institution, or Adoption)
- `start_date`: Date placement began
- `end_date`: Date placement ended (NULL if ongoing)
- `organization`: Name of organization managing placement (for institutions/group homes)
- `placement_address`: Physical address of placement location
- `contact_person`: Name of primary contact at placement
- `contact_phone`: Phone number of contact
- `contact_email`: Email of contact
- `notes`: Additional placement-specific notes
- `is_current`: Flag indicating if this is the child's current placement
- `created_by`: User ID of case worker who created the placement record

## Placement Types

### 1. **Foster Home**
- Child temporarily placed with a licensed foster family
- Source family typically cited (child removed from home)
- Destination family is the foster family

### 2. **Kinship Care**
- Child placed with relatives or close family friends
- Occurs when source family cannot provide care but relatives can
- Maintains family connections while ensuring child safety

### 3. **Group Home**
- Child placed in a supervised group living facility
- May be used for short-term care or children with special needs
- Usually managed by an organization

### 4. **Institution**
- Child placed in a residential treatment facility, children's home, or institution
- Often used for therapeutic care or when specialized services needed
- Organization field typically populated

### 5. **Adoption**
- Permanent placement with adoptive family
- Ends involvement of biological/source family (legally)
- Source family required for record; destination family is adoptive family

## Relationships and Workflow

### Initial Removal & Placement
```
Child in Home Environment (Source Family)
    ↓
[Incident/Issue Identified]
    ↓
Child Removed (create placement record)
    - source_family_id: Original family profile
    - destination_family_id: New placement location
    - start_date: Date of removal
    - placement_type: Appropriate type
```

### Placement Transition
```
Current Placement (Destination Family A)
    ↓
[Placement Ends - Better Option Available]
    ↓
New Placement Created
    - source_family_id: Previous destination (Destination Family A)
    - destination_family_id: New family/location
    - start_date: Date of transition
    - Previous placement end_date: Set to transition date
```

### Adoption Finalization
```
Foster Home Placement
    ↓
[Adoption Process Completed]
    ↓
Create Final Placement Record
    - placement_type: 'Adoption'
    - source_family_id: Original family (for legal record)
    - destination_family_id: Adoptive family
    - end_date: NULL (permanent)
    - is_current: 1
```

## Foster System Integration

### Foster Tasks
Track action items for foster families:

```sql
CREATE TABLE foster_tasks (
  task_id INT AUTO_INCREMENT PRIMARY KEY,
  family_id INT NOT NULL,
  case_worker_id INT,
  description TEXT,
  status VARCHAR(50),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  due_date DATETIME,
  FOREIGN KEY (family_id) REFERENCES family_profile(family_id),
  FOREIGN KEY (case_worker_id) REFERENCES users(user_id)
);
```

Tasks might include:
- Required training or certifications
- Medical appointments for child
- Documentation updates
- Home safety checks
- Supervision visits

### Foster Matches
Track family-child compatibility assessments and matching processes:

```sql
CREATE TABLE foster_matches (
  match_id INT AUTO_INCREMENT PRIMARY KEY,
  family_id INT NOT NULL,
  case_worker_id INT,
  task_id INT,
  status VARCHAR(50),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (family_id) REFERENCES family_profile(family_id),
  FOREIGN KEY (case_worker_id) REFERENCES users(user_id),
  FOREIGN KEY (task_id) REFERENCES foster_tasks(task_id)
);
```

## API Endpoints

### Create Placement
```
POST /api/placements
{
  "child_id": 5,
  "source_family_id": 2,           // Family child removed from
  "destination_family_id": 8,      // Family child placed with
  "placement_type": "Foster Home",
  "start_date": "2025-08-15",
  "placement_address": "123 Oak Lane, City, State",
  "contact_person": "Jane Doe",
  "contact_phone": "555-0123",
  "contact_email": "jane@example.com",
  "notes": "Child has separation anxiety, requires continuity"
}
```

### Update Placement
```
PUT /api/placements/:placement_id
{
  "end_date": "2025-12-15",
  "notes": "Placement ended; child adopted by destination family"
}
```

### Get Placement History
```
GET /api/children/:child_id/placements
Returns: Array of all placements for child (oldest to newest)
```

### Get Current Placement
```
GET /api/children/:child_id/placements?current=true
Returns: Child's current active placement
```

## Best Practices

1. **Always Record Source Family**: Even in initial placements, attempt to identify and record the source family for legal/historical purposes.

2. **Use Appropriate Placement Types**: Select placement_type accurately as it affects benefits, supervision requirements, and legal status.

3. **Set end_date When Placement Changes**: When a child transitions to a new placement, set the `end_date` of the previous placement to enable accurate placement history tracking.

4. **Contact Information**: Keep contact_person, contact_phone, and contact_email current for case worker access.

5. **Notes for Context**: Use the notes field to document:
   - Behavioral needs
   - Health conditions
   - Special requirements
   - Cultural/religious considerations
   - Sibling relationships

6. **Location Data**: For geographic analysis and emergency response, include accurate addresses and optionally latitude/longitude.

7. **Audit Trail**: All placement changes are logged in `audit_logs` for compliance and investigation purposes.

## Examples

### Example 1: Initial Foster Placement After Removal
```sql
-- Create family profiles
INSERT INTO family_profile (user_id, address, household_size, notes)
VALUES 
  (3, '456 Parent Lane, City, State', 4, 'Source family - history of neglect'),
  (7, '789 Foster Ave, City, State', 3, 'Licensed foster home, experienced');

-- Record child removal and placement
INSERT INTO placements 
  (child_id, source_family_id, destination_family_id, placement_type, start_date, 
   placement_address, contact_person, contact_phone, notes, created_by)
VALUES
  (12, 1, 2, 'Foster Home', '2025-08-15',
   '789 Foster Ave, City, State', 'John & Sarah Smith', '555-0789',
   'Emergency removal due to neglect. Child very anxious. Needs counseling.',
   5);
```

### Example 2: Transitioning to Adoption
```sql
-- Update previous placement
UPDATE placements 
SET end_date = '2025-11-30'
WHERE placement_id = 45;

-- Create adoption placement
INSERT INTO placements
  (child_id, source_family_id, destination_family_id, placement_type, start_date,
   placement_address, contact_person, contact_phone, notes, is_current, created_by)
VALUES
  (12, 1, 2, 'Adoption', '2025-12-01',
   '789 Foster Ave, City, State', 'John & Sarah Smith', '555-0789',
   'Adoption finalized. Child now permanent member of family.',
   1, 5);
```

### Example 3: Kinship Care Placement
```sql
INSERT INTO family_profile (user_id, address, household_size, notes)
VALUES (11, '321 Relative Road, City, State', 5, 'Kinship care provider - paternal grandmother');

INSERT INTO placements
  (child_id, source_family_id, destination_family_id, placement_type, start_date,
   placement_address, contact_person, contact_phone, notes, created_by)
VALUES
  (15, 4, 3, 'Kinship Care', '2025-09-20',
   '321 Relative Road, City, State', 'Maria Garcia', '555-0456',
   'Placed with paternal grandmother. Maintains family connection.',
   5);
```

## Database Import

To set up this schema in a new database, use the provided import script:

```powershell
# Windows PowerShell
& ".\database\import-db.ps1"
```

The script will:
1. Create the `adoption_and_childcare_tracking_system_db` database
2. Import all tables, relationships, and constraints
3. Seed initial system data (admin user, permissions, etc.)

## Support and Troubleshooting

- **Orphaned Records**: If a source family is deleted while placement records reference it, use CASCADE DELETE policies (already configured).
- **Placement Conflicts**: Ensure a child cannot have overlapping active placements (enforce via application logic).
- **Audit Trail**: Query `audit_logs` to track all placement changes for compliance audits.
- **Performance**: Add indexes on `child_id`, `source_family_id`, `destination_family_id` for faster queries on large datasets.

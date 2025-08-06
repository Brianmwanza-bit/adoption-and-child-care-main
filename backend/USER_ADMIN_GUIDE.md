# Adoption & Child Care App: User & Admin Guide

## Getting Started

### Login
- Open the app and enter your username and password.
- If you do not have an account, contact your administrator.

### Navigation
- Use the bottom navigation bar to access Dashboard, Analytics, Notifications, Map, and User Management (admin only).
- Tap the menu or profile icon for settings and help.

## Common Workflows

### Add a Child
1. Go to the Children section.
2. Tap the "+" or "Add Child" button.
3. Fill in the child's details and select a photo.
4. Tap "Save" to add the child to the system.

### Upload a Document
1. Go to the Documents section.
2. Tap the "+" or "Add Document" button.
3. Enter document details and select a file or photo.
4. Tap "Save" to upload.

### Assign a Placement
1. Go to the Placements section.
2. Tap the "+" or "Add Placement" button.
3. Select the child and family, set status, and save.

### Request a Background Check
1. Go to the Background Checks section.
2. Tap the "+" or "Request Check" button.
3. Enter the user or person details and submit.

## Admin Tasks

### User Management
- Go to User Management (admin only).
- Add, edit, or delete users.
- Assign roles and permissions.

### Analytics
- Go to Analytics to view summary stats, placements over time, and children by status.
- Use the retry button if data fails to load.

### Notifications
- Go to Notifications to view system alerts.
- Mark notifications as read or mark all as read.

## Demo Data
- To populate demo data, use the "+" buttons in each section to add sample children, families, placements, etc.
- Admins can use the backend API or database tools to import bulk demo data if needed.

## Troubleshooting / FAQ

**Q: I can't log in.**
- Check your username and password.
- Contact your admin to reset your account.

**Q: The app says 'Too many requests.'**
- Wait a few minutes and try again (rate limiting is enabled for security).

**Q: I see 'No data' or 'Failed to load.'**
- Check your internet connection.
- Tap the retry button or refresh the app.

**Q: How do I reset my password?**
- Contact your admin; password reset is not self-service.

**Q: How do I deploy the backend?**
- See `backend/README.md` and use `./deploy.sh` for Docker deployment.

**Q: Where can I get help?**
- Contact your system administrator or refer to this guide. 
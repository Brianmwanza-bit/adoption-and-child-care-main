# Manual Device Testing Checklist

Use this checklist to verify the app works as expected on real Android and iOS devices.

## General
- [ ] App installs and launches without errors
- [ ] App icon and splash screen display correctly
- [ ] App name is correct

## Authentication
- [ ] Login with valid credentials
- [ ] Login fails with invalid credentials
- [ ] Logout works

## Navigation
- [ ] Bottom navigation bar works (Dashboard, Analytics, Notifications, Map, User Management)
- [ ] All screens load without errors

## CRUD Operations
- [ ] Add, edit, and delete a child
- [ ] Add, edit, and delete a document (file/photo upload)
- [ ] Add, edit, and delete a placement
- [ ] Add, edit, and delete a family profile
- [ ] Add, edit, and delete a background check

## Notifications
- [ ] Receive and view notifications
- [ ] Mark notifications as read/unread
- [ ] Unread count updates correctly

## Analytics
- [ ] Analytics data loads and displays correctly
- [ ] Retry button works if data fails to load

## Map & Matching
- [ ] Map displays family and user locations
- [ ] Matching logic works and results display

## Background Checks
- [ ] Request a background check
- [ ] View status and results

## Offline/Connectivity
- [ ] App handles loss of internet gracefully (shows error, retries, etc.)
- [ ] Data syncs when connection is restored

## Permissions
- [ ] Camera, storage, and location permissions are requested and handled
- [ ] App does not crash if permissions are denied

## UI/UX
- [ ] All buttons, dialogs, and forms are usable
- [ ] Text is readable and UI is accessible (screen reader, color contrast)
- [ ] App works in both portrait and landscape modes

## Other
- [ ] Push notifications (if enabled) are received
- [ ] Crash reporting (if enabled) works
- [ ] App does not drain battery excessively

---

**Test on multiple devices and OS versions for best results.** 
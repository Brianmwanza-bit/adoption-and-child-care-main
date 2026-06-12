# Remote Access Guide: Connecting App to DB Across Networks

By default, the app and the database server must be on the same Wi-Fi network to communicate using local IP addresses (like `192.168.x.x`). 

If you want the app to work when the phone is on **Mobile Data (4G/5G)** or a **different Wi-Fi**, you need to expose your local PC server to the internet.

---

## Option 1: Using a Tunnel (Recommended for Development)

A tunnel creates a secure, public URL that points directly to your PC. This is the easiest way to test the app remotely.

### 1. Using Ngrok
1. Download **Ngrok** from [ngrok.com](https://ngrok.com/).
2. Run your backend server on your PC (port `50000`).
3. Open a terminal and type:
   ```bash
   ngrok http 50000
   ```
4. Ngrok will give you a "Forwarding" address like: `https://a1b2-c3d4.ngrok-free.app`.
5. Copy this URL.

### 2. Update the App Settings
1. Open the Adoption & Child Care app on your phone.
2. Go to **Settings** > **Network & API**.
3. Paste the Ngrok URL into the **API Base URL** field.
4. Tap **Save Network Settings**.
5. Press the **Sync** button in the footer. It will now work from anywhere in the world!

---

## Option 2: Cloud Hosting (Recommended for Production)

For a real-world app, you shouldn't host the database on your personal laptop. Instead, move the Backend and MySQL Database to a Cloud Provider.

### Suggested Providers:
*   **Heroku / Render / Railway:** Good for hosting Node.js backends quickly.
*   **DigitalOcean / AWS / Google Cloud:** For full control over a Virtual Private Server (VPS).
*   **A2 Hosting / Bluehost:** If they support Node.js and MySQL.

### Deployment Steps:
1. Upload your `backend/` folder to the cloud server.
2. Export your local MySQL database using phpMyAdmin and import it into the cloud MySQL service.
3. Update the cloud environment variables (`.env`) to point to the cloud database.
4. Use the cloud-provided domain (e.g., `https://api.adoptioncare.ke`) in the app's settings.

---

## Option 3: VPN (Tailscale)

If you only want specific people to access the DB across networks without making it public:
1. Install **Tailscale** on your PC and your Phone.
2. Tailscale will give your PC a "Magic DNS" name or a static internal IP.
3. Use that Tailscale IP in the app's settings.
4. This works even on mobile data as long as Tailscale is "Connected" on both devices.

---

## Summary for the "Sync" Button
The **Sync** button in your app is designed to fetch the most recent server URL from the internal `AppSettings`. As long as you have a valid Public URL or Tunnel active, the button will successfully link the app to your database records across any network.

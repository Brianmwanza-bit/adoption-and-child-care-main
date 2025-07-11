// script.js
// All event listeners and logic are now wrapped in DOMContentLoaded for reliability

document.addEventListener('DOMContentLoaded', function() {
  // --- NAV BUTTONS ---
document.getElementById('about-btn').addEventListener('click', function() {
    const aboutText = `The Adoption and Child Care Tracking System is a digital solution designed to streamline the management of adoption services and child welfare processes. It serves as a centralized platform where information about children awaiting adoption, ongoing cases, and care arrangements can be securely stored and accessed by authorized users. The system helps professionals stay organized and ensures that critical details about each child's background, legal status, and personal needs are readily available.\n\nOne of the system's core functions is maintaining detailed child profiles. These profiles include personal data, health records, educational progress, legal documents, and social histories. By gathering this information in one place, the platform reduces paperwork and minimizes the risk of losing important records. This holistic view of a child's circumstances helps social workers, legal professionals, and caregivers make well-informed decisions that prioritize each child's best interests.\n\nAnother key feature is case tracking. The system allows users to monitor the progress of adoption processes, legal proceedings, and care placements. Alerts and notifications keep staff updated about upcoming court dates, document deadlines, or required assessments. This proactive approach ensures that cases move forward without unnecessary delays and that children spend less time waiting for permanent, stable homes.\n\nMoreover, the platform supports collaboration among multiple stakeholders. Different user roles can be set up so that social workers, legal advisors, medical staff, and administrative personnel can securely share information while maintaining data privacy. This improves communication and fosters a coordinated approach to addressing the complex needs of children involved in adoption and child care services.\n\nOverall, the Adoption and Child Care Tracking System is an innovative tool that enhances efficiency, accountability, and transparency in child welfare work. By integrating data management with process monitoring and stakeholder collaboration, it helps organizations provide higher-quality services, protect sensitive information, and, most importantly, ensure that children receive timely, appropriate care and support on their journey toward permanent families.`;
    const aboutWindow = window.open('', '_blank', 'width=600,height=700');
    aboutWindow.document.write('<html><head><title>About - Adoption and Child Care Tracking System</title><style>body{font-family:Arial,sans-serif;padding:2em;line-height:1.6;}h1{color:#2d6a4f;}</style></head><body>');
    aboutWindow.document.write('<h1>About the System</h1>');
    aboutWindow.document.write('<p>' + aboutText.replace(/\n/g, '<br><br>') + '</p>');
    aboutWindow.document.write('</body></html>');
    aboutWindow.document.close();
}); 

document.querySelectorAll('.nav-item').forEach(function(item) {
  if (item.textContent.includes('Search')) {
    item.addEventListener('click', function() {
      document.getElementById('search-modal').style.display = 'block';
    });
  }
    if (item.textContent.includes('Camera')) {
    item.addEventListener('click', function() {
      document.getElementById('video-modal').style.display = 'block';
    });
  }
    if (item.textContent.includes('Home')) {
      item.addEventListener('click', function() {
        document.getElementById('search-modal').style.display = 'none';
        document.getElementById('video-modal').style.display = 'none';
        document.getElementById('feature-content').innerHTML = '';
        window.scrollTo({ top: 0, behavior: 'smooth' });
      });
    }
  });

  document.getElementById('close-search-modal').addEventListener('click', function() {
    document.getElementById('search-modal').style.display = 'none';
});
document.getElementById('close-video-modal').addEventListener('click', function() {
  document.getElementById('video-modal').style.display = 'none';
  stopCamera();
  document.getElementById('camera-container').innerHTML = '';
});

  // --- SEARCH FORM ---
  document.getElementById('search-form').addEventListener('submit', function(e) {
    e.preventDefault();
    const query = document.getElementById('search-query').value;
    fetch(`${API_BASE}/children?search=${encodeURIComponent(query)}`)
      .then(res => res.json())
      .then(data => {
        document.getElementById('feature-content').innerHTML =
          '<h3>Search Results</h3>' +
          (data.length ? '<ul>' + data.map(child => `<li>${child.first_name} ${child.last_name}</li>`).join('') + '</ul>' : '<p>No results found.</p>');
      })
      .catch(err => alert('Search error: ' + err.message));
  });

  // --- CAMERA MODAL BUTTONS ---
document.getElementById('take-picture-btn').addEventListener('click', function() {
    let facingMode = 'user';
  function openCamera() {
    const popup = window.open('', '', 'width=400,height=700');
    popup.document.write(`
      <html><head><title>Camera - Picture</title>
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <style>
        html, body { height: 100%; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background: #000; color: #fff; height: 100vh; width: 100vw; overflow: hidden; position: relative; }
        h2 { color: #fff; text-align: center; margin-top: 0.5em; }
        .close-btn { position: absolute; top: 1em; left: 1em; font-size: 2em; cursor: pointer; z-index: 10; color: #fff; background: rgba(0,0,0,0.3); border-radius: 50%; padding: 0.2em 0.5em; }
        #switch-camera-btn { position: absolute; top: 1em; right: 1em; z-index: 10; background: #222; color: #fff; border: none; border-radius: 50%; padding: 0.7em 1em; font-size: 1em; cursor: pointer; }
        #camera-container { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 80vh; width: 100vw; margin-top: 2.5em; }
        video { width: 100vw; max-width: 100vw; height: 60vh; object-fit: cover; border-radius: 1em; background: #222; }
        #capture-btn { position: absolute; left: 50%; bottom: 2em; transform: translateX(-50%); background: transparent !important; color: #fff; border: none; border-radius: 50%; width: 64px; height: 64px; font-size: 2em; cursor: pointer; z-index: 10; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 8px rgba(0,0,0,0.3); }
        img { width: 100vw; max-width: 100vw; height: auto; border-radius: 1em; margin-top: 2em; }
      </style></head><body>
        <span class="close-btn" id="close-popup">&times;</span>
        <button id="switch-camera-btn" type="button">🔄</button>
        <h2>Camera - Picture</h2>
        <div id="camera-container"></div>
        <button id="capture-btn" type="button">📸</button>
        <script>
          let facingMode = '${facingMode}';
          let currentStream = null;
          function startCamera() {
  const container = document.getElementById('camera-container');
  container.innerHTML = '';
  const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
  const isHttps = window.location.protocol === 'https:';
  if (!isLocalhost && !isHttps) {
    container.innerHTML = '<p style="color:red;">Camera access requires HTTPS or localhost.<br>On mobile, use a secure tunnel like <a href="https://ngrok.com/" target="_blank">ngrok</a>.<br>On desktop, use http://localhost or https:// for full camera support.</p>';
    return;
            } else if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    container.innerHTML = '<p>Camera API not supported in this browser.<br>Try Chrome, Firefox, or Edge.</p>';
    return;
  }
            navigator.mediaDevices.getUserMedia({ video: { facingMode: facingMode } }).then(stream => {
              currentStream = stream;
    const video = document.createElement('video');
    video.autoplay = true;
              video.playsInline = true;
    video.srcObject = stream;
              video.style.background = '#222';
    container.appendChild(video);
            }).catch(err => {
              container.innerHTML = '<p>Camera access denied or not available.</p>';
            });
          }
          document.getElementById('close-popup').onclick = function() { if(currentStream) currentStream.getTracks().forEach(track => track.stop()); window.close(); };
          document.getElementById('switch-camera-btn').onclick = function() {
            if(currentStream) currentStream.getTracks().forEach(track => track.stop());
            facingMode = (facingMode === 'user') ? 'environment' : 'user';
            startCamera();
          };
          document.getElementById('capture-btn').onclick = function() {
            const video = document.querySelector('#camera-container video');
            if (!video) return;
      const canvas = document.createElement('canvas');
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      const ctx = canvas.getContext('2d');
      ctx.drawImage(video, 0, 0);
      // Watermark settings
      const watermarkText = 'ADOPTION AND CHILD CARE TRACKING SYSTEM';
      const fontSize = Math.floor(canvas.height * 0.035); // Small font
      ctx.font = 'bold ' + fontSize + 'px Arial';
      const textWidth = ctx.measureText(watermarkText).width;
      const padding = 8;
      const blockHeight = fontSize + padding * 1.2;
      // Draw pink block
      ctx.fillStyle = '#c77dff';
      ctx.globalAlpha = 0.85;
      ctx.fillRect(padding, canvas.height - blockHeight - padding, textWidth + padding * 2, blockHeight);
      ctx.globalAlpha = 1.0;
      // Draw yellow text
      ctx.fillStyle = '#fff700';
      ctx.fillText(watermarkText, padding * 2, canvas.height - padding - fontSize * 0.3);
      // Create image
      const img = document.createElement('img');
      img.src = canvas.toDataURL('image/png');
            document.getElementById('camera-container').innerHTML = '';
            document.getElementById('camera-container').appendChild(img);
            // Prompt to save
            setTimeout(function() {
              if (confirm('Photo taken! Do you want to save this photo to your device?')) {
                const a = document.createElement('a');
                a.href = img.src;
                a.download = 'adoption_child_care_photo.png';
                a.style.display = 'none';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
    }
            }, 300);
            // TODO: Upload img.src to backend for DB storage
            if(currentStream) currentStream.getTracks().forEach(track => track.stop());
          };
          startCamera();
        </script>
      </body></html>
    `);
    popup.document.close();
  }
  openCamera();
});

document.getElementById('record-video-btn').addEventListener('click', function() {
  let facingMode = 'user';
  function openCamera() {
    const popup = window.open('', '', 'width=400,height=700');
    popup.document.write(`
      <html><head><title>Camera - Video</title>
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <style>
        html, body { height: 100%; margin: 0; padding: 0; }
        body { font-family: Arial, sans-serif; background: #000; color: #fff; height: 100vh; width: 100vw; overflow: hidden; position: relative; }
        h2 { color: #fff; text-align: center; margin-top: 0.5em; }
        .close-btn { position: absolute; top: 1em; left: 1em; font-size: 2em; cursor: pointer; z-index: 10; color: #fff; background: rgba(0,0,0,0.3); border-radius: 50%; padding: 0.2em 0.5em; }
        #switch-camera-btn { position: absolute; top: 1em; right: 1em; z-index: 10; background: #222; color: #fff; border: none; border-radius: 50%; padding: 0.7em 1em; font-size: 1em; cursor: pointer; }
        #camera-container { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 80vh; width: 100vw; margin-top: 2.5em; }
        video { width: 100vw; max-width: 100vw; height: 60vh; object-fit: cover; border-radius: 1em; background: #222; }
        #capture-btn, #rec-btn { position: absolute; left: 50%; bottom: 2em; transform: translateX(-50%); background: transparent !important; color: #fff; border: none; border-radius: 50%; width: 64px; height: 64px; font-size: 2em; cursor: pointer; z-index: 10; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 8px rgba(0,0,0,0.3); }
        img { width: 100vw; max-width: 100vw; height: auto; border-radius: 1em; margin-top: 2em; }
      </style></head><body>
        <span class="close-btn" id="close-popup">&times;</span>
        <button id="switch-camera-btn" type="button">🔄</button>
        <h2>Camera - Video</h2>
        <div id="camera-container"></div>
        <button id="rec-btn" type="button">⏺️</button>
        <script>
          let facingMode = '${facingMode}';
          let currentStream = null;
    let mediaRecorder;
    let chunks = [];
    let recording = false;
    let timer;
          function startCamera() {
            const container = document.getElementById('camera-container');
            container.innerHTML = '';
            const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
            const isHttps = window.location.protocol === 'https:';
            if (!isLocalhost && !isHttps) {
              container.innerHTML = '<p style="color:red;">Camera access requires HTTPS or localhost.<br>On mobile, use a secure tunnel like <a href="https://ngrok.com/" target="_blank">ngrok</a>.<br>On desktop, use http://localhost or https:// for full camera support.</p>';
              return;
            } else if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
              container.innerHTML = '<p>Camera API not supported in this browser.<br>Try Chrome, Firefox, or Edge.</p>';
              return;
            }
            navigator.mediaDevices.getUserMedia({ video: { facingMode: facingMode }, audio: true }).then(stream => {
              currentStream = stream;
              const video = document.createElement('video');
              video.autoplay = true;
              video.playsInline = true;
              video.srcObject = stream;
              video.style.background = '#222';
              container.appendChild(video);
            }).catch(err => {
              container.innerHTML = '<p>Camera access denied or not available.</p>';
            });
          }
          document.getElementById('close-popup').onclick = function() { if(currentStream) currentStream.getTracks().forEach(track => track.stop()); window.close(); };
          document.getElementById('switch-camera-btn').onclick = function() {
            if(currentStream) currentStream.getTracks().forEach(track => track.stop());
            facingMode = (facingMode === 'user') ? 'environment' : 'user';
            startCamera();
          };
          document.getElementById('rec-btn').onclick = function() {
            const video = document.querySelector('#camera-container video');
            if (!video) return;
      if (!recording) {
              mediaRecorder = new MediaRecorder(currentStream);
              mediaRecorder.ondataavailable = function(e) { chunks.push(e.data); };
        mediaRecorder.onstop = function() {
          const blob = new Blob(chunks, { type: 'video/webm' });
          const url = URL.createObjectURL(blob);
          const recordedVideo = document.createElement('video');
          recordedVideo.controls = true;
          recordedVideo.src = url;
                document.getElementById('camera-container').innerHTML = '';
                document.getElementById('camera-container').appendChild(recordedVideo);
                if (currentStream) currentStream.getTracks().forEach(track => track.stop());
        };
        mediaRecorder.start();
        recording = true;
              document.getElementById('rec-btn').textContent = 'Stop Recording';
        timer = setTimeout(() => {
          if (mediaRecorder && recording) {
            mediaRecorder.stop();
            recording = false;
          }
        }, 20 * 60 * 1000); // 20 minutes
      } else {
        mediaRecorder.stop();
        recording = false;
        clearTimeout(timer);
              document.getElementById('rec-btn').textContent = '⏺️';
            }
          };
          startCamera();
        </script>
      </body></html>
    `);
    popup.document.close();
  }
  openCamera();
});

  // --- FEATURE BUTTONS ---
  const API_BASE = 'http://192.168.43.244:8888';
  const featureTableMap = {
    'court': 'court_cases',
    'children': 'children',
    'placement': 'placements',
    'medical-health': 'medical_records',
    'mental-health': 'guardians',
    'case-reports': 'case_reports',
    'money': 'money_records',
    'education': 'education_records',
    'users': 'users',
    'audit-logs': 'audit_logs',
    'permissions': 'permissions',
    'user-permissions': 'user_permissions',
    'documents': 'documents',
    'user': null // handled separately
  };
document.querySelectorAll('.feature-card').forEach(function(btn) {
    const feature = btn.getAttribute('data-feature');
    if (feature === 'user') {
      btn.addEventListener('click', function() {
        document.getElementById('user-modal').style.display = 'block';
      });
        return;
    }
    if (featureTableMap[feature]) {
      btn.addEventListener('click', function() {
        fetch(`${API_BASE}/${featureTableMap[feature]}`)
          .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
          })
          .then(data => {
            const contentDiv = document.getElementById('feature-content');
      if (Array.isArray(data) && data.length > 0) {
              contentDiv.innerHTML = `<h3>${btn.querySelector('.feature-label').textContent} List</h3>` +
                '<ul>' + data.map(row => `<li>${Object.values(row).join(' | ')}</li>`).join('') + '</ul>';
            } else {
              contentDiv.innerHTML = `<p>No ${btn.querySelector('.feature-label').textContent.toLowerCase()} found.</p>`;
            }
          })
          .catch(error => {
            document.getElementById('feature-content').innerHTML = `<p style=\"color:red;\">Error fetching ${btn.querySelector('.feature-label').textContent.toLowerCase()}: ${error.message}</p>`;
          });
      });
    }
  });

  // --- USERS MODAL ---
  const usersBtn = document.querySelector('.feature-card[data-feature="users"]');
  if (usersBtn) {
    usersBtn.addEventListener('click', function() {
      const usersModal = document.getElementById('users-modal');
      const usersListBlock = document.getElementById('users-list-block');
      // List of roles
      const roles = [
        'Admin',
        'Caseworker',
        'Guardian',
        'Judge',
        'Lawyer',
        'Medical Expert',
        'Police'
      ];
      usersListBlock.innerHTML = roles.map(role =>
        `<button class='user-list-btn' type='button'>${role}</button>`
      ).join('');
      usersModal.style.display = 'block';
      document.querySelectorAll('.user-list-btn').forEach((btn, idx) => {
        btn.addEventListener('click', function() {
          alert(roles[idx] + ' button clicked!');
        });
      });
    });
  }
  document.getElementById('close-users-modal').addEventListener('click', function() {
    document.getElementById('users-modal').style.display = 'none';
  });

  // --- USER MODAL ---
  document.getElementById('close-user-modal')?.addEventListener('click', function() {
    document.getElementById('user-modal').style.display = 'none';
  });
  // User role buttons
  const userRoleActions = {
    'Admin': () => alert('Admin button clicked!'),
    'Caseworker': () => alert('Caseworker button clicked!'),
    'Guardian': () => alert('Guardian button clicked!'),
    'Judge': () => alert('Judge button clicked!'),
    'Lawyer': () => alert('Lawyer button clicked!'),
    'Medical Expert': () => alert('Medical Expert button clicked!'),
    'Police': () => alert('Police button clicked!')
  };
  document.querySelectorAll('.user-role-btn').forEach(btn => {
    btn.addEventListener('click', function() {
      const role = btn.textContent.trim();
      if (userRoleActions[role]) {
        userRoleActions[role]();
      } else {
        alert(role + ' button clicked!');
      }
    });
  });

  // --- HOME BUTTON DOUBLE TAP (optional) ---
  let homeTapTimeout = null;
  let homeTapCount = 0;
  document.querySelector('.nav-item .nav-label').parentElement.addEventListener('click', function() {
    homeTapCount++;
    if (homeTapCount === 1) {
      homeTapTimeout = setTimeout(() => {
        // Single tap: clear feature-content
        document.getElementById('feature-content').innerHTML = '';
        homeTapCount = 0;
      }, 300);
    } else if (homeTapCount === 2) {
      clearTimeout(homeTapTimeout);
      // Double tap: fetch dashboard
      fetch(`${API_BASE}/dashboard`)
        .then(res => res.json())
        .then(data => {
          document.getElementById('feature-content').innerHTML =
            `<h3>Dashboard</h3><pre>${JSON.stringify(data, null, 2)}</pre>`;
        })
        .catch(err => {
          document.getElementById('feature-content').innerHTML = `<p style='color:red;'>Dashboard error: ${err.message}</p>`;
        });
      homeTapCount = 0;
    }
  });

  // --- CAMERA STOP FUNCTION ---
  function stopCamera() {
    const video = document.querySelector('#camera-container video');
    if (video && video.srcObject) {
      video.srcObject.getTracks().forEach(track => track.stop());
    }
  }

  // --- UPLOAD IMAGE TO BACKEND (for camera) ---
  function uploadImageToBackend(dataUrl) {
    fetch(`${API_BASE}/documents/upload`, {
      method: 'POST',
      body: (() => {
        const formData = new FormData();
        const byteString = atob(dataUrl.split(',')[1]);
        const mimeString = dataUrl.split(',')[0].split(':')[1].split(';')[0];
        const ab = new ArrayBuffer(byteString.length);
        const ia = new Uint8Array(ab);
        for (let i = 0; i < byteString.length; i++) ia[i] = byteString.charCodeAt(i);
        const blob = new Blob([ab], { type: mimeString });
        formData.append('file', blob, 'photo.png');
        formData.append('child_id', 1); // Example, update as needed
        return formData;
      })()
    })
    .then(res => res.json())
    .then(data => alert('Upload successful!'))
    .catch(err => alert('Upload failed: ' + err.message));
  }

  // Patch into camera code: after photo taken, call uploadImageToBackend(img.src)
}); 
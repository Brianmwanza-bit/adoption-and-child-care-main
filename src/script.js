// script.js
// Add interactivity here later 

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
});
document.getElementById('close-search-modal').addEventListener('click', function() {
  document.getElementById('search-modal').style.display = 'none';
});
document.getElementById('search-form').addEventListener('submit', function(e) {
  e.preventDefault();
  // Add search logic here later
  alert('Search functionality coming soon!');
}); 

document.querySelectorAll('.nav-item').forEach(function(item) {
  if (item.textContent.includes('Video')) {
    item.addEventListener('click', function() {
      document.getElementById('video-modal').style.display = 'block';
    });
  }
});
document.getElementById('close-video-modal').addEventListener('click', function() {
  document.getElementById('video-modal').style.display = 'none';
  stopCamera();
  document.getElementById('camera-container').innerHTML = '';
});

function stopCamera() {
  const video = document.querySelector('#camera-container video');
  if (video && video.srcObject) {
    video.srcObject.getTracks().forEach(track => track.stop());
  }
}

document.getElementById('take-picture-btn').addEventListener('click', function() {
  const container = document.getElementById('camera-container');
  container.innerHTML = '';
  navigator.mediaDevices.getUserMedia({ video: true }).then(stream => {
    const video = document.createElement('video');
    video.autoplay = true;
    video.srcObject = stream;
    container.appendChild(video);
    const snapBtn = document.createElement('button');
    snapBtn.textContent = 'Capture Photo';
    snapBtn.style.marginTop = '1em';
    container.appendChild(snapBtn);
    snapBtn.onclick = function() {
      const canvas = document.createElement('canvas');
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      canvas.getContext('2d').drawImage(video, 0, 0);
      const img = document.createElement('img');
      img.src = canvas.toDataURL('image/png');
      container.innerHTML = '';
      container.appendChild(img);
      stopCamera();
    };
  }).catch(err => {
    container.innerHTML = '<p>Camera access denied or not available.</p>';
  });
});

document.getElementById('record-video-btn').addEventListener('click', function() {
  const container = document.getElementById('camera-container');
  container.innerHTML = '';
  navigator.mediaDevices.getUserMedia({ video: true, audio: true }).then(stream => {
    const video = document.createElement('video');
    video.autoplay = true;
    video.srcObject = stream;
    container.appendChild(video);
    const recBtn = document.createElement('button');
    recBtn.textContent = 'Start Recording';
    recBtn.style.marginTop = '1em';
    container.appendChild(recBtn);
    let mediaRecorder;
    let chunks = [];
    let recording = false;
    let timer;
    recBtn.onclick = function() {
      if (!recording) {
        mediaRecorder = new MediaRecorder(stream);
        mediaRecorder.ondataavailable = function(e) {
          chunks.push(e.data);
        };
        mediaRecorder.onstop = function() {
          const blob = new Blob(chunks, { type: 'video/webm' });
          const url = URL.createObjectURL(blob);
          const recordedVideo = document.createElement('video');
          recordedVideo.controls = true;
          recordedVideo.src = url;
          container.innerHTML = '';
          container.appendChild(recordedVideo);
          stopCamera();
        };
        mediaRecorder.start();
        recording = true;
        recBtn.textContent = 'Stop Recording';
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
      }
    };
  }).catch(err => {
    container.innerHTML = '<p>Camera access denied or not available.</p>';
  });
});

document.querySelectorAll('.nav-item').forEach(function(item) {
  if (item.textContent.includes('Home')) {
    item.addEventListener('click', function() {
      // Hide all modals and overlays
      document.getElementById('search-modal').style.display = 'none';
      document.getElementById('video-modal').style.display = 'none';
      // Optionally clear any dynamic content
      document.getElementById('feature-content').innerHTML = '';
      // Scroll to top (for mobile)
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }
}); 

// Live server reconnect logic
// IMPORTANT: If you want to access the backend from your phone, use your computer's local IP address below (e.g., http://10.39.16.146:5000)
const API_BASE_URL = "http://localhost:5000";
document.getElementById('reconnect-btn').addEventListener('click', function() {
  fetch(`${API_BASE_URL}/children`)
    .then(response => {
      if (!response.ok) throw new Error('Server responded with ' + response.status);
      return response.json();
    })
    .then(data => {
      alert('Reconnected! Data loaded from live server.');
      // Optionally update your UI with the data
      // console.log(data);
    })
    .catch(err => {
      alert('Failed to reconnect: ' + err.message);
    });
}); 
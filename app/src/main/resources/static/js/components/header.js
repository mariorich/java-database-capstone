// header.js
import { openModal } from "./modals.js";

export function renderHeader() {
  const headerDiv = document.getElementById("header");

  if (!headerDiv) return;

  // If at root, reset role
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>
  `;

  // Define buttons by role
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn">Add Doctor</button>
      <button id="logoutBtn" class="adminBtn">Logout</button>
    `;
  } else if (role === "doctor") {
    headerContent += `
      <button id="doctorHomeBtn" class="adminBtn">Home</button>
      <button id="logoutBtn" class="adminBtn">Logout</button>
    `;
  } else if (role === "patient") {
    headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>
    `;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="homeBtn" class="adminBtn">Home</button>
      <button id="patientAppointmentsBtn" class="adminBtn">Appointments</button>
      <button id="logoutPatientBtn" class="adminBtn">Logout</button>
    `;
  }

  headerContent += `
      </nav>
    </header>
  `;

  // Inject header HTML
  headerDiv.innerHTML = headerContent;

  // Attach button listeners
  attachHeaderListeners(role);
}

function attachHeaderListeners(role) {
  // Admin
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) addDocBtn.addEventListener("click", () => openModal("addDoctor"));

  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) logoutBtn.addEventListener("click", logout);

  // Doctor
  const doctorHomeBtn = document.getElementById("doctorHomeBtn");
  if (doctorHomeBtn) doctorHomeBtn.addEventListener("click", () => {
    window.location.href = "/pages/doctorDashboard.html";
  });

  // Patient login/signup
  const patientLoginBtn = document.getElementById("patientLogin");
  if (patientLoginBtn) patientLoginBtn.addEventListener("click", () => openModal("patientLogin"));

  const patientSignupBtn = document.getElementById("patientSignup");
  if (patientSignupBtn) patientSignupBtn.addEventListener("click", () => openModal("patientSignup"));

  // Logged patient
  const homeBtn = document.getElementById("homeBtn");
  if (homeBtn) homeBtn.addEventListener("click", () => {
    window.location.href = "/pages/loggedPatientDashboard.html";
  });

  const patientAppointmentsBtn = document.getElementById("patientAppointmentsBtn");
  if (patientAppointmentsBtn) patientAppointmentsBtn.addEventListener("click", () => {
    window.location.href = "/pages/patientAppointments.html";
  });

  const logoutPatientBtn = document.getElementById("logoutPatientBtn");
  if (logoutPatientBtn) logoutPatientBtn.addEventListener("click", logoutPatient);
}

// Logout functions
function logout() {
  localStorage.removeItem("userRole");
  localStorage.removeItem("token");
  window.location.href = "/";
}

function logoutPatient() {
  localStorage.removeItem("token");
  window.location.href = "/pages/patientDashboard.html";
}

// Automatically render the header
renderHeader();

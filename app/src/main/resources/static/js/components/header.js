// header.js

// Render the header based on user role and session
function renderHeader() {
    const headerDiv = document.getElementById("header");

    // If root page, reset session and render basic header
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

    // Get role and token from localStorage
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Basic header content with logo
    let headerContent = `
        <header class="header">
            <div class="logo-section">
                <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                <span class="logo-title">Hospital CMS</span>
            </div>
            <nav>`;

    // Session expired or invalid login
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/"; // or specific login page
        return;
    }

    // Role-specific buttons
    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
            <a href="#" onclick="logout()">Logout</a>`;
    } else if (role === "doctor") {
        headerContent += `
            <button class="adminBtn" onclick="selectRole('doctor')">Home</button>
            <a href="#" onclick="logout()">Logout</a>`;
    } else if (role === "patient") {
        headerContent += `
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>`;
    } else if (role === "loggedPatient") {
        headerContent += `
            <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
            <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
            <a href="#" onclick="logoutPatient()">Logout</a>`;
    }

    // Close nav and header
    headerContent += `
            </nav>
        </header>`;

    // Insert into the DOM
    headerDiv.innerHTML = headerContent;

    // Attach event listeners to dynamically created buttons
    attachHeaderButtonListeners();
}

// Attach event listeners for header buttons
function attachHeaderButtonListeners() {
    const loginBtn = document.getElementById("patientLogin");
    const signupBtn = document.getElementById("patientSignup");

    if (loginBtn) {
        loginBtn.addEventListener("click", () => openModal("patientLogin"));
    }
    if (signupBtn) {
        signupBtn.addEventListener("click", () => openModal("patientSignup"));
    }
}

// Logout for admin or doctor
function logout() {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    window.location.href = "/";
}

// Logout for patient
function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/pages/loggedPatientDashboard.html";
}

// Initialize header on page load
renderHeader();

// render.js
import { getRole, setRole } from './util.js';
import { openModal } from './services/modal.js';

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('adminButton').addEventListener('click', () => selectRole('admin'));
    document.getElementById('doctorButton').addEventListener('click', () => selectRole('doctor'));
    document.getElementById('patientButton').addEventListener('click', () => selectRole('patient'));

    renderContent();
});

function selectRole(role) {
    setRole(role);
    const token = localStorage.getItem('token');

    if (role === "admin" && token) {
        window.location.href = `/adminDashboard/${token}`;
    } else if (role === "doctor" && token) {
        window.location.href = `/doctorDashboard/${token}`;
    } else if (role === "patient") {
        window.location.href = "/pages/patientDashboard.html";
    } else {
        openModal("Please log in first.");
    }
}

function renderContent() {
    const role = getRole();

    if (window.location.pathname !== '/' && !role) {
        window.location.href = "/";
    }
}

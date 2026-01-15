// render.js
import { getRole, setRole } from './util.js';
import { openModal } from './services/modal.js';

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('adminLogin').addEventListener('click', () => selectRole('admin'));
    document.getElementById('doctorLogin').addEventListener('click', () => selectRole('doctor'));
    document.getElementById('patientLogin').addEventListener('click', () => selectRole('patient'));

    renderContent();
});

function selectRole(role) {
    setRole(role);
    const token = localStorage.getItem('token');

    if (role === "admin" && token) {
        window.location.href = `/adminDashboard/${token}`;
    } else if (role === "doctor" && token) {
        window.location.href = `/doctorDashboard/${token}`;
    } else if (role === "patient" && token) {
        window.location.href = "/pages/patientDashboard.html";
    } else {
        if (role === "admin") openModal("adminLogin");
        if (role === "doctor") openModal("doctorLogin");
        if (role === "patient") openModal("patientLogin");
    }
}


function renderContent() {
    const role = getRole();

    if (window.location.pathname !== '/' && !role) {
        window.location.href = "/";
    }
}

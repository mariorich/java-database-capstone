// doctorDashboard.js

import { getAllAppointments } from './appointmentServices.js';
import { createPatientRow } from './patientRow.js';

// DOM elements
const tableBody = document.getElementById('patientTableBody');
const searchBar = document.getElementById('searchBar');
const todayButton = document.getElementById('todayButton');
const datePicker = document.getElementById('datePicker');

// Initial state
let selectedDate = new Date().toISOString().split('T')[0]; // today's date (YYYY-MM-DD)
let token = localStorage.getItem('token');
let patientName = 'null'; // used for filtering by patient name

// =======================
// SEARCH BAR FILTER
// =======================
if (searchBar) {
  searchBar.addEventListener('input', (e) => {
    const value = e.target.value.trim();
    patientName = value !== '' ? value : 'null';
    loadAppointments();
  });
}

// =======================
// TODAY BUTTON CLICK
// =======================
if (todayButton) {
  todayButton.addEventListener('click', () => {
    selectedDate = new Date().toISOString().split('T')[0];
    if (datePicker) datePicker.value = selectedDate;
    loadAppointments();
  });
}

// =======================
// DATE PICKER CHANGE
// =======================
if (datePicker) {
  datePicker.addEventListener('change', (e) => {
    selectedDate = e.target.value;
    loadAppointments();
  });
}

// =======================
// LOAD APPOINTMENTS
// =======================
async function loadAppointments() {
  if (!tableBody) return;

  tableBody.innerHTML = `
    <tr>
      <td colspan="5" style="text-align: center;">Loading appointments...</td>
    </tr>
  `;

  try {
    const data = await getAllAppointments(selectedDate, patientName, token);
    tableBody.innerHTML = '';

    const appointments = data.appointments || [];

    if (appointments.length === 0) {
      tableBody.innerHTML = `
        <tr>
          <td colspan="5" style="text-align: center;">No Appointments found for today.</td>
        </tr>
      `;
      return;
    }

    // Render each appointment as a patient row
    appointments.forEach((appointment) => {
      const patient = {
        id: appointment.patientId,
        name: appointment.patientName,
        phone: appointment.patientPhone,
        email: appointment.patientEmail,
        prescription: appointment.prescription,
      };

      const row = createPatientRow(patient);
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error('Error loading appointments:', error);
    tableBody.innerHTML = `
      <tr>
        <td colspan="5" style="text-align: center;">Error loading appointments. Try again later.</td>
      </tr>
    `;
  }
}

// =======================
// PAGE LOAD INITIALIZATION
// =======================
document.addEventListener('DOMContentLoaded', async () => {
  renderContent(); // assumed function sets up header/footer layout
  await loadAppointments(); // load today's appointments by default
});

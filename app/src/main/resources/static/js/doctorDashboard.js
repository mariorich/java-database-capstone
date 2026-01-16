import { getAllAppointments } from "../services/appointmentServices.js";
import { createPatientRow } from "../components/patientRow.js";

const tableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0]; // Default to today
const token = localStorage.getItem("token");
let patientName = null;

// Search bar input listener
document.getElementById("searchBar").addEventListener("input", (e) => {
  const input = e.target.value.trim();
  patientName = input.length > 0 ? input : null;
  loadAppointments();
});

// "Today" button click listener
document.getElementById("todayBtn").addEventListener("click", () => {
  selectedDate = new Date().toISOString().split("T")[0];
  document.getElementById("datePicker").value = selectedDate;
  loadAppointments();
});

// Date picker change listener
document.getElementById("datePicker").addEventListener("change", (e) => {
  selectedDate = e.target.value;
  loadAppointments();
});

async function loadAppointments() {
  try {
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    tableBody.innerHTML = ""; // Clear existing rows

    if (!appointments || appointments.length === 0) {
      tableBody.innerHTML = `<tr><td class="noPatientRecord" colspan='5'>No Appointments found for today.</td></tr>`;
      return;
    }

    appointments.forEach(appointment => {
      const patient = {
        id: appointment.patientId,
        name: appointment.patientName,
        phone: appointment.patientPhone,
        email: appointment.patientEmail
      };
      const row = createPatientRow(appointment, patient);
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    tableBody.innerHTML = `<tr><td class="noPatientRecord" colspan='5'>Error loading appointments. Try again later.</td></tr>`;
  }
}

document.addEventListener("DOMContentLoaded", () => {
  renderContent(); // Assuming this function sets up the UI layout
  loadAppointments(); // Load today's appointments by default
});
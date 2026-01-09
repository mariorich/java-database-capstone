// adminDashboard.js

import { openModal } from './modal.js';
import { getDoctors, filterDoctors, saveDoctor } from './doctorServices.js';
import { createDoctorCard } from './doctorCard.js';

// =======================
// EVENT LISTENERS
// =======================

// When "Add Doctor" button is clicked → open the modal
document.addEventListener('click', (event) => {
  if (event.target && event.target.id === 'addDocBtn') {
    openModal('addDoctor');
  }
});

// When the DOM is loaded → load all doctor cards
document.addEventListener('DOMContentLoaded', () => {
  loadDoctorCards();

  // Attach filter listeners
  const searchBar = document.getElementById('searchBar');
  const timeFilter = document.getElementById('timeFilter');
  const specialtyFilter = document.getElementById('specialtyFilter');

  if (searchBar) searchBar.addEventListener('input', filterDoctorsOnChange);
  if (timeFilter) timeFilter.addEventListener('change', filterDoctorsOnChange);
  if (specialtyFilter) specialtyFilter.addEventListener('change', filterDoctorsOnChange);
});

// =======================
// LOAD ALL DOCTOR CARDS
// =======================
async function loadDoctorCards() {
  const contentDiv = document.getElementById('content');
  if (!contentDiv) return;

  contentDiv.innerHTML = '<p>Loading doctors...</p>';

  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error('Error loading doctors:', error);
    contentDiv.innerHTML = '<p>Error loading doctor list.</p>';
  }
}

// =======================
// FILTER DOCTORS
// =======================
async function filterDoctorsOnChange() {
  const searchBar = document.getElementById('searchBar');
  const timeFilter = document.getElementById('timeFilter');
  const specialtyFilter = document.getElementById('specialtyFilter');
  const contentDiv = document.getElementById('content');

  const name = searchBar?.value.trim() || 'null';
  const time = timeFilter?.value || 'null';
  const specialty = specialtyFilter?.value || 'null';

  contentDiv.innerHTML = '<p>Filtering doctors...</p>';

  try {
    const data = await filterDoctors(name, time, specialty);
    const doctors = data.doctors || [];

    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      contentDiv.innerHTML = '<p>No doctors found with the given filters.</p>';
    }
  } catch (error) {
    console.error('Error filtering doctors:', error);
    alert('An error occurred while filtering doctors.');
  }
}

// =======================
// RENDER DOCTOR CARDS
// =======================
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById('content');
  contentDiv.innerHTML = '';

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// =======================
// ADD DOCTOR (ADMIN)
// =======================
export async function adminAddDoctor() {
  const name = document.getElementById('doctorName')?.value.trim();
  const email = document.getElementById('doctorEmail')?.value.trim();
  const phone = document.getElementById('doctorPhone')?.value.trim();
  const password = document.getElementById('doctorPassword')?.value.trim();
  const specialty = document.getElementById('doctorSpecialty')?.value.trim();
  const availableTimes = document.getElementById('doctorAvailableTimes')?.value.trim();

  if (!name || !email || !phone || !password || !specialty || !availableTimes) {
    alert('Please fill in all fields before adding a doctor.');
    return;
  }

  const token = localStorage.getItem('token');
  if (!token) {
    alert('Admin not authenticated. Please log in again.');
    return;
  }

  const doctor = {
    name,
    email,
    phone,
    password,
    specialization: specialty,
    availableTimes: availableTimes.split(',').map((t) => t.trim()),
  };

  try {
    const result = await saveDoctor(doctor, token);

    if (result.success) {
      alert('Doctor added successfully!');
      closeModal(); // Assuming a global closeModal() exists
      loadDoctorCards(); // Refresh the doctor list
    } else {
      alert(`Failed to add doctor: ${result.message}`);
    }
  } catch (error) {
    console.error('Error adding doctor:', error);
    alert('An error occurred while adding the doctor.');
  }
}

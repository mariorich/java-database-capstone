import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { openModal } from "./components/modals.js";

// Add Doctor Button
document.getElementById("addDoctorBtn").addEventListener("click", () => {
  openModal('addDoctor');
});

// Load all doctors on page load
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    doctors.forEach(doctor => {
      const card = createDoctorCard(doctor);
      contentDiv.appendChild(card);
    });
  } catch (error) {
    console.error("Failed to load doctors:", error);
  }
}
// Filter Input
document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("timeFilter").addEventListener("change", filterDoctorsOnChange);
document.getElementById("specialtyFilter").addEventListener("change", filterDoctorsOnChange);

async function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar").value.trim();
  const timeFilter = document.getElementById("timeFilter").value;
  const filterSpecialty = document.getElementById("specialtyFilter").value;

  const name = searchBar.length > 0 ? searchBar : null;
  const time = timeFilter.length > 0 ? timeFilter : null;
  const specialty = filterSpecialty.length > 0 ? filterSpecialty : null;

  try {
    const response = await filterDoctors(name, time, specialty);
    const doctors = response.doctors;
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    if (doctors.length > 0) {
      console.log(doctors);
      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    } else {
      contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
      console.log("Nothing");
    }
  } catch (error) {
    console.error("Failed to filter doctors:", error);
    alert("❌ An error occurred while filtering doctors.");
  }
}

export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

window.adminAddDoctor = async function () {
  const name = document.getElementById("doctorName").value;
  const email = document.getElementById("doctorEmail").value;
  const phone = document.getElementById("doctorPhone").value;
  const password = document.getElementById("doctorPassword").value;
  const specialty = document.getElementById("doctorSpecialty").value;
  const availableTimesSelect = document.getElementById("doctorAvailableTimes");
  const availableTimes = Array.from(availableTimesSelect.selectedOptions).map(option => option.value);

  const token = localStorage.getItem("token");
  if (!token) {
    alert("❌ No authentication token found. Please log in again.");
    return;
  }

  const doctor = {
    name,
    email,
    phone,
    password,
    specialty,
    availableTimes
  };

  try {
    const response = await saveDoctor(doctor, token);
    if (response.success) {
      alert("✅ Doctor added successfully.");
      document.getElementById("modalApp").remove();
      window.location.reload();
    } else {
      alert("❌ Failed to add doctor: " + response.message);
    }
  } catch (error) {
    console.error("Error adding doctor:", error);
    alert("An error occurred while adding the doctor.");
  }
}
import { showBookingOverlay } from "../loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { fetchPatientDetails } from "../services/patientServices.js";

export function createDoctorCard(doctor) {
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  const userRole = localStorage.getItem("role");

  const doctorInfo = document.createElement("div");
  doctorInfo.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialty = document.createElement("p");
  specialty.textContent = `Specialty: ${doctor.specialty}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const times = document.createElement("p");
  times.textContent = `Available Times: ${doctor.availableTimes.join(", ")}`;

  doctorInfo.appendChild(name);
  doctorInfo.appendChild(specialty);
  doctorInfo.appendChild(email);
  doctorInfo.appendChild(times);

  const actionButtons = document.createElement("div");
  actionButtons.classList.add("action-buttons");

  if (userRole === "admin") {
    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "Delete";
    deleteBtn.classList.add("delete-button");

    deleteBtn.addEventListener("click", async () => {
      const token = localStorage.getItem("token");
      try {
        const response = await deleteDoctor(doctor.id, token);
        if (response.success) {
          alert("Doctor deleted successfully.");
          card.remove();
        } else {
          alert("Failed to delete doctor: " + response.message);
        }
      } catch (error) {
        console.error("Error deleting doctor:", error);
        alert("An error occurred while deleting the doctor.");
      }
    });

    actionButtons.appendChild(deleteBtn);
  } else if (userRole === "patient") {
    const bookBtn = document.createElement("button");
    bookBtn.textContent = "Book Now";
    bookBtn.classList.add("book-button");

    bookBtn.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Please log in to book an appointment.");
        window.location.href = "/login.html";
        return;
      }

      try {
        const patient = await fetchPatientDetails(token);
        showBookingOverlay(e, doctor, patient);
      } catch (error) {
        console.error("Error fetching patient details:", error);
        alert("An error occurred while fetching your details. Please try again.");
      }
    });

    actionButtons.appendChild(bookBtn);
  } else {
    const bookBtn = document.createElement("button");
    bookBtn.textContent = "Book Now";
    bookBtn.classList.add("book-button");

    bookBtn.addEventListener("click", () => {
      alert("Please log in as a patient to book an appointment.");
    });

    actionButtons.appendChild(bookBtn);
  }

  card.appendChild(doctorInfo);
  card.appendChild(actionButtons);

  return card;
}
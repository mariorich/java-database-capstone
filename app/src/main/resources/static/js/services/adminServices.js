import { saveDoctor } from "./doctorServices.js";

export async function adminAddDoctor() {
  const name = document.getElementById("doctorName").value;
  const email = document.getElementById("doctorEmail").value;
  const phone = document.getElementById("doctorPhone").value;
  const password = document.getElementById("doctorPassword").value;
  const specialty = document.getElementById("doctorSpecialty").value;
  const availableTimes = Array.from(
  document.querySelectorAll('#doctorAvailableTimes input:checked')
  ).map(cb => cb.value);

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
      document.getElementById("modal").remove();
      window.location.reload();
    } else {
      alert("❌ Failed to add doctor: " + response.message);
    }
  } catch (error) {
    console.error("Error adding doctor:", error);
    alert("An error occurred while adding the doctor.");
  }
}
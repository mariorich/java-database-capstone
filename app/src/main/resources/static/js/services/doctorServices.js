import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = `${API_BASE_URL}/doctor`;
const ADMIN_API = `${API_BASE_URL}/admin`;

export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    const data = await response.json();
    return data.doctors;
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

export async function deleteDoctor(doctorId, token) {
    try {
      const response = await fetch(`${ADMIN_API}/delete/${doctorId}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
  
      const text = await response.text();
  
      let data;
      try {
        data = JSON.parse(text);
      } catch {
        data = { success: false, message: text };
      }
  
      return data;
    } catch (error) {
      console.error("Error deleting doctor:", error);
      return {
        success: false,
        message: "An error occurred while deleting the doctor."
      };
    }
  }

export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${ADMIN_API}/add`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(doctor),
    });

    const text = await response.text();

    let data;
    try {
    data = JSON.parse(text);
    } catch {
    data = { success: false, message: text };
    }

    return {
      success: data.success,
      message: data.message,
    };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return {
      success: false,
      message: "An error occurred while saving the doctor.",
    };
  }
}

export async function filterDoctors(name, time, specialty) {
  try {
    const response = await fetch(
      `${DOCTOR_API}/filter/${name}/${time}/${specialty}`
    );

    if (response.ok) {
      const data = await response.json();
      return data;
    } else {
      console.error("Error filtering doctors:", response.statusText);
      return { doctors: [] };
    }
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("An error occurred while filtering doctors. Please try again later.");
    return { doctors: [] };
  }
}
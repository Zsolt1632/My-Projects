<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="edu.bbte.idde.mzim2273.data.model.Hike" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/style.css">
    <title>Hike List</title>
</head>
<body>
<p><a href="${pageContext.request.contextPath}/">Home</a></p>
<p><a href="${pageContext.request.contextPath}/login">Login</a></p>
<h1>Hikes</h1>

<%
    // Retrieve the hikes attribute
    List<Hike> hikes = (List<Hike>) request.getAttribute("hikes");
    if (hikes != null && !hikes.isEmpty()) {
%>
<ul>
    <% for (Hike hike : hikes) { %>
    <li>
        <h2><%= hike.getName() %></h2>
        <p><strong>Location:</strong> <%= hike.getStartLocation() %></p>
        <p><strong>Date:</strong> <%= hike.getStartDate() %></p>
        <p><strong>Time:</strong> <%= hike.getStartTime() %></p>
        <p><strong>Price:</strong> <%= hike.getPrice() %></p>

        <!-- View Details link -->
        <p><a href="<%= request.getContextPath() %>/hikes?id=<%= hike.getId() %>">View Details</a></p>

        <!-- Form for Deleting the hike -->
        <form id="deleteForm_<%= hike.getId() %>" data-id="<%= hike.getId() %>" onsubmit="return confirm('Are you sure you want to delete this hike?');">
            <button type="submit" name="_method" value="DELETE">Delete</button>
        </form>

        <!-- Form for Updating the hike -->
        <form id="updateForm_<%= hike.getId() %>" data-id="<%= hike.getId() %>" style="display:none;">
            <label for="name_<%= hike.getId() %>">Name:</label>
            <input type="text" id="name_<%= hike.getId() %>" name="name" value="<%= hike.getName() %>" required><br>
            <label for="location_<%= hike.getId() %>">Location:</label>
            <input type="text" id="location_<%= hike.getId() %>" name="startLocation" value="<%= hike.getStartLocation() %>" required><br>
            <label for="date_<%= hike.getId() %>">Date:</label>
            <input type="date" id="date_<%= hike.getId() %>" name="startDate" value="<%= hike.getStartDate() %>" required><br>
            <label for="time_<%= hike.getId() %>">Time:</label>
            <input type="time" id="time_<%= hike.getId() %>" name="startTime" value="<%= hike.getStartTime() %>" required><br>
            <label for="price_<%= hike.getId() %>">Price:</label>
            <input type="number" id="price_<%= hike.getId() %>" name="price" value="<%= hike.getPrice() %>" required><br>
            <button type="submit">Update</button>
            <button type="button" onclick="cancelEdit(<%= hike.getId() %>)">Cancel</button>
        </form>

        <button onclick="editHike(<%= hike.getId() %>)">Edit</button>
    </li>
    <% } %>
</ul>
<%
} else {
%>
<p>No hikes available.</p>
<%
    }
%>

<!-- Form for Adding a New Hike -->
<h2>Add New Hike</h2>
<form id="hikeForm">
    <label for="name">Name:</label>
    <input type="text" id="name" name="name" required><br>

    <label for="location">Location:</label>
    <input type="text" id="location" name="startLocation" required><br>

    <label for="date">Date:</label>
    <input type="date" id="date" name="startDate" required><br>

    <label for="time">Time:</label>
    <input type="time" id="time" name="startTime" required><br>

    <label for="price">Price:</label>
    <input type="number" id="price" name="price" required><br>

    <button type="submit">Add Hike</button>
</form>

<script>
    document.getElementById('hikeForm').addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent default form submission

        const formData = new FormData(event.target);
        const hikeData = {};

        // Convert form data to an object
        formData.forEach((value, key) => {
            hikeData[key] = value;
        });

        // Send the data as JSON using fetch
        fetch('<%= request.getContextPath() %>/hikes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(hikeData)
        })
            .then(response => response.json())
            .then(data => {
                console.log('Success:', data);
                loadHikeList(); // Reload the hike list
            })
            .catch((error) => {
                console.error('Error:', error);
            });
    });

    function editHike(hikeId) {
        const id = 'updateForm_' + hikeId;
        // Find the update form using the correct ID
        const updateForm = document.getElementById(id);
        if (updateForm) {
            updateForm.style.display = 'block'; // Show the form
        } else {
            console.error('Update form not found for hike ID:', hikeId);
        }
    }

    function cancelEdit(hikeId) {
        const id = 'updateForm_' + hikeId;
        const updateForm = document.getElementById(id);
        if (updateForm) {
            updateForm.style.display = 'none'; // Hide the form
        }
    }

    // Update Form Submission
    const updateForms = document.querySelectorAll('form[id^="updateForm_"]');
    updateForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            const hikeId = form.getAttribute('data-id');
            const hikeData = { id: hikeId };

            // Add form data to the hikeData object
            const formData = new FormData(form);
            formData.forEach((value, key) => {
                hikeData[key] = value;
            });

            // Send the PUT request
            fetch('<%= request.getContextPath() %>/hikes?id=' + hikeId, {
                method: 'POST', // Still POST with override
                headers: {
                    'Content-Type': 'application/json',
                    'X-HTTP-Method-Override': 'PUT',
                },
                body: JSON.stringify(hikeData),
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Update failed');
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Updated:', data);
                    loadHikeList(); // Reload the hike list
                })
                .catch((error) => {
                    console.error('Error updating hike:', error);
                });
        });
    });

    // Delete Form Submission
    const deleteForms = document.querySelectorAll('form[id^="deleteForm_"]');
    deleteForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            event.preventDefault(); // Prevent default form submission

            const hikeId = form.getAttribute('data-id');
            console.log(hikeId);

            // Send the DELETE request
            fetch('<%= request.getContextPath() %>/hikes?id=' + hikeId, {
                method: 'POST', // Simulate DELETE method using POST
                headers: {
                    'Content-Type': 'application/json',
                    'X-HTTP-Method-Override': 'DELETE',
                }
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Deleted:', data);
                    loadHikeList(); // Reload the hike list
                })
                .catch((error) => {
                    console.error('Error deleting hike:', error);
                });
        });
    });

    // Function to reload the hike list after any update or delete
    function loadHikeList() {
        fetch('<%= request.getContextPath() %>/hikes')
            .then(response => response.text())
            .then(html => {
                const hikeListContainer = document.querySelector('ul');
                // Only replace the list items inside the <ul> element
                const newList = document.createElement('ul');
                newList.innerHTML = html;
                const newListItems = newList.querySelectorAll('li');
                hikeListContainer.innerHTML = ''; // Clear the existing list
                newListItems.forEach(item => {
                    hikeListContainer.appendChild(item); // Append each new list item
                });
            });
    }

</script>
</body>
</html>

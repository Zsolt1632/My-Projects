<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="edu.bbte.idde.mzim2273.data.model.Hike" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/style.css">
    <title>Hike Details</title>
</head>
<body>
<%
    Hike hike = (Hike) request.getAttribute("hike");
    if (hike != null) {
%>
<h1><%= hike.getName() %></h1>
<p><strong>Location:</strong> <%= hike.getStartLocation() %></p>
<p><strong>Date:</strong> <%= hike.getStartDate() %></p>
<p><strong>Time:</strong> <%= hike.getStartTime() %></p>
<p><strong>Price:</strong> <%= hike.getPrice() %></p>
<%
} else {
%>
<p>Hike details not available.</p>
<%
    }
%>
</body>
</html>

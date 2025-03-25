package edu.bbte.idde.mzim2273.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.bbte.idde.mzim2273.business.service.HikeServiceImpl;
import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.business.service.exception.MissingArgumentException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.model.Hike;
import edu.bbte.idde.mzim2273.error.ErrorResponse;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/hikes")
public class HikeTemplateServlet extends HttpServlet {

    private static final Boolean archived = false;
    private static final Logger LOG = LoggerFactory.getLogger(HikeTemplateServlet.class);

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // Register module for Java 8 time support
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Use ISO-8601 format

    private transient HikeServiceImpl hikeService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        hikeService = HikeServiceImpl.getInstance();
        LOG.info("HikeServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        LOG.info("id = {}", idParam);
        try {
            if (idParam != null) {
                long id = Long.parseLong(idParam);
                Hike hike = hikeService.getHike(id, archived);
                request.setAttribute("hike", hike);
                request.getRequestDispatcher("/hike-detail.jsp").forward(request, response);
            } else {
                List<Hike> hikes = (List<Hike>) hikeService.getAllHikes(archived);
                request.setAttribute("hikes", hikes);
                request.getRequestDispatcher("/hike-list.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            LOG.error("Invalid hike ID format", e);
            writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid hike ID format");
        } catch (RepositoryException | EntityNotFoundException e) {
            writeErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Hike not found");
        } catch (IOException | ServletException e) {
            writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String methodOverride = request.getHeader("X-HTTP-Method-Override");
        if ("DELETE".equalsIgnoreCase(methodOverride)) {
            doDelete(request, response);
        } else if ("PUT".equalsIgnoreCase(methodOverride)) {
            doPut(request, response);
        } else {
            try {
                Hike hike = objectMapper.readValue(request.getReader(), Hike.class);
                if (hike != null && hike.getName() != null && hike.getStartLocation() != null) {
                    hikeService.addHike(hike);
                    writeResponse(response, HttpServletResponse.SC_CREATED, "Hike created successfully");
                }
            } catch (MissingArgumentException | RepositoryException e) {
                LOG.error("Error in POST /hikes", e);
                writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid hike data");
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Hike ID is required");
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            Hike existingHike = hikeService.getHikeById(id, archived);

            updateHikeFields(request, existingHike);
            hikeService.updateHike(existingHike, archived);
            writeResponse(response, HttpServletResponse.SC_OK, "Hike updated successfully");
        } catch (RepositoryException | EntityNotFoundException e) {
            writeErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Hike not found");
        } catch (NumberFormatException e) {
            writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid hike ID format");
        }
    }

    private void updateHikeFields(HttpServletRequest request, Hike hike) throws IOException {
        ObjectNode requestBody = (ObjectNode) objectMapper.readTree(request.getInputStream());

        if (requestBody.has("name")) {
            hike.setName(requestBody.get("name").asText());
        }
        if (requestBody.has("startLocation")) {
            hike.setStartLocation(requestBody.get("startLocation").asText());
        }
        if (requestBody.has("startDate")) {
            hike.setStartDate(LocalDate.parse(requestBody.get("startDate").asText()));
        }
        if (requestBody.has("startTime")) {
            hike.setStartTime(LocalTime.parse(requestBody.get("startTime").asText()));
        }
        if (requestBody.has("price")) {
            hike.setPrice(requestBody.get("price").asDouble());
        }
    }


    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        try {
            if (idParam == null || idParam.isEmpty()) {
                writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Hike ID is required");
                return;
            }

            long id = Long.parseLong(idParam);

            hikeService.deleteHike(id, archived);
            writeResponse(response, HttpServletResponse.SC_OK, "Hike deleted successfully");
        } catch (RepositoryException | EntityNotFoundException e) {
            LOG.error("Error in DELETE /hikes", e);
            writeErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Hike not found");
        }
    }

    private void writeResponse(HttpServletResponse response, int status, Object data) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), data);
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String errorMessage) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(errorMessage));
    }
}
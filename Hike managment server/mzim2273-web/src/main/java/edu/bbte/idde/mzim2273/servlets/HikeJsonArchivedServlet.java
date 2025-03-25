package edu.bbte.idde.mzim2273.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.bbte.idde.mzim2273.business.service.HikeServiceImpl;
import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
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
import java.util.List;

@WebServlet("/archived")
public class HikeJsonArchivedServlet extends HttpServlet {
    private static final Boolean archived = true;
    private static final Logger LOG = LoggerFactory.getLogger(HikeJsonServlet.class);

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
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            if (idParam != null) {
                long id = Long.parseLong(idParam);
                Hike hike = hikeService.getHike(id, archived);
                request.setAttribute("hike", hike);
                writeResponse(response, HttpServletResponse.SC_OK, hike);
            } else {
                List<Hike> hikes = (List<Hike>) hikeService.getAllHikes(archived);
                request.setAttribute("hikes", hikes);
                writeResponse(response, HttpServletResponse.SC_OK, hikes);
            }
        } catch (NumberFormatException e) {
            LOG.error("Invalid hike ID format", e);
            writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid hike ID format");
        } catch (RepositoryException | EntityNotFoundException e) {
            writeErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Hike not found");
        } catch (IOException e) {
            writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server error");
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        LOG.info("id = {}", idParam);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            if (idParam == null || idParam.isEmpty()) {
                writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Hike ID is required");
                return;
            }

            Hike hike = getHike(Long.parseLong(idParam));
            if (hike == null) {
                writeErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Hike not found");
                return;
            }

            hike.setArchived(!hike.getArchived());
            hikeService.updateHike(hike, !hike.getArchived());
            writeResponse(response, HttpServletResponse.SC_OK, hike);
        } catch (EntityNotFoundException | RepositoryException e) {
            LOG.error("Error in POST /archived", e);
            writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid hike data");
        } catch (NumberFormatException e) {
            LOG.error("Invalid hike ID format", e);
            writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid hike ID format");
        }
    }


    private Hike getHike(long idParam) {
        try {
            return hikeService.getHikeById(idParam, archived);
        } catch (EntityNotFoundException | RepositoryException e) {
            LOG.warn("Hike {} not found in archived list, checking unarchived...", idParam);
            try {
                return hikeService.getHikeById(idParam, !archived);
            } catch (EntityNotFoundException | RepositoryException ex) {
                LOG.error("Hike {} not found in both archived and unarchived lists.", idParam);
                return null;
            }
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


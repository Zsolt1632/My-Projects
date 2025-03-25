package edu.bbte.idde.mzim2273.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/hikes/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            HttpSession session = httpRequest.getSession(false);

            // Redirect to the login servlet if the user is not authenticated
            if (session == null || session.getAttribute("user") == null) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            } else {
                chain.doFilter(request, response);
            }
        } else {
            // Handle the case where the request/response are not of the expected type
            throw new ServletException("Unexpected request or response type.");
        }
    }

    @Override
    public void destroy() {}
}


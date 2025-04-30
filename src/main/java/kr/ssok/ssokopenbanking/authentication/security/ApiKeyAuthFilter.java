//package kr.ssok.ssokopenbanking.authentication.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import kr.ssok.ssokopenbanking.authentication.repository.ApiKeyRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class ApiKeyAuthFilter extends OncePerRequestFilter {
//
//    private final ApiKeyRepository apiKeyRepository;
//
//    private static final String API_KEY_HEADER = "X-API-KEY";
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String path = request.getRequestURI();
//
//        // 인증을 우회할 경로
//        if ("/api/openbank/openapikey".equals(path)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String apiKey = request.getHeader(API_KEY_HEADER);
//
//        if (apiKey == null || !isValidApiKey(apiKey)) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Invalid or missing API Key");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private boolean isValidApiKey(String apiKey) {
//        return apiKeyRepository.findById(apiKey).isPresent();
//    }
//
//}

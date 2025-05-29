package kr.ssok.ssokopenbanking.opensearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/alerts")
public class TestAlertController {

    @PostMapping
    public ResponseEntity<Void> receiveAlert(@RequestBody(required = false) Map<String, Object> body) {
        try {
            if (body == null) {
                log.warn("[ALERT] Request body is null.");
            } else {
                Object message = body.get("message");
                log.warn("[ALERT] {}", message != null ? message.toString() : "message field is missing.");
            }
        } catch (Exception e) {
            log.error("[ALERT] Failed to process alert payload.", e);
        }

        return ResponseEntity.ok().build(); // 항상 200 OK 반환
    }
}